package com.tendersaucer.asquareastray.event;

import com.badlogic.gdx.Screen;
import com.tendersaucer.asquareastray.event.listener.IScreenChangeListener;

public class ScreenChangeEvent extends Event<IScreenChangeListener> {

    private final Screen screen;

    public ScreenChangeEvent(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void notify(IScreenChangeListener listener) {
        listener.onScreenChange(screen);
    }
}
