package com.cloudcredo.cloudfoundry.test;

import org.junit.runners.model.InitializationError;
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
    protected Object createTest() throws Exception {
        createRequiredServices();
        return super.createTest();
    }

    private void createRequiredServices() {
        Class<?> javaClass = getTestClass().getJavaClass();
        CloudFoundryServiceProvisioner cloudFoundryServiceProvisioner = new CloudFoundryServiceProvisioner();
        cloudFoundryServiceProvisioner.createServicesForClass(javaClass);
    }
}
