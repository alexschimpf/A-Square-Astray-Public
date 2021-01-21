package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.IStartLevelListener;

public class StartLevelEvent extends Event<IStartLevelListener> {

    private final int levelId;
    private final boolean skipCountdown;

    public StartLevelEvent(int levelId, boolean skipCountdown) {
        this.levelId = levelId;
        this.skipCountdown = skipCountdown;
    }

    public StartLevelEvent(int levelId) {
        this(levelId, false);
    }

    @Override
    public void notify(IStartLevelListener listener) {
        listener.onStartLevel(levelId, this.skipCountdown);
    }
}
