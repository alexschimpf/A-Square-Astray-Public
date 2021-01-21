package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.IAbilityActivatedListener;

public class AbilityActivatedEvent extends Event<IAbilityActivatedListener> {

    @Override
    public void notify(IAbilityActivatedListener listener) {
        listener.onAbilityActivated();
    }
}
