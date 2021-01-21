package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.pay.PurchaseManagerConfig;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.asquareastray.ads.IAdListener;
import com.tendersaucer.asquareastray.ads.IAdManager;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.event.SettingsChangeEvent;
import com.tendersaucer.asquareastray.event.StartLevelEvent;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.event.listener.IScreenChangeListener;
import com.tendersaucer.asquareastray.event.listener.IStartLevelListener;
import com.tendersaucer.asquareastray.purchase.Offers;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;
import com.tendersaucer.asquareastray.screen.GameScreen;
import com.tendersaucer.asquareastray.screen.MainMenuScreen;

public class Game extends com.badlogic.gdx.Game implements IScreenChangeListener, IStartLevelListener, IAdListener {

    private int levelId;
    private boolean skipCountdown;
    private final IAdManager adManager;

    public Game(IAdManager adManager) {
        this.adManager = adManager;
    }

    @Override
    public void dispose() {
        super.dispose();

        AudioManager.getInstance().dispose();
        AssetManager.getInstance().dispose();
        Fonts.dispose();
        PurchaseManager.getInstance().dispose();
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Globals.LOG_LEVEL);

        EventManager.getInstance().listen(ScreenChangeEvent.class, this);
        EventManager.getInstance().listen(StartLevelEvent.class, this);
        EventManager.getInstance().listen(LevelStateChangeEvent.class, Time.getInstance());
        EventManager.getInstance().listen(TimeScaleEvent.class, Time.getInstance());
        EventManager.getInstance().listen(SettingsChangeEvent.class, Time.getInstance());

        AssetManager.getInstance().load();
        EventManager.getInstance().listen(SettingsChangeEvent.class, AudioManager.getInstance());

        adManager.addAdListener(this);
        adManager.addAdListener(AudioManager.getInstance());

        initPurchaseManager();

        if (Globals.CLEAR_DATA) {
            Persistence.getInstance().clear();
        }
        if (Globals.UNLOCK_ALL_LEVELS) {
            Persistence.getInstance().putInteger("level_id", Globals.NUM_LEVELS);
        }

        if (Globals.SKIP_MAIN_MENU) {
            int levelId = Persistence.getInstance().getInteger("level_id", 1);
            setScreen(new GameScreen(levelId));
        } else {
            setScreen(new MainMenuScreen());
        }
    }

    @Override
    public void onScreenChange(Screen screen) {
        getScreen().dispose();
        setScreen(screen);
    }

    @Override
    public void onStartLevel(int levelId, boolean skipCountdown) {
        Persistence.getInstance().increment("level_" + levelId + "_play_count");

        this.levelId = levelId;
        this.skipCountdown = skipCountdown;

        if (shouldShowAd() && adManager.showAd()) {
            return;
        }

        startLevel();
    }

    @Override
    public void onAdOpened() {
    }

    @Override
    public void onAdClosed() {
        startLevel();
    }

    private void startLevel() {
        if (Gdx.graphics.getWidth() < Gdx.graphics.getHeight()) {
            Gdx.app.log(Globals.LOG_TAG, "Tried to start level, but screen is in portrait mode. Retrying...");

            // This is a BIG hack to handle the case when a level tries to
            // load right after an interstitial ad loads in portrait mode
            // ... not sure why an ad would load in portrait mode but whatever
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    startLevel();
                }
            }, 0.2f);

            return;
        }

        getScreen().dispose();
        GameScreen screen = new GameScreen(levelId, skipCountdown);
        setScreen(screen);
    }

    private void initPurchaseManager() {
        PurchaseManagerConfig config = new PurchaseManagerConfig();
        config.addOffer(Offers.REMOVE_ADS);
        PurchaseManager.getInstance().install(config);
    }

    private boolean shouldShowAd() {
        boolean purchasedAdFreeMode = Persistence.getInstance().getBoolean("purchased_remove_ads", false);
        return Globals.ENABLE_ADS && !purchasedAdFreeMode && Math.random() < 0.07f;
    }
}
