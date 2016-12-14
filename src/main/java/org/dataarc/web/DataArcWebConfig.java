package org.dataarc.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.dataarc")
public class DataArcWebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public FreeMarkerViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        // resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/js/**","/css/**","/images/**","/data/**")
//                .addResourceLocations("/webapp/", "/other-resources/")
//                .setCachePeriod(3600)
//                .resourceChain(true)
//                .addResolver(new PathResourceResolver());
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/content/");
        return freeMarkerConfigurer;
    }

    //
    // @Bean
    // public ViewResolver viewResolver() {
    // InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    // viewResolver.setViewClass(FreeMarkerView.class);
    // viewResolver.setPrefix("/WEB-INF/content/");
    // viewResolver.setSuffix(".ftl");
    //
    // return viewResolver;
    // }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

}