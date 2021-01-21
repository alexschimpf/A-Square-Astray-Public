package com.tendersaucer.asquareastray.event;

public abstract class Event<L> {

    public Event() {
    }

    public abstract void notify(L listener);
}
