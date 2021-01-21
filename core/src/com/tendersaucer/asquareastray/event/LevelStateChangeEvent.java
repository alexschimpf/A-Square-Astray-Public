package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.level.LevelState;

public class LevelStateChangeEvent extends Event<ILevelStateChangeListener> {

    private final LevelState oldState;
    private final LevelState newState;

    public LevelStateChangeEvent(LevelState oldState, LevelState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public void notify(ILevelStateChangeListener listener) {
        listener.onLevelStateChange(oldState, newState);
    }
}
