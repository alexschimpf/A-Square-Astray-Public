package com.tendersaucer.asquareastray.event;

import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.event.listener.ITeleportListener;

public class TeleportEvent extends Event<ITeleportListener> {

    private final Vector2 oldPos;
    private final Vector2 newPos;

    public TeleportEvent(Vector2 oldPos, Vector2 newPos) {
        this.oldPos = oldPos;
        this.newPos = newPos;
    }

    @Override
    public void notify(ITeleportListener listener) {
        listener.onTeleport(this.oldPos, this.newPos);
    }
}
