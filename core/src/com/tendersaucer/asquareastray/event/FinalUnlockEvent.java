package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.IFinalUnlockListener;

public class FinalUnlockEvent extends Event<IFinalUnlockListener> {

    private final String doorName;

    public FinalUnlockEvent(String doorName) {
        this.doorName = doorName;
    }

    @Override
    public void notify(IFinalUnlockListener listener) {
        listener.onFullUnlock(doorName);
    }
}
