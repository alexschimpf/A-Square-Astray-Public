package com.tendersaucer.asquareastray.object.actions;

import com.tendersaucer.asquareastray.object.actions.TemporalAction;

public abstract class RelativeTemporalAction extends TemporalAction {

    private float lastPercent;

    protected void begin() {
        lastPercent = 0;
    }

    protected void update(float percent) {
        updateRelative(percent - lastPercent);
        lastPercent = percent;
    }

    protected abstract void updateRelative(float percentDelta);
}
