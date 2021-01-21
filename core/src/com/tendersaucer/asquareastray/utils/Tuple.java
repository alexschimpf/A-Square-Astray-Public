package com.tendersaucer.asquareastray.utils;

public class Tuple<X, Y> {

    public X x;
    public Y y;

    public Tuple() {
    }

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public Tuple set(X x, Y y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Tuple set(Tuple<X, Y> other) {
        set(other.x, other.y);
        return this;
    }

    public boolean isSet() {
        return x != null && y != null;
    }
}