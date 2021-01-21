package com.tendersaucer.asquareastray.pool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;


public final class Vector2Pool extends Pool<Vector2> {

    private static final Vector2Pool instance = new Vector2Pool();

    private Vector2Pool() {
    }

    public static Vector2Pool getInstance() {
        return instance;
    }

    @Override
    protected Vector2 newObject() {
        return new Vector2();
    }

    public Vector2 obtain(Vector2 v) {
        return obtain().set(v);
    }

    public Vector2 obtain(float x, float y) {
        return obtain().set(x, y);
    }
}