package com.tendersaucer.asquareastray.object.actions;

public class RunnableAction extends Action {

    private Runnable runnable;
    private boolean ran;

    @Override
    public boolean act(float delta) {
        if (!ran) {
            ran = true;
            run();
        }
        return true;
    }

    @Override
    public void restart() {
        ran = false;
    }

    public void run() {
        runnable.run();
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
