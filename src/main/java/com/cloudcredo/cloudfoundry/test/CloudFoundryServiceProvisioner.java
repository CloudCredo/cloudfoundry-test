package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.RabbitMQCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.RedisCloudFoundryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Object that delegates to an an instance of Cloud Foundry, creates the required service and set the environment
 * variable for the Java process.
 *
 * @author: chris
 * @date: 29/04/2013
 */
public class CloudFoundryServiceProvisioner {

    private static final Logger log = LoggerFactory.getLogger(CloudFoundryServiceProvisioner.class);

    /** Sets the Environment variables that the Spring Cloud Foundry module expect */
    private CloudFoundryEnvironmentAdapter cloudFoundryEnvironmentAdapter = new CloudFoundryEnvironmentAdapter();

    /** Creates the required service in Cloud Foundry and returns the generated Credentials */
    private NatsCloudFoundryServicesClient natsCloudFoundryServicesClient = new NatsCloudFoundryServicesClient();

    /**
     * Creates a RabbitMQ service in the target instance of Cloud Foundry and sets the VCAP_SERVICES environment
     * variables as required by Spring for auto-connect functionality.
     */
    void createRabbitMqService() {
        try {
            log.info("Creating new RabbitMQ Cloud Foundry Service");
            Credentials rabbitmqCredentials = natsCloudFoundryServicesClient.getCredentialsForNewService("rabbit-test", CloudFoundryService.RABBITMQ);
            cloudFoundryEnvironmentAdapter.addVcapServicesForRabbitMq(rabbitmqCredentials);
        } catch (InterruptedException e) {
            e.printStackTrace();  //TODO Handle this exception
        }
    }

    /**
     * Creates a Redis service in the target instance of Cloud Foundry and sets the VCAP_SERVICES environment variables
     * as required by Spring for auto-connect functionality.
     */
    void createRedisService() {
        try {
            log.info("Creating new Redis Cloud Foundry Service");
            Credentials redisCredentials = natsCloudFoundryServicesClient.getCredentialsForNewService("redis-test", CloudFoundryService.REDIS);
            cloudFoundryEnvironmentAdapter.addVcapServicesForRedis(redisCredentials);
        } catch (InterruptedException e) {
            e.printStackTrace();  //TODO Handle this exception
        }
    }

    /**
     * Looks for the Service annotations on the test class and creates a service for each found mixin interface.
     *
     * @param clazz Class to look for presence of annotations on.
     * @see com.cloudcredo.cloudfoundry.test.annotation
     */
    void createServicesForClass(Class clazz) {
        if (clazz.isAnnotationPresent(RabbitMQCloudFoundryService.class)) {
            createRabbitMqService();
        }

        if (clazz.isAnnotationPresent(RedisCloudFoundryService.class)) {
            createRedisService();
        }
    }
}
