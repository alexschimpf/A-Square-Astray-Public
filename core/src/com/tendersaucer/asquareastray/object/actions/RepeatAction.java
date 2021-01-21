package com.tendersaucer.asquareastray.object.actions;

public class RepeatAction extends DelegateAction {

    public static final int FOREVER = -1;

    private int repeatCount, executedCount;
    private boolean finished;

    @Override
    public void restart () {
        super.restart();
        executedCount = 0;
        finished = false;
    }

    @Override
    protected boolean delegate(float delta) {
        if (executedCount == repeatCount) {
            return true;
        }

        if (action.act(delta)) {
            if (finished) {
                return true;
            }
            if (repeatCount > 0) {
                executedCount++;
            }
            if (executedCount == repeatCount) {
                return true;
            }
            if (action != null) {
                action.restart();
            }
        }

        return false;
    }

    public void finish() {
        finished = true;
    }

    public void setCount(int count) {
        this.repeatCount = count;
    }

    public int getCount() {
        return repeatCount;
    }
}
