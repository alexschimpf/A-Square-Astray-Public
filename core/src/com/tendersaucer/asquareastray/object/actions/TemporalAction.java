package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.math.Interpolation;

public abstract class TemporalAction extends Action {

    private float duration, time;
    private boolean reverse, began, complete;
    private Interpolation interpolation;

    public TemporalAction() {
    }

    public TemporalAction(float duration) {
        this.duration = duration;
    }

    public TemporalAction(float duration, Interpolation interpolation) {
        this.duration = duration;
        this.interpolation = interpolation;
    }

    @Override
    public boolean act(float delta) {
        if (complete) {
            return true;
        }
        if (!began) {
            begin();
            began = true;
        }

        time += delta;
        complete = time >= duration;
        float percent = complete ? 1 : time / duration;
        if (interpolation != null) {
            percent = interpolation.apply(percent);
        }

        update(reverse ? 1 - percent : percent);
        if (complete) {
            end();
        }

        return complete;
    }

    @Override
    public void restart() {
        time = 0;
        began = false;
        complete = false;
    }

    abstract protected void update(float percent);

    protected void begin() {
    }

    protected void end() {
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

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isComplete() {
        return complete;
    }
}
