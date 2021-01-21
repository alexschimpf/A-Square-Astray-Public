package com.tendersaucer.asquareastray.event;

import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.event.listener.IAutoDashListener;

public class AutoDashEvent extends Event<IAutoDashListener> {

    private final Vector2 velocity;
    private final Vector2 position;

    public AutoDashEvent(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    @Override
    public void notify(IAutoDashListener listener) {
        listener.onAutoDash(position, velocity);
    }
}
