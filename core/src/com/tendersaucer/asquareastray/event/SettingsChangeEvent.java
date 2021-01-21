package com.tendersaucer.asquareastray.event;

import com.tendersaucer.asquareastray.event.listener.ISettingsChangeListener;

public class SettingsChangeEvent extends Event<ISettingsChangeListener> {

    private final String key;
    private final Object value;

    public SettingsChangeEvent(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void notify(ISettingsChangeListener listener) {
        listener.onSettingsChange(key, value);
    }
}
