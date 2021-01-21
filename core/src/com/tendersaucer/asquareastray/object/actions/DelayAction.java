package com.tendersaucer.asquareastray.object.actions;

public class DelayAction extends DelegateAction {

    private float duration, time;

    public DelayAction() {
    }

    public DelayAction(float duration) {
        this.duration = duration;
    }

    @Override
    public void restart () {
        super.restart();
        time = 0;
    }

    @Override
    protected boolean delegate(float delta) {
        if (time < duration) {
            time += delta;
            if (time < duration) return false;
            delta = time - duration;
        }
        if (action == null) {
            return true;
        }

        return action.act(delta);
    }

    public void finish() {
        time = duration;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }
}
