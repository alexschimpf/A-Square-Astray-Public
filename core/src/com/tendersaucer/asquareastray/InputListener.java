package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.screen.HUD;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.utils.Screenshot;

public class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {

    private static final int DOUBLE_TAP_INTERVAL = 200;
    private static final float MIN_LINE_LENGTH = ConversionUtils.getMetersPerTile() * 0.2f;

    private Level level;
    private HUD hud;
    public Vector2 lineStart;
    public Vector2 lineEnd;
    private long lastTapTime;

    public InputListener(Level level) {
        this.level = level;
    }

    public void checkInput() {
        OrthographicCamera camera = (OrthographicCamera)level.getCamera();
        if (Globals.DEBUG_MODE) {
            if (Gdx.input.isKeyPressed(Keys.Z)) {
                camera.zoom -= 0.05;
            } else if (Gdx.input.isKeyPressed(Keys.X)) {
                camera.zoom += 0.05;
            } else if (Gdx.input.isKeyPressed(Keys.D)) {
                camera.position.x += 1;
            } else if (Gdx.input.isKeyPressed(Keys.A)) {
                camera.position.x -= 1;
            } else if (Gdx.input.isKeyPressed(Keys.W)) {
                camera.position.y += 1;
            } else if (Gdx.input.isKeyPressed(Keys.S)) {
                camera.position.y -= 1;
            } else if (Gdx.input.isKeyPressed(Keys.UP)) {
                Time.getInstance().setTimeScale(MathUtils.clamp(Time.getInstance().getTimeScale() + 0.01f, 0, 1));
            } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                Time.getInstance().setTimeScale(MathUtils.clamp(Time.getInstance().getTimeScale() - 0.01f, 0, 1));
            }
        }
    }

    @Override
    public boolean keyDown(InputEvent event, int keyCode) {
        switch(keyCode) {
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.Q:
                if (Globals.DEBUG_MODE) {
                    Globals.DEBUG_PHYSICS = !Globals.DEBUG_PHYSICS;
                }
                break;
            case Keys.P:
                if (Globals.DEBUG_MODE) {
                    EventManager.getInstance().notify(new LevelStateChangeEvent(
                            level.getState(), LevelState.DONE_SUCCESS));
                }
                break;
            case Keys.O:
                if (Globals.DEBUG_MODE) {
                    EventManager.getInstance().notify(new LevelStateChangeEvent(
                            level.getState(), LevelState.DONE_FAILURE));
                }
                break;
            case Keys.ENTER:
                if (Globals.DEBUG_MODE) {
                    Screenshot.takeScreenshot();
                }
                break;
        }

        return true;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (!level.isRunning()) {
            return false;
        }

        if (hud != null && TimeUtils.timeSinceMillis(lastTapTime) < DOUBLE_TAP_INTERVAL) {
            hud.startDash();
        } else {
            float dx = x - level.getCameraRight();
            float dy = y - level.getCameraTop();
            lineStart = new Vector2(dx, dy);
            lineEnd = new Vector2(dx, dy);

            level.onLineDrawStart();
        }

        lastTapTime = TimeUtils.millis();

        return true;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        if (lineStart != null) {
            float newX = level.getCameraRight() + lineStart.x;
            float newY = level.getCameraTop() + lineStart.y;
            if (new Vector2(newX - x, newY - y).len() > MIN_LINE_LENGTH) {
                level.addLine(newX, newY, x, y);
            }
        }

        lineStart = null;
        lineEnd = null;
    }

    @Override
    public void touchDragged (InputEvent event, float x, float y, int pointer) {
        if (lineEnd != null) {
            float dx = x - level.getCameraRight();
            float dy = y - level.getCameraTop();
            lineEnd.set(dx, dy);
        }
    }

    public void setHUD(HUD hud) {
        this.hud = hud;
    }
}
