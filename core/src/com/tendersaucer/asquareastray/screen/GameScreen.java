package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.StartLevelEvent;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.level.LevelLoader;
import com.tendersaucer.asquareastray.level.LevelState;

public class GameScreen implements Screen, ILevelStateChangeListener {

    private int levelId;
    private boolean skipCountdown;
    private Level level;
    private HUD hud;
    private Vector2 viewport;

    public GameScreen(int levelId, boolean skipCountdown) {
        this.levelId = levelId;
        this.skipCountdown = skipCountdown;

        viewport = new Vector2();

        AssetManager.getInstance().load();
        EventManager.getInstance().listen(LevelStateChangeEvent.class, this);

        loadLevel(levelId, skipCountdown);
    }

    public GameScreen(int levelId) {
        this(levelId, false);
    }

    @Override
    public void show() {
        Gdx.app.log(Globals.LOG_TAG, "Showing game screen...");
    }

    @Override
    public void render(float delta) {
        if (level != null && level.isDoneLoading()) {
            Time.getInstance().update();

            level.update();
            hud.update();

            level.draw();
            hud.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(Globals.LOG_TAG, "Resizing: " + width + " x " + height);

        viewport.set(width, height);
        if (level != null) {
            level.resizeViewport(width, height);
        }
        if (hud != null) {
            hud.resizeViewport(width, height);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        EventManager.getInstance().mute(LevelStateChangeEvent.class, this);

        hud.dispose();
        level.dispose();
    }

    @Override
    public void onLevelStateChange(LevelState oldState, LevelState newState) {
        if (newState.equals(LevelState.DONE_SUCCESS)) {
            int lastUnlockedLevelId = Persistence.getInstance().getInteger("level_id", 1);
            if (levelId == lastUnlockedLevelId && levelId != Globals.NUM_LEVELS) {
                final int levelId = this.levelId + 1;
                Timer.post(new Timer.Task() {
                    @Override
                    public void run() {
                        Persistence.getInstance().putInteger("level_id", levelId);
                    }
                });
            }
        }
        if (newState.equals(LevelState.DONE_FAILURE) || newState.equals(LevelState.DONE_SUCCESS)) {
            Gdx.input.setInputProcessor(hud);
        }
    }

    private void loadLevel(int levelId, boolean skipCountdown) {
        long loadStartTime = TimeUtils.millis();

        Gdx.app.log(Globals.LOG_TAG, "Loading level " + levelId + "...");

        this.levelId = levelId;
        this.skipCountdown = skipCountdown;

        if (level != null) {
            level.dispose();
        }
        if (hud != null) {
            hud.dispose();
        }

        LevelLoader levelLoader = new LevelLoader();
        try {
            long startTime = TimeUtils.millis();
            Level newLevel = levelLoader.loadLevel(levelId);
            Gdx.app.log(Globals.LOG_TAG, "loadLevel took " + TimeUtils.timeSinceMillis(startTime) + " ms");

            if (level != null) {
                newLevel.resizeViewport(viewport.x, viewport.y);
            }

            startTime = TimeUtils.millis();
            hud = new HUD(newLevel);
            Gdx.app.log(Globals.LOG_TAG, "HUD constructor took " + TimeUtils.timeSinceMillis(startTime) + " ms");

            EventManager.getInstance().listen(LevelStateChangeEvent.class, hud);

            level = newLevel;

            startTime = TimeUtils.millis();
            level.init(skipCountdown);
            Gdx.app.log(Globals.LOG_TAG, "level.init took " + TimeUtils.timeSinceMillis(startTime) + " ms");

            level.getInputListener().setHUD(hud);

            InputMultiplexer inputMultiplexer = new InputMultiplexer();
            inputMultiplexer.addProcessor(hud);
            inputMultiplexer.addProcessor(level);
            Gdx.input.setInputProcessor(inputMultiplexer);

            long loadTime = TimeUtils.timeSinceMillis(loadStartTime);
            Gdx.app.log(Globals.LOG_TAG, "Loaded level " + levelId + " in " + loadTime + " ms");
        } catch(Exception e) {
            Gdx.app.error(Globals.LOG_TAG, e.getMessage(), e);
            Gdx.app.exit();
        }
    }
}
