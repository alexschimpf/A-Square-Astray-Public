package com.tendersaucer.asquareastray.event;

import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.event.listener.IGravitySwitchListener;

public class GravitySwitchEvent extends Event<IGravitySwitchListener> {

    private final Vector2 oldGravity;
    private final Vector2 newGravity;

    public GravitySwitchEvent(Vector2 oldGravity, Vector2 newGravity) {
        this.oldGravity = oldGravity;
        this.newGravity = newGravity;
    }

    @Override
    public void notify(IGravitySwitchListener listener) {
        listener.onGravitySwitch(oldGravity, newGravity);
    }
}
