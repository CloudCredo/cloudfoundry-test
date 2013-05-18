package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.MongoDbCloudFoundryService;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: chris
 * @date: 12/05/2013
 */
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration("classpath:META-INF/cf-test/mongodb-context.xml")
@MongoDbCloudFoundryService
public class MongoDBCloudFoundryServiceTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void createCollection() {
        mongoTemplate.createCollection(DomainObject.class);
        Assertions.assertThat(mongoTemplate.collectionExists(DomainObject.class)).isTrue();
    }

}
