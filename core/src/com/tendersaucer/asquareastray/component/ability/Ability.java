package com.tendersaucer.asquareastray.component.ability;

import com.tendersaucer.asquareastray.component.Component;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.event.AbilityActivatedEvent;
import com.tendersaucer.asquareastray.event.listener.IAbilityActivatedListener;

public abstract class Ability extends Component implements IAbilityActivatedListener {

    public Ability(Level level, GameObject parent, Object... params) {
        super(level, parent);
    }

    @Override
    public void init() {
        EventManager.getInstance().listen(AbilityActivatedEvent.class, this);
    }

    public abstract AbilityType getType();
}
