package com.cloudcredo.cloudfoundry.test;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: chris
 * @date: 11/05/2013
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/cf-test/root-context.xml")
public class LoginTest {

    @Value("${cf.username}")
    private String username;

    @Value("${cf.password}")
    private String password;

    @Value("${cf.target}")
    private String target;

    @Test
    public void shouldLoginSuccessfully() throws MalformedURLException {
        CloudCredentials cloudCredentials = new CloudCredentials(username, password);
        CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(cloudCredentials, new URL(target));
        String actual = cloudFoundryClient.login();
        Assertions.assertThat(actual).contains("bearer ");
    }
}
