package com.cloudcredo.cloudfoundry.test;

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
                environment.put(key, value);
            }
        });
    }

    public void remove(final String key) {
        execute(new Command() {
            @Override
            public void execute(Map<String, String> environment) {
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
