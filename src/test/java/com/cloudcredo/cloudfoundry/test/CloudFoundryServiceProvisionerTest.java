package com.cloudcredo.cloudfoundry.test;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: chris
 * @date: 29/04/2013
 */
public class CloudFoundryServiceProvisionerTest {

    private CloudFoundryServiceProvisioner unit;

    @Before
    public void before() {
        new EnvironmentVariables().remove("VCAP_SERVICES");
        unit = new CloudFoundryServiceProvisioner();
    }

    @Test
    public void shouldCreateRabbitService() {
        unit.createRabbitMqService();
        String actual = System.getenv().get("VCAP_SERVICES");
        //We have other tests to ensure that the structure is correct. As this is an integration test the actual value
        //of actual will change everytime the test is run.
        Assertions.assertThat(actual).isNotEmpty();
    }

    @Test
    public void shouldCreateRedisService() {
        unit.createRedisService();
        String actual = System.getenv().get("VCAP_SERVICES");
        //Need a better way of checking the returned env
        Assertions.assertThat(actual).isNotEmpty();
    }
}
