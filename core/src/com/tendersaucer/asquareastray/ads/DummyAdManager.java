package com.tendersaucer.asquareastray.ads;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.asquareastray.Globals;

public class DummyAdManager implements IAdManager {

    @Override
    public boolean showAd() {
        Gdx.app.log(Globals.LOG_TAG, "Showing ad...");
        return false;
    }

    @Override
    public void addAdListener(IAdListener listener) { }

    @Override
    public void removeAdListener(IAdListener listener) { }
}
