package com.cloudcredo.cloudfoundry.test;

import nats.client.*;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.cloudcredo.cloudfoundry.test.EnvironmentVariables.getEnv;

/**
 * Provides functionality to create and return generated credential for Cloud Foundry services.
 *
 * @author chris
 */
class NatsCloudFoundryServicesClient {

    private static final Logger log = LoggerFactory.getLogger(NatsCloudFoundryServicesClient.class);

    private static final String CLOUD_FOUNDRY_EMAIL = getEnv("cloud_foundry_email", "chris@cloudcredo.com");

    private static final String CLOUD_FOUNDRY_PASSWORD = getEnv("cloud_foundry_password", "c1oudc0w");

    /** The Cloud Foundry Nats URL */
    private final String NATS_URL = getEnv("mbus", "nats://nats:nats@api.vcap.me:4222");

    /** Cloud foundry Target */
    private final String CLOUD_FOUNDRY_TARGET = getEnv("target", "http://api.vcap.me");

    /**
     * Connects to the instance of Cloud Foundry as defined in TargetUrl, creates a new service, constructs and returns
     * a new Credentials object based on the created service credentials.
     *
     * @return a Credentials object containing all of the properties return from the instance of Cloud Foundry for the
     *         newly created service.
     * @throws InterruptedException
     */
    Credentials getCredentialsForNewService(final String serviceName, final CloudFoundryService service) throws InterruptedException {

        final AtomicReference<Credentials> credentialsAtomicReference = new AtomicReference<Credentials>();
        final CountDownLatch latch = new CountDownLatch(1);

        log.info("Connecting to NATS on: " + NATS_URL);
        final Nats nats = new NatsConnector().addHost(NATS_URL).connect();

        //Subscribe to all...
        final Subscription subscription = nats.subscribe(">");

        subscription.addMessageHandler(new MessageHandler() {
            Boolean name = false;

            public void onMessage(Message message) {

                log.info("Received message with Subject: " + message.getSubject() + " :: Body:" + message.getBody());


                if (message.getSubject().startsWith(service.newServiceMessageSubjectName)) {
                    log.info("Received message to listen for credentials for new " + service.serviceName + " service");
                    name = true;
                }

                //TODO new message handler for inbox
                if (name && message.getSubject().startsWith("_INBOX") && message.getBody() != null) {
                    System.out.println(message.getBody());
                    try {
                        Credentials foundCreds = getCredentials(message.getBody(), service);
                        if (foundCreds.getNodeId().startsWith(service.serviceNode)) {
                            log.info("Received expected _INBOX message for " + foundCreds.getNodeId());
                            credentialsAtomicReference.set(foundCreds);
                            log.info("Credentials " + foundCreds + " set.");
                        } else {
                            log.warn("Received expected _INBOX message for " + foundCreds.getNodeId()
                                    + " but was expecting "
                                    + service.serviceNode);
                        }
                    } catch (Exception e) {
                        log.warn("Not yet Received Credentials: " + message.getSubject(), e);
                    }

                }

                if (message.getSubject().equals("vcap.cc.events")) {
                    String body = message.getBody();
                    if (body.contains(serviceName) && body.contains(service.serviceName) && body.contains("POST:/services") && body.contains("SUCCEEDED")) {
                        log.info("Service creation for " + serviceName + " succeeded. Exiting listener");
                        latch.countDown();
                    } else {
                        log.warn(body);
                    }
                }
            }
        });

        newService(serviceName, service.serviceName);
        latch.await(5000, TimeUnit.MILLISECONDS);
        closeNats(nats);

        return credentialsAtomicReference.get();
    }

    private void closeNats(Nats nats) {
        log.info("Credentials found. Will close channel to Cloud Foundry");
        nats.close();
    }

    /*
     * Creates a new Cloud Foundry service
     */
    private void newService(String serviceName, String serviceType) {
        log.info("Requesting new Cloud Foundry service for " + serviceType);
        URL target = getCloudFoundryURL();
        CloudCredentials cloudCredentials = new CloudCredentials(CLOUD_FOUNDRY_EMAIL, CLOUD_FOUNDRY_PASSWORD);
        CloudFoundryClient cloudFoundryClient = new CloudFoundryClient(cloudCredentials, target);
        log.info(CLOUD_FOUNDRY_EMAIL + " " + CLOUD_FOUNDRY_PASSWORD + " " + CLOUD_FOUNDRY_TARGET);
        cloudFoundryClient.login();

        CloudService cloudService = getCloudService(cloudFoundryClient, serviceName, serviceType);

        //Could pull this out in to a wrapped CloudFoundryService
        if (serviceExists(cloudFoundryClient, serviceName)) {
            log.warn("Service " + serviceName + " already exists. Deleting existing service. All existing data will be lost");
            deleteService(cloudFoundryClient, cloudService);
        }
        newService(cloudFoundryClient, cloudService);
    }

    private URL getCloudFoundryURL() {
        try {
            return new URL(CLOUD_FOUNDRY_TARGET);
        } catch (MalformedURLException e) {
            throw new RuntimeException(CLOUD_FOUNDRY_TARGET + " is not a valid URL format", e);
        }
    }

    /*
     * Rudimentary retry to catch service gateway 502 errors.
     */
    private void deleteService(CloudFoundryClient cloudFoundryClient, CloudService cloudService) {
        for (int i = 0; i < 3; i++) {
            try {
                log.info("Deleting existing Cloud Service: " + cloudService.getName());
                deleteRawService(cloudFoundryClient, cloudService);
                return;
            } catch (HttpServerErrorException e) {
                log.warn("Encountered 502 Bad Gateway error. Will try again");
            }
        }
    }

    private void deleteRawService(CloudFoundryClient cloudFoundryClient, CloudService cloudService) {
        try {
            cloudFoundryClient.deleteService(cloudService.getName());
        } catch (CloudFoundryException e) {
            log.warn("Could not delete service as expected.", e);
        }
    }

    /*
     * Rudimentary retry to catch service gateway 502 errors.
     */
    private void newService(CloudFoundryClient cloudFoundryClient, CloudService cloudService) {
        for (int i = 0; i < 3; i++) {
            try {
                log.info("Creating new Cloud Service: " + cloudService.getName());
                cloudFoundryClient.createService(cloudService);
                return;
            } catch (HttpServerErrorException e) {
                log.warn("Encountered 502 Bad Gateway error. Will try again");
            }
        }
    }

    /**
     * @param cloudFoundryClient to get the service configurations from.
     * @param serviceName
     * @param serviceType        to create
     * @return a CloudService
     */

    private CloudService getCloudService(CloudFoundryClient cloudFoundryClient, String serviceName, String serviceType) {
        ServiceConfiguration serviceConfiguration = getServiceConfiguration(cloudFoundryClient, serviceType);
        CloudService cloudService = new CloudService(serviceConfiguration.getMeta(), serviceType);
        cloudService.setName(serviceName);
        cloudService.setProvider("core");
        cloudService.setTier("free");
        cloudService.setVersion(serviceConfiguration.getVersion());
        cloudService.setVendor(serviceConfiguration.getVendor());
        return cloudService;
    }

    private ServiceConfiguration getServiceConfiguration(CloudFoundryClient cloudFoundryClient, String serviceType) {
        List<ServiceConfiguration> serviceConfigurations = cloudFoundryClient.getServiceConfigurations();
        ServiceConfiguration databaseServiceConfiguration = null;
        for (ServiceConfiguration sc : serviceConfigurations) {
            if ((sc.getVendor() != null && sc.getVendor().equals(serviceType)) ||
                    (sc.getCloudServiceOffering() != null && sc.getCloudServiceOffering().getLabel().equals(serviceType))) {
                databaseServiceConfiguration = sc;
                break;
            }
        }
        return databaseServiceConfiguration;
    }

    /**
     * CloudFoundry API provides no 'exists' method so need to catch the Exception.
     *
     * @param cloudFoundryClient
     * @param serviceName
     * @return
     */
    private boolean serviceExists(CloudFoundryClient cloudFoundryClient, String serviceName) {
        try {
            cloudFoundryClient.getService(serviceName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Builds a Credentials object from a JSON string.
     *
     * @param body Raw Json returned from Cloud Foundry
     * @return a Credentials object build up from the contents of body
     * @throws IOException
     */
    private Credentials getCredentials(String body, CloudFoundryService cloudFoundryService) throws IOException {
        JsonNode credentialsJsonNode = new ObjectMapper().readTree(body).get("credentials");
        Credentials credentials = new ObjectMapper().readValue(credentialsJsonNode, Credentials.class);

        //Sadly the CloudFoudnry MongoDB VCAP env var is in a different form to the other services so we need
        //to override the name to that of our test service name (This logic should not live here tho).
        if (cloudFoundryService.serviceName.equals("mongodb")) {
            credentials.setName("mongodb-test");
        }
        return credentials;
    }
}
