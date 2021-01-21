package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.event.listener.ISettingsChangeListener;
import com.tendersaucer.asquareastray.event.listener.ITimeScaleListener;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.utils.Tween;

public class Time implements ILevelStateChangeListener, ITimeScaleListener, ISettingsChangeListener {

    private static final float[] TIME_SCALES_BY_DIFFICULTY = new float[] { 0.9f, 1, 1.2f };

    private static final Time instance = new Time();

    private float maxTimeScale;
    private float timeScale;
    private Tween tween;

    public Time() {
        int difficulty = Persistence.getInstance().getInteger("difficulty", 1);
        maxTimeScale = TIME_SCALES_BY_DIFFICULTY[difficulty];
    }

    public static Time getInstance() {
        return instance;
    }

    @Override
    public void onLevelStateChange(LevelState oldState, LevelState newState) {
        if (newState == LevelState.LOADING) {
            tween = null;
            timeScale = maxTimeScale;
        }
    }

    @Override
    public void onTimeScale(Tween tween) {
        this.tween = tween;
    }

    @Override
    public void onSettingsChange(String key, Object value) {
        if (key.equals("difficulty")) {
            long difficulty = (long)value;
            maxTimeScale = TIME_SCALES_BY_DIFFICULTY[(int)difficulty];
        }
    }

    public void update() {
        if (tween != null) {
            if (tween.isDone()) {
                tween = null;
            } else {
                timeScale = tween.get(Gdx.graphics.getDeltaTime());
            }
        }
    }

    public float getDeltaTime() {
        return timeScale * Gdx.graphics.getDeltaTime();
    }

    public float getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
    }

    public float getMaxTimeScale() {
        return maxTimeScale;
    }
}
