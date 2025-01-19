package com.elan.sample;

public class Emp {
    private String name;

    @Override
    public String toString() {
        return "Emp{" +
                "name='" + name + '\'' +
                '}';
    }

    public Emp(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
