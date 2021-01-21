package com.tendersaucer.asquareastray.screen.transition;

import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class ScreenTransition {

    public static final float DEFAULT_DURATION = 1f;

    private boolean hasStarted;
    protected final float duration;
    protected final Stage stage;

    public ScreenTransition(Stage stage, float duration) {
        this.stage = stage;
        this.duration = duration;
    }

    public void start() {
        hasStarted = true;
    }

    public boolean update() {
        return false;
    }

    public boolean hasStarted() {
        return hasStarted;
    }
}
