package com.neptune.api.template.dao;

public class Ordering {
    public static enum Direction {
        DESC, ASC
    }

    private Direction mDirection;
    private String mField;

    public Ordering(Direction direction, String field) {
        super();
        this.setDirection(direction);
        this.setField(field);
    }

    public final Direction getDirection() {
        return mDirection;
    }

    public final void setDirection(Direction direction) {
        this.mDirection = direction;
    }

    public final String getField() {
        return mField;
    }

    public final void setField(String field) {
        this.mField = field;
    }

}
