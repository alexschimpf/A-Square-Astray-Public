package com.tendersaucer.asquareastray.event;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.asquareastray.Globals;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class EventManager {

    private static final EventManager instance = new EventManager();

    private final Map<Class<? extends Event>, ArrayList> eventListeners;

    private EventManager() {
        eventListeners = new ConcurrentHashMap<Class<? extends Event>, ArrayList>();
    }

    public static EventManager getInstance() {
        return instance;
    }

    public <L> boolean isListening(Object object, Class<? extends Event<L>> eventClass) {
        return eventListeners.get(eventClass).contains(object);
    }

    public <L> void listen(Class<? extends Event<L>> eventClass, L listener) {
        ArrayList listeners = eventListeners.get(eventClass);
        if (listeners == null) {
            listeners = new ArrayList();
        }

        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }

        eventListeners.put(eventClass, listeners);
    }

    public <L> void notify(Event<L> event) {
        Gdx.app.debug(Globals.LOG_TAG, "Event triggered: " + event.toString());

        Class<Event<L>> eventClass = (Class<Event<L>>)event.getClass();
        if (eventListeners.containsKey(eventClass)) {
            for (L listener : (ArrayList<L>)eventListeners.get(eventClass)) {
                event.notify(listener);
            }
        }
    }

    public <L> void postNotify(final Event<L> event) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                EventManager.this.notify(event);
            }
        });
    }

    public <L> void mute(Class<? extends Event<L>> eventClass, L listener) {
        ArrayList listeners = eventListeners.get(eventClass);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public <L> void clear(Class<? extends Event<L>> eventClass) {
        eventListeners.remove(eventClass);
    }

    public void clearAll() {
        eventListeners.clear();
    }
}
