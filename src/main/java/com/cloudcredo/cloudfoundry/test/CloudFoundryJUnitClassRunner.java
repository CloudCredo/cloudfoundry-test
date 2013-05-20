package com.cloudcredo.cloudfoundry.test;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test Runner that will allow Tests to create and bind to Cloud Foundry services through the use of the Spring
 * '<cloud:... /> namespace. This Test Runner as with SpringJUnit4ClassRunner expects a valid @ContextConfiguration to
 * be present.
 *
 * @author: chris
 * @date: 28/04/2013
 */
public class CloudFoundryJUnitClassRunner extends SpringJUnit4ClassRunner {

    public CloudFoundryJUnitClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        setUpEnvironment();
        createRequiredServices();
        return super.withBeforeClasses(statement);
    }

    /*
     * Environment is required for cloud:properties
     */
    private void setUpEnvironment() {
        String json = "{\"instance_id\":\"66e80d21b9a55675cdb67ab8594c3a3a\",\"instance_index\":0,\"name\":\"test\",\"uris\":[\"test.vcap.me\"],\"users\":[\"chris@cloudcredo.com\"],\"version\":\"67cfc347bfb81be70285dd29a819074d9d198079-1\",\"start\":\"2013-05-05 16:51:45 -0700\",\"runtime\":\"ruby18\",\"state_timestamp\":1367797905,\"port\":53703,\"limits\":{\"fds\":256,\"mem\":134217728,\"disk\":2147483648},\"host\":\"172.16.19.127\"}";
        if (!EnvironmentVariables.contains("VCAP_APPLICATION")) {
            new EnvironmentVariables().set("VCAP_APPLICATION", json);
        }
    }


    private void createRequiredServices() {
        Class<?> javaClass = getTestClass().getJavaClass();
        CloudFoundryServiceProvisioner cloudFoundryServiceProvisioner = new CloudFoundryServiceProvisioner();
        cloudFoundryServiceProvisioner.createServicesForClass(javaClass);
    }
}
