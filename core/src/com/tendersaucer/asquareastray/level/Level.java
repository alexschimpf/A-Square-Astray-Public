package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.tendersaucer.asquareastray.ColorScheme;
import com.tendersaucer.asquareastray.Shaders;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.FinalUnlockEvent;
import com.tendersaucer.asquareastray.event.TeleportEvent;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.InputListener;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.GravitySwitchEvent;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.listener.IGravitySwitchListener;
import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.screen.Tutorial;
import com.tendersaucer.asquareastray.utils.ColorUtils;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.object.Line;

import java.util.HashMap;
import java.util.Map;

public class Level extends Stage implements ILevelStateChangeListener, IGravitySwitchListener {

    public static final int PIXELS_PER_TILE = 64;
    public static final int TILES_PER_SCREEN_WIDTH = 16;
    public static final float MIN_VIEWPORT_WIDTH = 50;
    public static final float MIN_VIEWPORT_HEIGHT = 37.5f;
    public static final float MAX_PHYSICS_WORLD_TIME_STEP = 1 / 60.0f;
    public static final int PHYSICS_WORLD_VELOCITY_ITERATIONS = 5;
    public static final int PHYSICS_WORLD_POSITION_ITERATIONS = 5;
    public static final float DEFAULT_RESTITUTION = 0.2f;
    public static final Vector2 DEFAULT_GRAVITY = new Vector2(MIN_VIEWPORT_WIDTH * 0.14f, 0);
    public static final float COUNTDOWN_SECONDS = 3f;
    public static final float LINE_WIDTH = 0.8f;

    private int id;
    private float elapsed;
    private int timeLimit1;
    private int timeLimit2;
    private boolean skipCountdown;
    private LevelState state;
    private Player player;
    private InputListener inputListener;
    private ParticleEffectEmitter backgroundEmitter;
    private Timer.Task startLevelTask;
    private final Vector2 size;
    private final Vector2 center;
    private final ShaderProgram shaderProgram;
    private final World physicsWorld;
    private final Map<String, GameObject> gameObjectsByName;
    private final Array<GameObject> gameObjects;
    private final Canvas canvas;
    private final Sprite currLineSprite;
    private final Matrix4 debugMatrix;
    private final Box2DDebugRenderer debugRenderer;
    private final PolygonSpriteBatch polygonSpriteBatch;
    private final CameraHandler cameraHandler;
    private final ColorScheme colorScheme;
    private final ParticleEffectEmitter lineStartEmitter;

    public Level(int id, World physicsWorld) {
        super(new ExtendViewport(MIN_VIEWPORT_WIDTH, MIN_VIEWPORT_HEIGHT));

        setState(LevelState.LOADING);

        this.id = id;
        elapsed = 0;
        gameObjectsByName = new HashMap<>();
        gameObjects = new Array<>();
        this.physicsWorld = physicsWorld;
        debugMatrix = new Matrix4();
        debugRenderer = new Box2DDebugRenderer();
        polygonSpriteBatch = new PolygonSpriteBatch();
        currLineSprite = new Sprite();
        canvas = new Canvas();
        size = new Vector2();
        center = new Vector2();
        colorScheme = ColorScheme.getColorScheme();

        String fragmentShaderName = colorScheme.shaderName;
        String fragmentShader = Shaders.getShader(fragmentShaderName);
        String vertexShader = Shaders.getShader("vertex");
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        getBatch().setShader(shaderProgram);
        polygonSpriteBatch.setShader(shaderProgram);
        if (!getBatch().getShader().isCompiled()) {
            Gdx.app.log(Globals.LOG_TAG, getBatch().getShader().getLog());
        }

        lineStartEmitter = ParticleEffects.getEmitter(
                ParticleEffects.LINE_PLACEMENT, new Vector2());

        cameraHandler = new CameraHandler(this);
        EventManager.getInstance().listen(LevelStateChangeEvent.class, cameraHandler);
        EventManager.getInstance().listen(GravitySwitchEvent.class, cameraHandler);
        EventManager.getInstance().listen(TeleportEvent.class, cameraHandler);
        EventManager.getInstance().listen(FinalUnlockEvent.class, cameraHandler);
    }

    @Override
    public void onLevelStateChange(LevelState oldState, LevelState newState) {
        if (newState.equals(LevelState.DONE_FAILURE) || newState.equals(LevelState.DONE_SUCCESS)) {
            state = newState;

            if (startLevelTask != null) {
                startLevelTask.cancel();
            }

            if (newState.equals(LevelState.DONE_SUCCESS)) {
                AudioManager.playMusic(AudioManager.getInstance().successMusic);
                saveEarnedStars();
            } else {
                AudioManager.playMusic(AudioManager.getInstance().failureMusic);
            }
        } else if (newState.equals(LevelState.COUNTDOWN)) {
            scheduleLevelStart(COUNTDOWN_SECONDS);
        }
    }

    @Override
    public void onGravitySwitch(Vector2 oldGravity, Vector2 newGravity) {
        physicsWorld.setGravity(newGravity);
    }

    public void init(boolean skipCountdown) {
        Gdx.app.log(Globals.LOG_TAG, "Level initializing...");

        this.skipCountdown = skipCountdown;

        inputListener = new InputListener(this);
        addListener(inputListener);

        EventManager.getInstance().listen(LevelStateChangeEvent.class, this);
        EventManager.getInstance().listen(GravitySwitchEvent.class, this);

        initBackgroundEmitters();

        for (GameObject gameObject : gameObjects) {
            gameObject.init();
        }

        cameraHandler.init();
        player.getPhysicsBody().setType(BodyDef.BodyType.StaticBody);

        currLineSprite.setTexture(ColorUtils.getSolidColorDrawable(
                colorScheme.lineColor).getRegion().getTexture());

        handleUserSettings();

        if (Tutorial.showTutorial()) {
            setState(LevelState.TUTORIAL);
        } else if (this.skipCountdown) {
            scheduleLevelStart(0.5f);
        } else {
            setState(LevelState.COUNTDOWN);
        }
        Gdx.app.log(Globals.LOG_TAG, "Level done initializing");
    }

    public void update() {
        if (isRunning()) {
            elapsed += Time.getInstance().getDeltaTime() * 1000;
        }

        inputListener.checkInput();

        cameraHandler.update();
        Vector3 cameraPosition = getCamera().position;
        backgroundEmitter.setPosition(cameraPosition.x, cameraPosition.y);

        updatePhysicsWorld();
        updateGameObjects();
        act();
    }

    public void draw() {
        Color backgroundColor = colorScheme.backgroundColor1;
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Camera camera = getCamera();
        SpriteBatch batch = (SpriteBatch)getBatch();
        batch.setProjectionMatrix(camera.combined);
        polygonSpriteBatch.setProjectionMatrix(camera.combined);

        batch.begin();

        getRoot().draw(batch, 1);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        drawInProgressLine();
        canvas.render(batch, polygonSpriteBatch);

        batch.end();

        if (Globals.DEBUG_PHYSICS) {
            drawDebugPhysics();
        }
    }

    public void dispose() {
        Gdx.app.log(Globals.LOG_TAG, "Disposing of level...");

        EventManager.getInstance().mute(LevelStateChangeEvent.class, this);
        EventManager.getInstance().mute(GravitySwitchEvent.class, this);
        EventManager.getInstance().mute(LevelStateChangeEvent.class, cameraHandler);
        EventManager.getInstance().mute(GravitySwitchEvent.class, cameraHandler);
        EventManager.getInstance().mute(TeleportEvent.class, cameraHandler);
        EventManager.getInstance().mute(FinalUnlockEvent.class, cameraHandler);

        for (int i = gameObjects.size - 1; i >= 0; i--) {
            GameObject gameObject = gameObjects.get(i);
            gameObject.destroy();
        }
        // TODO: Figure out a safer way to do this
        // physicsWorld.dispose();
        clear();

        Gdx.app.log(Globals.LOG_TAG, "Done disposing of level");
    }

    public void cancelStartLevelStart() {
        // TODO: It'd probably be cleaner to handle cancelling these tasks via events
        if (startLevelTask != null) {
            startLevelTask.cancel();
        }
    }

    private void scheduleLevelStart(float secondsToStart) {
        startLevelTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                startLevel();
            }
        }, secondsToStart);
    }

    private void startLevel() {
        player.getPhysicsBody().setType(BodyDef.BodyType.DynamicBody);
        setState(LevelState.RUNNING);
    }

    public void setState(LevelState state) {
        if (this.state != state) {
            EventManager.getInstance().notify(new LevelStateChangeEvent(this.state, state));
            this.state = state;
        }
    }

    private void updatePhysicsWorld() {
        float frame_time = Time.getInstance().getDeltaTime();
        while (frame_time > 0) {
            float min_delta_time = Math.min(frame_time, MAX_PHYSICS_WORLD_TIME_STEP);
            physicsWorld.step(min_delta_time, PHYSICS_WORLD_VELOCITY_ITERATIONS,
                    PHYSICS_WORLD_POSITION_ITERATIONS);
            frame_time -= min_delta_time;
        }
    }

    private void updateGameObjects() {
        for (GameObject gameObject : gameObjects) {
            gameObject.update();
        }
    }

    private void drawDebugPhysics() {
        debugMatrix.set(getBatch().getProjectionMatrix());
        debugRenderer.render(physicsWorld, debugMatrix);
    }

    private void drawInProgressLine() {
        if (isDone() || inputListener.lineStart == null || inputListener.lineEnd == null) {
            return;
        }

        float cameraTop = getCameraTop();
        float cameraRight = getCameraRight();
        float x1 = cameraRight + inputListener.lineStart.x;
        float y1 = cameraTop + inputListener.lineStart.y;
        float x2 = cameraRight + inputListener.lineEnd.x;
        float y2 = cameraTop + inputListener.lineEnd.y;

        float length = (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        currLineSprite.setSize(length, LINE_WIDTH);
        currLineSprite.setRotation(MathUtils.radiansToDegrees * (float)Math.atan2(y2 - y1, x2 - x1));
        currLineSprite.setPosition(x1, y1);
        currLineSprite.setOrigin(0, 0);
        currLineSprite.draw(getBatch());

        float angle = (float)Math.atan2(y2 - y1, x2 - x1);
        float dx = LINE_WIDTH * MathUtils.sin(-angle) / 2;
        float dy = LINE_WIDTH * MathUtils.cos(-angle) / 2;
        lineStartEmitter.setPosition(x1 + dx, y1 + dy);
    }

    public void onLineDrawStart() {
        // A single emitter for all line start positions is kinda hacky
        // But it's barely noticeable so whatever...
        addActor(lineStartEmitter);
        lineStartEmitter.emit();
    }

    public void addLine(float x1, float y1, float x2, float y2) {
        addGameObject(new Line(this, x1, y1, x2, y2,
                Line.DEFAULT_LINE_DURATION, new Color(colorScheme.lineColor)));

        float angle = (float)Math.atan2(y2 - y1, x2 - x1);
        float dx = LINE_WIDTH * MathUtils.sin(-angle) / 2;
        float dy = LINE_WIDTH * MathUtils.cos(-angle) / 2;
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(
                ParticleEffects.LINE_PLACEMENT, new Vector2(x2 + dx, y2 + dy));
        addActor(emitter);
        emitter.emit();
    }

    public GameObject getGameObjectByName(String name) {
        return gameObjectsByName.get(name);
    }

    public void addGameObject(GameObject gameObject) {
        String name = gameObject.getName();
        if (name != null) {
            gameObjectsByName.put(name, gameObject);
        }
        gameObjects.add(gameObject);
        canvas.addToLayer(gameObject, gameObject.getLayer());
    }

    public void removeGameObject(GameObject gameObject) {
        if (gameObject.getName() != null) {
            gameObjectsByName.remove(gameObject.getName());
        }
        gameObjects.removeValue(gameObject, true);
        canvas.remove(gameObject);
    }

    public void resizeViewport(float width, float height) {
        getViewport().update((int)width, (int)height);
        getViewport().apply();
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getElapsedTime() {
        return elapsed;
    }

    public void setTimeLimit1(int timeLimit1) {
        this.timeLimit1 = timeLimit1;
    }

    public void setTimeLimit2(int timeLimit2) {
        this.timeLimit2 = timeLimit2;
    }

    public LevelState getState() {
        return state;
    }

    public boolean isDoneLoading() {
        return state != LevelState.LOADING;
    }

    public boolean hasStarted() {
        return state == LevelState.RUNNING || state == LevelState.DONE_FAILURE ||
                state == LevelState.DONE_SUCCESS;
    }

    public boolean isRunning() {
        return state == LevelState.RUNNING;
    }

    public boolean isDone() {
        return state == LevelState.DONE_FAILURE || state == LevelState.DONE_SUCCESS;
    }

    public InputListener getInputListener() {
        return inputListener;
    }

    public float getCameraTop() {
        Vector3 cameraPos = getCamera().position;
        return cameraPos.y + (getCamera().viewportHeight / 2);
    }

    public float getCameraBottom() {
        Vector3 cameraPos = getCamera().position;
        return cameraPos.y - (getCamera().viewportHeight / 2);
    }

    public float getCameraRight() {
        Vector3 cameraPos = getCamera().position;
        return cameraPos.x + (getCamera().viewportWidth / 2);
    }

    public float getCameraLeft() {
        Vector3 cameraPos = getCamera().position;
        return cameraPos.x - (getCamera().viewportWidth / 2);
    }

    public Vector2 getCameraVelocity() {
        return cameraHandler.getCameraVelocity();
    }

    public CameraHandler getCameraHandler() {
        return cameraHandler;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public int getId() {
        return id;
    }

    public void setSize(float width, float height) {
        size.set(width, height);
    }

    public Vector2 getSize() {
        return size;
    }

    public void setCenter(float x, float y) {
        center.set(x, y);
    }

    public Vector2 getCenter() {
        return center;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public int getEarnedStars() {
        int elapsedSeconds = (int)(elapsed / 1000);

        int numStars = 1;
        if (elapsedSeconds <= timeLimit1) {
            numStars = 3;
        } else if (elapsedSeconds <= timeLimit2) {
            numStars = 2;
        }

        return numStars;
    }

    private void initBackgroundEmitters() {
        Vector3 cameraPos = getCamera().position;
        backgroundEmitter = ParticleEffects.getEmitter(
                ParticleEffects.GAME_BACKGROUND, new Vector2(cameraPos.x, cameraPos.y), 5000);
        backgroundEmitter.getEffect().setStartColorRange(
                new Color(colorScheme.minBackgroundColor2), new Color(colorScheme.maxBackgroundColor2));
        backgroundEmitter.getEffect().setGrayscale(colorScheme.isBackgroundGrayscale);

        backgroundEmitter.getEffect().setFadeInRatio(null);
        backgroundEmitter.getEffect().setFadeOutRatio(null);

        addActor(backgroundEmitter);
        backgroundEmitter.emit();

        backgroundEmitter.getEffect().setFadeInRatio(0.5f);
        backgroundEmitter.getEffect().setFadeOutRatio(0.5f);

        ParticleEffectEmitter emitter2 = ParticleEffects.getEmitter(
                ParticleEffects.GAME_BACKGROUND_2, new Vector2(center), 10000);
        emitter2.getEffect().setStartColorRange(
                new Color(colorScheme.minBackgroundColor3), new Color(colorScheme.maxBackgroundColor3));
        emitter2.getEffect().setGrayscale(colorScheme.isBackgroundGrayscale);
        emitter2.getEffect().setPositionOffsetRange(
            new Vector2(
                -size.x * 0.5f - ConversionUtils.getMetersPerScreenWidth(),
                -size.y * 0.5f - ConversionUtils.getMetersPerScreenHeight()),
            new Vector2(
                size.x * 0.5f + ConversionUtils.getMetersPerScreenWidth(),
                size.y * 0.5f + ConversionUtils.getMetersPerScreenHeight())
        );
        float metersPerScreenWidth = ConversionUtils.getMetersPerScreenWidth();
        float metersPerScreenHeight = ConversionUtils.getMetersPerScreenHeight();
        float numScreensPerLevelLength =
                Math.max(size.x, size.y) / Math.max(metersPerScreenWidth, metersPerScreenHeight);
        int numParticles = (int)(numScreensPerLevelLength * 300);
        emitter2.getEffect().setNumParticlesRange(numParticles, numParticles);

        addActor(emitter2);
        emitter2.emit();
    }

    private void saveEarnedStars() {
        String key = "level_" + id + "_stars";
        int numStars = getEarnedStars();
        int currNumStarsForLevel = Persistence.getInstance().getInteger(key, 0);
        if (numStars > currNumStarsForLevel) {
            Persistence.getInstance().putInteger(key, numStars);

            int newlyEarnedStars = numStars - currNumStarsForLevel;
            Persistence.getInstance().increment("total_earned_stars", newlyEarnedStars);
        }
    }

    private void handleUserSettings() {
        if (Persistence.getInstance().getBoolean("settings_enable_left_handed_mode", false)) {
            getCamera().rotate(new Vector3(0, 1, 0), 180);
        }
    }
}
