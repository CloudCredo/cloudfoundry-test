package com.cloudcredo.cloudfoundry.test;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author: chris
 * @date: 05/05/2013
 */
public class DomainObject implements Serializable {

    private String name;
    private Integer age;
    private UUID id;

    public DomainObject(String name, Integer age) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.age = age;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainObject that = (DomainObject) o;

        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DomainObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                '}';
    }
}
