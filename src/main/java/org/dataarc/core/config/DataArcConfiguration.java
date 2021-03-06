package org.dataarc.core.config;

import java.util.Properties;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.rollbar.Rollbar;

/**
 * Basic app configuration
 * 
 * @author abrin
 *
 */
@Configuration
@EnableTransactionManagement
@EnableAsync
@ComponentScan(basePackages = { DataArcConfiguration.ORG_DATAARC_CORE, MongoProfile.ORG_DATAARC_MONGO, DataArcConfiguration.ORG_DATAARC_SOLR })
@PropertySource(ignoreResourceNotFound = true, value = "classpath:dataarc.properties")
@PropertySource(ignoreResourceNotFound = true, value = "classpath:crowd.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DataArcConfiguration {

    private static final String LOCALHOST = "localhost";
    private static final String DATAARC = "dataarc";
    private static final String JDBC_POSTGRESQL_POST_GIS_S_5432_S = "jdbc:postgresql_postGIS://%s:5432/%s";
    private static final String PG_PASSWORD = "pgPassword";
    private static final String PG_USERNAME = "pgUsername";
    private static final String PG_DATABASE = "pgDatabase";
    private static final String PG_HOST = "pgHost";
    static final String ORG_DATAARC_MONGO = "org.dataarc.datastore.mongo";
    static final String ORG_DATAARC_CORE = "org.dataarc.core";
    static final String ORG_DATAARC_SOLR = "org.dataarc.datastore.solr";
    private static final String ORG_DATAARC_BEAN = "org.dataarc.bean";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected Environment env;

    private Rollbar rollbar;

    public DataArcConfiguration() {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        return executor;
    }

    @Bean
    public Rollbar rollbar() {
        String accessToken = env.getProperty("rollbar.key");
        if (accessToken != null && rollbar == null) {
            rollbar = new Rollbar(accessToken, env.getProperty("rollbar.env", "production"));
            rollbar.handleUncaughtErrors();
        }
        return rollbar;
    }
    
    @Bean(name = "mailSender")
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(env.getProperty("mail.smtp.host", "localhost"));
        String username = env.getProperty("mail.smtp.username");
        String password = env.getProperty("mail.smtp.password");
        sender.setPort(env.getProperty("mail.smtp.port", Integer.class, 25));
        if (StringUtils.isNotBlank(username)) {
            sender.setUsername(username);
            sender.setUsername(password);

        }
        return sender;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { ORG_DATAARC_BEAN });

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(org.postgis.DriverWrapper.class.getName());
        String host = env.getProperty(PG_HOST, LOCALHOST);
        String database = env.getProperty(PG_DATABASE, DATAARC);
        dataSource.setUrl(String.format(JDBC_POSTGRESQL_POST_GIS_S_5432_S, host, database));
        dataSource.setUsername(env.getProperty(PG_USERNAME, DATAARC));
        dataSource.setPassword(env.getProperty(PG_PASSWORD, ""));

        return dataSource;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", org.hibernate.spatial.dialect.postgis.PostgisDialect.class.getName());
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("IndexAsync-");
        executor.initialize();
        return executor;
    }

}