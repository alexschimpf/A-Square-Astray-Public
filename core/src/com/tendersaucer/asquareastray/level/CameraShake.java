package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.asquareastray.Time;

public class CameraShake {

    private static final int SHAKE_COOLDOWN = 200;

    private float time;
    private float currTime;
    private float power;
    private float currPower;
    private long lastShakeTime;
    private Vector3 translation;

    public CameraShake() {
        power = 0;
        time = 0;
        currTime = 0;
        currPower = 0;
        lastShakeTime = 0;
        translation = new Vector3();
    }

    public void shake(float power, float duration) {
        shake(power, duration, false);
    }

    public void shake(float power, float duration, boolean force) {
        if (force || TimeUtils.timeSinceMillis(lastShakeTime) > SHAKE_COOLDOWN) {
            lastShakeTime = TimeUtils.millis();
            this.power = power;
            time = duration;
            currTime = 0;
        }
    }

    public void stop() {
        time = 0;
    }

    public Vector3 update() {
        if (currTime <= time) {
            currPower = power * ((time - currTime) / time);
            translation.x = (MathUtils.random() - 0.5f) * 2 * currPower;
            translation.y = (MathUtils.random() - 0.5f) * 2 * currPower;
            currTime += Time.getInstance().getDeltaTime();
        } else {
            time = 0;
        }

        return translation;
    }

    public boolean isShaking() {
        return time > 0;
    }

    public Vector3 getTranslation() {
        return translation;
    }
}
