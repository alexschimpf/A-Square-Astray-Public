package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.event.listener.IFinalUnlockListener;
import com.tendersaucer.asquareastray.event.listener.ITeleportListener;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.GravitySwitchEvent;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.listener.IGravitySwitchListener;
import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.utils.Tween;

public class CameraHandler implements ILevelStateChangeListener, IGravitySwitchListener,
        ITeleportListener, IFinalUnlockListener {

    public static final float DEFAULT_CAMERA_SPEED = Level.MIN_VIEWPORT_WIDTH * 0.18f;
    public static final float REPOSITION_DURATION = 1000;
    public static final float MOVE_TO_TARGET_DURATION = 250;
    private static final float GRAVITY_SWITCH_DELAY = 0.15f;
    private static final float LEVEL_START_ZOOM_OUT_DURATION = 300;

    private Vector2 startPosition;
    private Vector2 targetPosition;
    private boolean isReturningFromTarget;
    private boolean isReturnFromTargetEnabled;
    private Long moveToTargetStartTime;
    private Long repositionStartTime;
    private Array<Tween> zoomTweens;
    private boolean trackPlayer;
    private final Level level;
    private final Vector2 cameraVelocity;
    private final CameraShake cameraShake;

    public CameraHandler(Level level) {
        this.level = level;
        trackPlayer = true;
        repositionStartTime = null;
        cameraVelocity = new Vector2();
        cameraShake = new CameraShake();
        zoomTweens = new Array<>();

        OrthographicCamera camera = (OrthographicCamera)level.getCamera();
        camera.setToOrtho(false, Level.MIN_VIEWPORT_WIDTH, Level.MIN_VIEWPORT_HEIGHT);
    }

    @Override
    public void onLevelStateChange(LevelState oldState, LevelState newState) {
        if (newState.equals(LevelState.DONE_FAILURE) || newState.equals(LevelState.DONE_SUCCESS)) {
            trackPlayer = false;

            final CameraHandler self = this;
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    EventManager.getInstance().mute(LevelStateChangeEvent.class, self);
                    EventManager.getInstance().mute(GravitySwitchEvent.class, self);
                }
            });
        }
        if (newState.equals(LevelState.DONE_SUCCESS)) {
            cameraVelocity.scl(0.5f);
        }
        if (oldState.equals(LevelState.LOADING) && newState.equals(LevelState.RUNNING)) {
            addZoomTween(new Tween(0, 1, LEVEL_START_ZOOM_OUT_DURATION));
        }
    }

    @Override
    public void onGravitySwitch(final Vector2 oldGravity, final Vector2 newGravity) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!oldGravity.equals(newGravity)) {
                    float cameraSpeed = Level.DEFAULT_GRAVITY.len();

                    float correction = 1;
                    float baseLength = Math.min(Level.MIN_VIEWPORT_WIDTH, Level.MIN_VIEWPORT_HEIGHT) * 2;
                    if (Math.abs(newGravity.x) > 0) {
                        correction = level.getWidth() / baseLength;
                    } else if (Math.abs(newGravity.y) > 0) {
                        correction = level.getHeight() / baseLength;
                    }

                    cameraVelocity.set(newGravity).nor().scl(cameraSpeed).scl(correction);
                    repositionStartTime = TimeUtils.millis();
                }
            }
        }, GRAVITY_SWITCH_DELAY);
    }

    @Override
    public void onTeleport(Vector2 oldPos, Vector2 newPos) {
        Camera camera = level.getCamera();
        startPosition = new Vector2(camera.position.x, camera.position.y);

        targetPosition = new Vector2(newPos);
        isReturnFromTargetEnabled = false;
        moveToTargetStartTime = TimeUtils.millis();

        cameraShake.stop();
    }

    @Override
    public void onFullUnlock(String doorName) {
        Camera camera = level.getCamera();
        startPosition = new Vector2(camera.position.x, camera.position.y);

        GameObject door = level.getGameObjectByName(doorName);
        targetPosition = new Vector2(door.getCenterX(), door.getCenterY());
        isReturnFromTargetEnabled = true;
        moveToTargetStartTime = TimeUtils.millis();

        cameraShake.stop();
    }

    public void init() {
        Vector3 cameraPos = level.getCamera().position;
        Player player = level.getPlayer();
        cameraPos.set(player.getCenterX(), player.getCenterY(), 0);
    }

    public void update() {
        updateCameraZoom();
        updateCameraPosition();
        handleCameraShake();
        level.getCamera().update();
    }

    public void setCameraVelocity(float vx, float vy) {
        /*
         * When using ExtendViewport, the game world is first scaled to fit inside the viewport.
         * Then the shorter dimension is extended to fill the remaining empty space.
         *
         * Suppose the screen size is 1200x600
         * We set the min viewport "world" size to be 50x37.5.
         * So the game world is first scaled to fit within the viewport.
         * Thus, it gets scaled to 50x25.
         * Then the height (i.e. the shorter dimension) is extended to fill the remaining space.
         * Thus, the height scales to 37.5, and the width scales to 75 to keep the same aspect ratio.
         *
         * However, if the screen size is say, 600x600, the viewport size will become 50x50.
         * Compared to someone with a screen size of 1200x600, this user will see less of the world
         * horizontally and more of the world vertically.
         *
         * To account for this, we need to make sure the camera speed scales accordingly.
         * If the camera speed is 10m/s, it will take longer for it to catch up to the player
         * on wider screens (since it has more distance to cover).
         *
         * Since most testing was done with a 1200x600 screen size (and most movement is horizontal),
         * we'll use a "base length" of 75. So for a viewport length of 75m, the camera we'll move at 10m/s.
         * If the viewport length is shorter in a direction, say 37.5m, we'll halve the camera speed so that
         * it will take the same amount of time for the camera to cover the length of the viewport.
         */
        float correction = 1;
        float baseLength = Math.min(Level.MIN_VIEWPORT_WIDTH, Level.MIN_VIEWPORT_HEIGHT) * 2;
        if (Math.abs(vx) > 0) {
            correction = level.getWidth() / baseLength;
        } else if (Math.abs(vy) > 0) {
            correction = level.getHeight() / baseLength;
        }

        cameraVelocity.set(vx, vy).scl(correction);
    }

    public Vector2 getCameraVelocity() {
        return cameraVelocity;
    }

    public boolean isRepositioning() {
        return repositionStartTime != null;
    }

    public boolean isMovingToTarget() {
        return moveToTargetStartTime != null;
    }

    public void shakeCamera(float power, float duration) {
        cameraShake.shake(power, duration);
    }

    public void shakeCamera(float power, float duration, boolean force) {
        cameraShake.shake(power, duration, force);
    }

    public void addZoomTween(Tween zoomTween) {
        this.zoomTweens.add(zoomTween);
    }

    private void updateCameraZoom() {
        if (zoomTweens.size > 0) {
            Tween zoomTween = zoomTweens.get(0);
            float dt = Time.getInstance().getDeltaTime();
            float zoom = zoomTween.get(dt);
            if (zoomTween.isDone()) {
                zoomTweens.removeIndex(0);
            }
            ((OrthographicCamera)level.getCamera()).zoom = zoom;
        }
    }

    private void updateCameraPosition() {
        if (level.hasStarted()) {
            if (isMovingToTarget()) {
                moveToTarget();
            } else if (trackPlayer) {
                if (isRepositioning()) {
                    reposition();
                } else {
                    trackPlayer();
                }
            } else {
                advance();
            }
        }
    }

    /**
     * TODO: Clean up this disgusting code!
     */
    private void moveToTarget() {
        float elapsed = TimeUtils.timeSinceMillis(moveToTargetStartTime);
        float progress = elapsed / MOVE_TO_TARGET_DURATION;

        float waitTime = 750;
        float progressWithWait = elapsed / (MOVE_TO_TARGET_DURATION + waitTime);

        Camera camera = level.getCamera();
        if (progress <= 1) {
            float clampedProgress = MathUtils.clamp(progress, 0, 1);;
            float x = MathUtils.lerp(startPosition.x, targetPosition.x, clampedProgress);
            float y = MathUtils.lerp(startPosition.y, targetPosition.y, clampedProgress);
            camera.position.set(x, y, 0);
        } else if (progressWithWait >= 1) {
            if (isReturningFromTarget) {
                moveToTargetStartTime = null;
                isReturningFromTarget = false;
                EventManager.getInstance().notify(new TimeScaleEvent(
                        new Tween(0, Time.getInstance().getMaxTimeScale(), 0.01f)));
            } else {
                if (isReturnFromTargetEnabled) {
                    isReturningFromTarget = true;
                    startPosition = new Vector2(camera.position.x, camera.position.y);
                    Player player = level.getPlayer();
                    if (player != null) {
                        targetPosition = new Vector2(player.getCenterX(), player.getCenterY());
                        moveToTargetStartTime = TimeUtils.millis();
                    }
                } else {
                    moveToTargetStartTime = null;
                    EventManager.getInstance().notify(new TimeScaleEvent(
                            new Tween(0, Time.getInstance().getMaxTimeScale(), 0.01f)));
                }
            }
        }
    }

    private void reposition() {
        float elapsed = TimeUtils.timeSinceMillis(repositionStartTime);
        float progress = MathUtils.clamp(elapsed / REPOSITION_DURATION, 0, 1);

        Camera camera = level.getCamera();
        Player player = level.getPlayer();
        float x = MathUtils.lerp(camera.position.x, player.getCenterX(), progress);
        float y = MathUtils.lerp(camera.position.y, player.getCenterY(), progress);
        camera.position.set(x, y, 0);

        if (progress >= 1) {
            repositionStartTime = null;
        }
    }

    private void trackPlayer() {
        float x, y;
        Camera camera = level.getCamera();
        Player player = level.getPlayer();
        if (Math.abs(cameraVelocity.x) > 0) {
            y = player.getCenterY();
            if (cameraVelocity.x > 0) {
                x = Math.max(camera.position.x + (cameraVelocity.x * Time.getInstance().getDeltaTime()),
                        player.getCenterX() - (camera.viewportWidth / 7.0f));
            } else {
                x = Math.min(camera.position.x + (cameraVelocity.x * Time.getInstance().getDeltaTime()),
                        player.getCenterX() + (camera.viewportWidth / 7.0f));
            }
        } else {
            x = player.getCenterX();
            if (cameraVelocity.y > 0) {
                y = Math.max(camera.position.y + (cameraVelocity.y * Time.getInstance().getDeltaTime()),
                        player.getCenterY() - (camera.viewportHeight / 7.0f));
            } else {
                y = Math.min(camera.position.y + (cameraVelocity.y * Time.getInstance().getDeltaTime()),
                        player.getCenterY() + (camera.viewportHeight / 7.0f));
            }
        }
        camera.position.set(x, y, 0);
    }

    private void advance() {
        Camera camera = level.getCamera();
        float x = camera.position.x + (cameraVelocity.x * Time.getInstance().getDeltaTime());
        float y = camera.position.y + (cameraVelocity.y * Time.getInstance().getDeltaTime());
        camera.position.set(x, y, 0);
    }

    private void handleCameraShake() {
        Camera camera = level.getCamera();
        if (cameraShake.isShaking()) {
            cameraShake.update();
            camera.translate(cameraShake.getTranslation());
        }
    }
}
