package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.CassandraCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.MongoDbCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.RabbitMQCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.RedisCloudFoundryService;
import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: chris
 * @date: 29/04/2013
 */
@RabbitMQCloudFoundryService
@CassandraCloudFoundryService
@RedisCloudFoundryService
@MongoDbCloudFoundryService
public class CloudFoundryServiceProvisionerTest {

    private CloudFoundryServiceProvisioner unit;

    @Before
    public void before() {
        new EnvironmentVariables().remove("VCAP_SERVICES");
        unit = new CloudFoundryServiceProvisioner();
    }

    @After
    public void after() {
        new EnvironmentVariables().remove("VCAP_SERVICES");
    }

    @Test
    public void shouldCreateService() {
        unit.createServicesForClass(this.getClass());
        String actual = System.getenv().get("VCAP_SERVICES");
        //We have other tests to ensure that the structure is correct. As this is an integration test the actual value
        //of actual will change everytime the test is run.
        Assertions.assertThat(actual)
                .contains(CloudFoundryService.CASSANDRA.serviceName)
                .contains(CloudFoundryService.REDIS.serviceName)
                .contains(CloudFoundryService.MONGODB.serviceName)
                .contains(CloudFoundryService.RABBITMQ.serviceName);
    }
}
