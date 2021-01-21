package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.IAbilityDeactivatedListener;

public class AbilityDeactivatedEvent extends Event<IAbilityDeactivatedListener> {

    @Override
    public void notify(IAbilityDeactivatedListener listener) {
        listener.onAbilityDeactivated();
    }
}
