package com.tendersaucer.asquareastray.event.listener;

import com.tendersaucer.asquareastray.level.LevelState;

public interface ILevelStateChangeListener {

    void onLevelStateChange(LevelState oldState, LevelState newState);
}
