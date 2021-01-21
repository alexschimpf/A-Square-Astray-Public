package com.tendersaucer.asquareastray.pool;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class Vector3Pool extends Pool<Vector3> {

    private static final Vector3Pool instance = new Vector3Pool();

    private Vector3Pool() {
    }

    public static Vector3Pool getInstance() {
        return instance;
    }

    @Override
    protected Vector3 newObject() {
        return new Vector3();
    }

    public Vector3 obtain(float x, float y, float z) {
        return obtain().set(x, y, z);
    }
}