package com.cloudcredo.cloudfoundry.test;

/**
 * @author: chris
 * @date: 29/04/2013
 */

/**
 * Adapter that site in-between the Nats CloudFoundry layer and the Environment that the system sits on. I.e it sets the
 * environment variables that the CloudFoundry Springframework support requires to run (VCAP_SERVICES)
 */
class CloudFoundryEnvironmentAdapter {

    /**
     * Allows us to set the Environment Variables expected by the Cloud Foundry Spring plugins for the running Java
     * process
     */
    private EnvironmentVariables environmentVariables = new EnvironmentVariables();

    /**
     *
     * @param testCredentials
     */
    void addVcapServicesForRabbitMq(Credentials testCredentials) {
        environmentVariables.set("VCAP_SERVICES", getJson(testCredentials, CloudFoundryService.RABBITMQ));
    }

    /**
     *
     * @param testCredentials
     */
    void addVcapServicesForRedis(Credentials testCredentials) {
        environmentVariables.set("VCAP_SERVICES", getJson(testCredentials, CloudFoundryService.REDIS));
    }

    /** @param credentials to create the expected vcap Json from */
    private void addVcapServicesForRabbitMq(Credentials credentials, CloudFoundryService cloudFoundryService) {
        environmentVariables.set("VCAP_SERVICES", getJson(credentials, cloudFoundryService));
    }

    private String getJson(Credentials credentials, CloudFoundryService cloudFoundryService) {

        String serviceName = cloudFoundryService.serviceName;
        String serviceVersion = cloudFoundryService.serviceVersion;
        String label = String.format("%s-%s", serviceName, serviceVersion);

        return "{\"" + label + "\":[\n" +
                "    {\n" +
                "        \"name\":\"" + credentials.getName() + "\",\n" +
                "        \"label\":\"" + label + "\",\n" +
                "        \"plan\":\"free\",\n" +
                "        \"tags\":[\"" + cloudFoundryService.serviceName + "\"],\n" +
                "        \"credentials\":{\n" +
                "            \"name\":\"d6d665aa69817406d8901cd145e05e3c6\",\n" +
                "            \"hostname\":\"" + credentials.getHostname() + "\",\n" +
                "            \"host\":\"" + credentials.getHost() + "\",\n" +
                "            \"port\":" + credentials.getPort() + ",\n" +
                "            \"user\":\"" + credentials.getUser() + "\",\n" +
                "            \"username\":\"" + credentials.getUsername() + "\",\n" +
                "            \"password\":\"" + credentials.getPassword() + "\",\n" +
                "            \"url\":\"" + credentials.getUrl() + "\"\n" +
                "        }\n" +
                "    }\n" +
                "]}";
    }
}
