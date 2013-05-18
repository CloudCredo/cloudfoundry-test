package com.cloudcredo.cloudfoundry.test;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author: chris
 * @date: 11/05/2013
 */
public class Login {

    @Test
    public void login() throws MalformedURLException {
        CloudCredentials cloudCredentials = new CloudCredentials("chrus@cloudcredo.com", "c1oudc0w");
        CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(cloudCredentials, new URL("http://api.cloudfoundry.postoffice.test"));
        cloudFoundryClient.login();
    }
}
