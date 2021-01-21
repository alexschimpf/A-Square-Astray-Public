package com.tendersaucer.asquareastray.object.actions;

import com.tendersaucer.asquareastray.object.GameObject;

public abstract class DelegateAction extends Action {

    protected Action action;

    abstract protected boolean delegate(float delta);

    @Override
    public final boolean act(float delta) {
        return delegate(delta);
    }

    @Override
    public void restart() {
        if (action != null) {
            action.restart();
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setTarget(GameObject target) {
        if (action != null) {
            action.setTarget(target);
        }
        super.setTarget(target);
    }
}
