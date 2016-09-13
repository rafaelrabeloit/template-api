package com.neptune.api.template.dao;

public class Filtering {
    public static enum Operation {
        EQUAL, NOT_EQUAL, SEARCH
    }

    private Operation mOperation;
    private String mProperty;
    private Object mValue;

    public Filtering(Operation operation, String property, Object value) {
        super();
        this.mOperation = operation;
        this.mProperty = property;
        this.mValue = value;
    }

    public final Operation getOperation() {
        return mOperation;
    }

    public final void setOperation(Operation operation) {
        this.mOperation = operation;
    }

    public final String getProperty() {
        return mProperty;
    }

    public final void setProperty(String property) {
        this.mProperty = property;
    }

    public final Object getValue() {
        return mValue;
    }

    public final void setValue(Object value) {
        this.mValue = value;
    }
}
