package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.SettingsChangeEvent;
import com.tendersaucer.asquareastray.utils.StringUtils;

import java.util.Map;

public class Persistence {

    public static final Persistence instance = new Persistence();

    private final Preferences preferences;
    private Map<String, ?> preferencesCache;

    public Persistence() {
        preferences = Gdx.app.getPreferences("com.tendersaucer.asquareastray");
        loadFromPreferences();
    }

    public static Persistence getInstance() {
        return instance;
    }

    public boolean containsKey(String key) {
        return preferences.contains(key);
    }

    public String getString(String key, String defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return preferencesCache.get(key).toString();
    }

    public long getLong(String key, long defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Long.parseLong(preferencesCache.get(key).toString());
    }

    public int getInteger(String key, int defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Integer.parseInt(preferencesCache.get(key).toString());
    }

    public float getFloat(String key, float defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Float.parseFloat(preferencesCache.get(key).toString());
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        String value = preferencesCache.get(key).toString();
        return value != null && StringUtils.equalsAny(value.toLowerCase(), "1", "true");
    }

    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void putLong(String key, long value) {
        preferences.putLong(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void putFloat(String key, float value) {
        preferences.putFloat(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void putString(String key, String value) {
        preferences.putString(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void putInteger(String key, int value) {
        preferences.putInteger(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void increment(String key) {
        add(key, 1);
    }

    public void increment(String key, int amount) {
        add(key, amount);
    }

    public void add(String key, long amount) {
        long value = amount;
        if (containsKey(key)) {
            long curr = getLong(key, 0);
            value += curr;
        }
        preferences.putLong(key, value);
        preferences.flush();
        loadFromPreferences();

        EventManager.getInstance().notify(new SettingsChangeEvent(key, value));
    }

    public void remove(String key) {
        if (containsKey(key)) {
            preferences.remove(key);
        }

        preferences.flush();
        loadFromPreferences();
    }

    public void clear() {
        preferences.clear();
        preferences.flush();
        preferencesCache.clear();
    }

    private void loadFromPreferences() {
        preferencesCache = preferences.get();
    }
}
