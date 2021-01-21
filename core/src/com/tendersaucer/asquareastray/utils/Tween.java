package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class Tween {

    private float elapsed;
    public float from;
    public float to;
    public float duration;
    public Interpolation interpolation;

    public Tween(float from, float to, float duration, Interpolation interpolation) {
        this.elapsed = 0;
        this.from = from;
        this.to = to;
        this.duration = duration;
        this.interpolation = interpolation;
    }

    public Tween(float from, float to, float duration) {
        this(from, to, duration, null);
    }

    public float get(float delta) {
        elapsed += delta * 1000;
        float progress = MathUtils.clamp(elapsed / duration, 0, 1);
        if (interpolation != null) {
            progress = interpolation.apply(progress);
        }
        return MathUtils.lerp(from, to, progress);
    }

    public boolean isDone() {
        return elapsed >= duration;
    }

    public void reset() {
        elapsed = 0;
    }

    public float getElapsed() {
        return elapsed;
    }

    public float getProgress() {
        return MathUtils.clamp(elapsed / duration, 0, 1);
    }
}
