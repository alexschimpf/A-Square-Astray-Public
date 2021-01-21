package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.component.ability.AbilityType;
import com.tendersaucer.asquareastray.event.listener.IAbilityReadyListener;

public class AbilityReadyEvent extends Event<IAbilityReadyListener> {

    private final AbilityType abilityType;

    public AbilityReadyEvent(AbilityType abilityType) {
        this.abilityType = abilityType;
    }

    @Override
    public void notify(IAbilityReadyListener listener) {
        listener.onAbilityReady(abilityType);
    }
}
