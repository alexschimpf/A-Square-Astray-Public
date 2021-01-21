package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.IUnlockListener;

public class UnlockEvent extends Event<IUnlockListener> {

    private final String doorName;

    public UnlockEvent(String doorName) {
        this.doorName = doorName;
    }

    @Override
    public void notify(IUnlockListener listener) {
        listener.onUnlock(doorName);
    }
}
