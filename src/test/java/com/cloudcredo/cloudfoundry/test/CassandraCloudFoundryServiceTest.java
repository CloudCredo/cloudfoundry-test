package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.CassandraCloudFoundryService;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author: chris
 * @date: 20/05/2013
 */
@RunWith(CloudFoundryJUnitClassRunner.class)
@ContextConfiguration("classpath:META-INF/cf-test/cassandra-context.xml")
@CassandraCloudFoundryService
public class CassandraCloudFoundryServiceTest {

    @Autowired
    ConnectionPoolConfigurationImpl unit;

    @Test
    public void shouldTest() {
        Assertions.assertThat(unit.getPort()).isNotNull();
        Assertions.assertThat(unit.getSeedHosts()).hasSize(1);
    }
}
