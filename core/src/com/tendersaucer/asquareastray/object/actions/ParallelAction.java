package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.object.GameObject;

public class ParallelAction extends Action {

    protected Array<Action> actions = new Array<>();
    private boolean complete;

    public ParallelAction() {
    }

    public ParallelAction(Action... actions) {
        this.actions.addAll(actions);
    }

    @Override
    public boolean act(float delta) {
        if (complete) {
            return true;
        }

        complete = true;
        Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n && target != null; i++) {
            Action currentAction = actions.get(i);
            if (currentAction.getTarget() != null && !currentAction.act(delta)) {
                complete = false;
            }
            if (target == null) {
                return true;
            }
        }

        return complete;
    }

    @Override
    public void setTarget(GameObject target) {
        Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n; i++) {
            actions.get(i).setTarget(target);
        }
        super.setTarget(target);
    }

    @Override
    public void restart() {
        complete = false;
        Array<Action> actions = this.actions;
        for (int i = 0, n = actions.size; i < n; i++)
            actions.get(i).restart();
    }

    public void addAction(Action action) {
        actions.add(action);
        if (target != null) {
            action.setTarget(target);
        }
    }

    public Array<Action> getActions() {
        return actions;
    }
}
