package com.cloudcredo.cloudfoundry.test;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: chris
 * @date: 29/04/2013
 */
public class CloudFoundryEnvironmentAdapterTest {

    private CloudFoundryEnvironmentAdapter unit;

    @Before
    public void before() {
        this.unit = new CloudFoundryEnvironmentAdapter();
    }

    @After
    public void after() {
        new EnvironmentVariables().remove("VCAP_SERVICES");
    }

    @Test
    public void shouldSetRabbitMq() {
        Credentials testCredentials = getTestCredentials();
        Map<CloudFoundryService, Credentials> t = new HashMap<CloudFoundryService, Credentials>();
        t.put(CloudFoundryService.RABBITMQ, testCredentials);
        unit.addVcapServices(t);
        String actual = System.getenv().get("VCAP_SERVICES");
        Assertions.assertThat(actual).isEqualTo(expectedJson(testCredentials, "rabbitmq-2.4", "rabbitmq"));
    }

    @Test
    public void shouldSetRedis() {
        Credentials testCredentials = getTestCredentials();
        Map<CloudFoundryService, Credentials> t = new HashMap<CloudFoundryService, Credentials>();
        t.put(CloudFoundryService.REDIS, testCredentials);
        unit.addVcapServices(t);
        String actual = System.getenv().get("VCAP_SERVICES");
        Assertions.assertThat(actual).isEqualTo(expectedJson(testCredentials, "redis-1.8", "redis"));
    }

    @Test
    public void shouldSetMongoDb() {
        Credentials testCredentials = getTestCredentials();
        Map<CloudFoundryService, Credentials> t = new HashMap<CloudFoundryService, Credentials>();
        t.put(CloudFoundryService.MONGODB, testCredentials);
        unit.addVcapServices(t);
        String actual = System.getenv().get("VCAP_SERVICES");
        Assertions.assertThat(actual).isEqualTo(expectedJson(testCredentials, "mongodb-2.8", "mongodb"));
    }

    private Credentials getTestCredentials() {

        String expectedUserName = "username";
        String expectedHost = "host";
        String expectedName = "expectedName";
        String expectedNodeId = "nodeId";
        String expectedPassword = "password";
        String expectedPort = "8080";
        String expectedUrl = "url";
        String expectedVhost = "vhost";
        String expectedDb = "db";

        Credentials c = new Credentials();
        c.setUsername(expectedUserName);
        c.setUser(expectedUserName);
        c.setUsername(expectedUserName);
        c.setHost(expectedHost);
        c.setHostname(expectedHost);
        c.setDb(expectedDb);
        c.setName(expectedName);
        c.setNode_id(expectedNodeId);
        c.setPass(expectedPassword);
        c.setPassword(expectedPassword);
        c.setPasswd(expectedPassword);
        c.setPort(expectedPort);
        c.setUrl(expectedUrl);
        c.setVhost(expectedVhost);
        return c;
    }

    private String expectedJson(Credentials credentials, String name, String tag) {
        return "{\"" + name + "\":[\n" +
                "    {\n" +
                "        \"name\":\"" + credentials.getName() + "\",\n" +
                "        \"label\":\"" + name + "\",\n" +
                "        \"plan\":\"free\",\n" +
                "        \"tags\":[\"" + tag + "\"],\n" +
                "        \"credentials\":{\n" +
                "            \"name\":\"d6d665aa69817406d8901cd145e05e3c6\",\n" +
                "            \"hostname\":\"" + credentials.getHostname() + "\",\n" +
                "            \"host\":\"" + credentials.getHost() + "\",\n" +
                "            \"port\":" + credentials.getPort() + ",\n" +
                "            \"user\":\"" + credentials.getUser() + "\",\n" +
                "            \"username\":\"" + credentials.getUsername() + "\",\n" +
                "            \"password\":\"" + credentials.getPassword() + "\",\n" +
                "            \"db\":\"" + credentials.getDb() + "\",\n" +
                "            \"url\":\"" + credentials.getUrl() + "\"\n" +
                "        }\n" +
                "    }\n" +
                "]}";
    }
}
