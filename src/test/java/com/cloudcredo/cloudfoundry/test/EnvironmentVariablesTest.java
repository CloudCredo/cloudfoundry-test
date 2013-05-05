package com.cloudcredo.cloudfoundry.test;

import org.fest.assertions.Assertions;
import org.junit.Test;

/**
 * @author: chris
 * @date: 29/04/2013
 */
public class EnvironmentVariablesTest {

    private final EnvironmentVariables unit = new EnvironmentVariables();

    @Test
    public void shouldSetEnvironmentVariable() {
        String expectedValue = "Value";
        String actualKey = "key";
        String actual = null;
        actual = System.getenv(actualKey);
        Assertions.assertThat(actual).isEqualTo(null);
        new EnvironmentVariables().set(actualKey, expectedValue);
        actual = System.getenv(actualKey);
        Assertions.assertThat(actual).isEqualTo(expectedValue);
    }

    @Test
    public void shouldRemoveEnvironmentVariable() {
        String toRemove = "TO_REMOVE";
        unit.set(toRemove, "value");
        String actual = System.getenv(toRemove);
        Assertions.assertThat(actual).isEqualTo("value");
        unit.remove(toRemove);
        actual = System.getenv(toRemove);
        Assertions.assertThat(actual).isNull();
    }
}
