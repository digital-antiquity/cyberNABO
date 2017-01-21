package org.dataarc.service;

import javax.xml.bind.JAXBException;

import org.dataarc.AbstractServiceTest;
import org.dataarc.bean.topic.TopicMap;
import org.dataarc.core.service.TopicMapService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.xml.sax.SAXException;

public class TopicMapServiceTest extends AbstractServiceTest {

    @Autowired 
    TopicMapService topicMapService;
    
    @Test
    @Rollback(true)
    public void testDeserializeUnidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/test/data/unidirectional.xtm");
    }

    @Test
    @Rollback(true)
    public void testDeserializeBidirectionalTopicMap() throws JAXBException, SAXException {
        TopicMap map = topicMapService.load("src/main/data/landscape_wandora.xtm");
    }

}