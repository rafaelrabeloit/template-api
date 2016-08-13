package com.neptune.api.template.dao;

public class Ordering {
    public static enum Direction {
        DESC, ASC
    }

    private Direction direction;
    private String field;

    public Ordering(Direction direction, String field) {
        super();
        this.setDirection(direction);
        this.setField(field);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
