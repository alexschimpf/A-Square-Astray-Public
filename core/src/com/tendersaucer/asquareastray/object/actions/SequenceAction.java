package com.tendersaucer.asquareastray.object.actions;

public class SequenceAction extends ParallelAction {

    private int index;

    public SequenceAction() {
    }

    public SequenceAction(Action... actions) {
        super(actions);
    }

    @Override
    public boolean act(float delta) {
        if (index >= actions.size) {
            return true;
        }

        if (actions.get(index).act(delta)) {
            if (target == null) {
                return true;
            }

            index++;
            if (index >= actions.size) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void restart() {
        super.restart();
        index = 0;
    }
}
