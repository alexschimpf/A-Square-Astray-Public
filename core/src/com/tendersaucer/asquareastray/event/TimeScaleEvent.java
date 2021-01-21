package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.ITimeScaleListener;
import com.tendersaucer.asquareastray.utils.Tween;

public class TimeScaleEvent extends Event<ITimeScaleListener> {

    private final Tween tween;

    public TimeScaleEvent(Tween tween) {
        this.tween = tween;
    }

    @Override
    public void notify(ITimeScaleListener listener) {
        listener.onTimeScale(tween);
    }
}
