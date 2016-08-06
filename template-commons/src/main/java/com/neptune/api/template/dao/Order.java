package com.neptune.api.template.dao;

public class Order {
    public static enum Direction {
        DESC, ASC
    }

    private Direction direction;
    private String field;

    public Order(Direction direction, String field) {
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
