package com.tendersaucer.asquareastray.event.listener;

import com.badlogic.gdx.math.Vector2;

public interface IGravitySwitchListener {

    void onGravitySwitch(Vector2 oldGravity, Vector2 newGravity);
}
