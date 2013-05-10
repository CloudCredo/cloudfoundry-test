package com.cloudcredo.cloudfoundry.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * Manges the System environment variables. This class allows environment variables to be set in the running
 * applications only - they will not be meade available at the system level or for other running Java processes.
 *
 * @author: chris
 * @date: 29/04/2013
 */
class EnvironmentVariables {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentVariables.class);

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv().get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * Hack to set the environment variable HashMap in the system class. Allows us to set the VCAP_SERVICES environment
     * variable that the AbstractServiceInfo classes require.
     *
     * @param key   the key of the environment variable
     * @param value the value of the environment variable
     * @throws Exception
     */
    void set(final String key, final String value) {
        execute(new Command() {
            @Override
            public void execute(Map<String, String> environment) {
                if(environment.containsKey(key)) {
                    EnvironmentVariables.this.remove(key);
                }
                log.debug("Adding environment variable: " + key + " " + value);
                environment.put(key, value);
            }
        });
    }

    public void remove(final String key) {
        execute(new Command() {
            @Override
            public void execute(Map<String, String> environment) {
                log.trace("Removing environment variable: " + key);
                environment.remove(key);
            }
        });
    }


    private void execute(Command command) {
        try {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    command.execute(map);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("There has been an error setting the environment variable");
        }
    }

    private static interface Command {
        void execute(Map<String, String> environment);
    }

}
