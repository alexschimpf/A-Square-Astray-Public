package com.tendersaucer.asquareastray.event.listener;

import com.badlogic.gdx.math.Vector2;

public interface ITeleportListener {

    void onTeleport(Vector2 oldPos, Vector2 newPos);
}
