package com.cloudcredo.cloudfoundry.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mixin interface to denote that a CloudFoundry MongoDB service will be required for the test.
 *
 * @author: chris
 * @date: 05/05/2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface CassandraCloudFoundryService {

}
