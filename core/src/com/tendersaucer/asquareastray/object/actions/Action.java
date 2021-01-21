package com.tendersaucer.asquareastray.object.actions;

import com.tendersaucer.asquareastray.object.GameObject;

public abstract class Action {

    protected GameObject target;

    abstract public boolean act(float delta);

    public void setTarget(GameObject target) {
        this.target = target;
    }

    public GameObject getTarget() {
        return target;
    }

    public void restart() {
    }
}
