package com.ilummc.tlib.scripting.groovy;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;

import java.util.HashMap;
import java.util.Map;

public class GroovyProperty extends GroovyObjectSupport {

    private final Map<String, Object> properties = new HashMap<>();

    @Override
    public void setProperty(String property, Object newValue) {
        try {
            super.setProperty(property, newValue);
        } catch (MissingPropertyException e) {
            properties.put(property, newValue);
        }
    }

    @Override
    public Object getProperty(String property) {
        try {
            Object o = super.getProperty(property);
            return o == null ? properties.get(property) : o;
        } catch (MissingPropertyException e) {
            Object o = properties.get(property);
            if (o == null) {
                throw e;
            } else {
                return o;
            }
        }
    }
}
