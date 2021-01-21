package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class Strings {

    private static final Strings instance = new Strings();

    private I18NBundle bundle;

    private Strings() {
        FileHandle fileHandle = Gdx.files.internal("bundles/bundle");
        bundle = I18NBundle.createBundle(fileHandle);
    }

    public static Strings getInstance() {
        return instance;
    }

    public String get(String key, Object... args) {
        return bundle.format(key, args);
    }

    public void setLocale(Locale locale) {
        FileHandle fileHandle = Gdx.files.internal("bundles/bundle");
        bundle = I18NBundle.createBundle(fileHandle, locale);
    }
}
