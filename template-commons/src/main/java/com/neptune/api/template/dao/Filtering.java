package com.neptune.api.template.dao;

public class Filtering {
    public static enum Operation {
        EQUAL, NOT_EQUAL, SEARCH
    }

    private Operation operation;
    private String property;
    private Object value;

    public Filtering(Operation operation, String property, Object value) {
        super();
        this.operation = operation;
        this.property = property;
        this.value = value;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
