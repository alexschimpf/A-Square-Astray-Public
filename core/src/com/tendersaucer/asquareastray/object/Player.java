package com.tendersaucer.asquareastray.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.asquareastray.level.CameraHandler;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.TeleportEvent;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.event.listener.ITeleportListener;
import com.tendersaucer.asquareastray.level.CollisionFilters;
import com.tendersaucer.asquareastray.component.Component;
import com.tendersaucer.asquareastray.component.FatalComponent;
import com.tendersaucer.asquareastray.component.ability.Ability;
import com.tendersaucer.asquareastray.component.ability.AbilityType;
import com.tendersaucer.asquareastray.component.ability.DashAbility;
import com.tendersaucer.asquareastray.event.AbilityActivatedEvent;
import com.tendersaucer.asquareastray.event.AbilityDeactivatedEvent;
import com.tendersaucer.asquareastray.event.AutoDashEvent;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.listener.IAbilityActivatedListener;
import com.tendersaucer.asquareastray.event.listener.IAbilityDeactivatedListener;
import com.tendersaucer.asquareastray.event.listener.IAutoDashListener;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.object.actions.Actions;
import com.tendersaucer.asquareastray.object.actions.ScaleToAction;
import com.tendersaucer.asquareastray.particle.ParticleEffect;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.utils.Tween;

public class Player extends GameObject implements IAbilityActivatedListener, IAbilityDeactivatedListener,
        IAutoDashListener, ITeleportListener {

    public static final float DEFAULT_MAX_SPEED = 13;
    private static final int COLLIDE_SOUND_COOLDOWN = 250;
    private static final int FULL_VOLUME_COLLIDE_SOUND_COOLDOWN = 500;

    private float maxSpeed;
    private boolean isAutoDashing;
    private int currCollisionSoundId;
    private boolean isAbilityActivated;
    private long lastCollideSoundTime;
    private float autoDashElapsed;
    private Fixture leftSensor, rightSensor, topSensor, bottomSensor, centerSensor;
    private boolean isLeftSensorActivated, isRightSensorActivated, isTopSensorActivated, isBottomSensorActivated;
    private final ParticleEffectEmitter emitter;

    public Player(Level level, Shape2D shape, Properties properties) {
        super(level, shape, properties);

        maxSpeed = DEFAULT_MAX_SPEED;
        layer = 1;
        isLeftSensorActivated = false;
        isRightSensorActivated = false;
        isTopSensorActivated = false;
        isBottomSensorActivated = false;
        isAutoDashing = false;
        currCollisionSoundId = GameObject.DEFAULT_LAYER - 1;
        sprite = AssetManager.getInstance().getSprite("player");
        sprite.setColor(new Color(level.getColorScheme().playerColor));
        properties.put("disable_outlines", true);

        emitter = ParticleEffects.getEmitter(ParticleEffects.PLAYER, new Vector2(), 1000);
    }

    public static boolean isPlayer(Contact contact, GameObject gameObject, boolean isObjectA) {
        Fixture fixture = isObjectA ? contact.getFixtureB() : contact.getFixtureA();
        return gameObject instanceof Player && !fixture.isSensor();
    }

    public static boolean isPlayerCenter(Contact contact, GameObject gameObject, boolean isObjectA) {
        Fixture fixture = isObjectA ? contact.getFixtureB() : contact.getFixtureA();
        return gameObject instanceof Player && fixture == ((Player)gameObject).centerSensor;
    }

    @Override
    public void init() {
        super.init();

        physicsBody.setFixedRotation(false);
        physicsBody.setBullet(true);
        physicsBody.setSleepingAllowed(false);

        createSensors();

        EventManager.getInstance().listen(AbilityActivatedEvent.class, this);
        EventManager.getInstance().listen(AbilityDeactivatedEvent.class, this);
        EventManager.getInstance().listen(AutoDashEvent.class, this);
        EventManager.getInstance().listen(TeleportEvent.class, this);

        // TODO: Do something about this???
        DashAbility dashAbility = new DashAbility(
                level, this, DashAbility.DEFAULT_SPEED,
                DashAbility.DEFAULT_DASH_DURATION, DashAbility.DEFAULT_DASH_COOLDOWN);
        dashAbility.init();
        addComponent(dashAbility);

        initEmitter();
    }

    @Override
    public void update() {
        super.update();

        // TODO: Is this actually needed?
        if (isDone) {
            return;
        }

        if (checkSideSensors()) return;
        if (checkCameraPosition()) return;

        emitter.setPosition(getCenterX(), getCenterY());
        emitter.setRotation(MathUtils.radiansToDegrees * getAngle());


        if (!isDashing()) {
            Vector2 currVelocity = physicsBody.getLinearVelocity();

            float vx = currVelocity.x;
            if (Math.abs(vx) > maxSpeed) {
                vx = maxSpeed * Math.signum(vx);
            }
            float vy = currVelocity.y;
            if (Math.abs(vy) > maxSpeed) {
                vy = maxSpeed * Math.signum(vy);
            }

            physicsBody.setLinearVelocity(vx, vy);
        }

        if (isAutoDashing) {
            autoDashElapsed += Time.getInstance().getDeltaTime() * 1000;
            if (autoDashElapsed > DashAbility.DEFAULT_DASH_DURATION) {
                autoDashElapsed = 0;
                isAutoDashing = false;
            }
        }
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        super.onBeginContact(contact, gameObject, isObjectA);

        Fixture ownFixture = isObjectA ? contact.getFixtureA() : contact.getFixtureB();
        if (isSideSensor(ownFixture)) {
            handleSensorCollision(contact, gameObject, isObjectA, true);
        } else if (ownFixture != centerSensor) {
            if (!level.isDone() && level.isDoneLoading() &&
                    TimeUtils.timeSinceMillis(lastCollideSoundTime) > COLLIDE_SOUND_COOLDOWN &&
                    (gameObject == null || !gameObject.isContactDisabled())) {
                playCollisionSound();
                lastCollideSoundTime = TimeUtils.millis();

                if (hasAction(ScaleToAction.class)) {
                    if (isDashing()) {
                        // TODO: Clean this up at some point
                        clearActions();
                        addAction(Actions.scaleTo(1, 1, 0.3f));
                    }
                } else {
                    addAction(Actions.sequence(
                        Actions.scaleTo(0.88f, 0.88f, 0.15f, Interpolation.slowFast),
                        Actions.scaleTo(1, 1, 0.15f, Interpolation.fastSlow)
                    ));
                }
            }

            if (gameObject == null || !gameObject.isContactDisabled() || gameObject.hasComponent(FatalComponent.class)) {
                boolean isFatal = gameObject != null && gameObject.hasComponent(FatalComponent.class);
                emitCollisionParticles(contact, isObjectA, isFatal);
                level.getCameraHandler().shakeCamera(0.6f, 0.1f);
                if (gameObject != null && gameObject.hasComponent(FatalComponent.class) && gameObject.isVisible()) {
                    setDone();
                }
            }
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        super.onEndContact(contact, gameObject, isObjectA);

        Fixture ownFixture = isObjectA ? contact.getFixtureA() : contact.getFixtureB();
        if (isSideSensor(ownFixture)) {
            handleSensorCollision(contact, gameObject, isObjectA, false);
        }
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
        super.onPreSolve(contact, oldManifold, gameObject, isObjectA);
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
        super.onPostSolve(contact, impulse, gameObject, isObjectA);
    }

    @Override
    public void onAbilityActivated() {
        isAbilityActivated = true;
        emitter.stopImmediately();

        EventManager.getInstance().postNotify(new TimeScaleEvent(
                new Tween(0.3f, Time.getInstance().getMaxTimeScale(), 1500, Interpolation.circle)));
    }

    @Override
    public void onAbilityDeactivated() {
        isAbilityActivated = false;
        level.addActor(emitter);
        emitter.emit();
    }

    @Override
    public void onAutoDash(Vector2 position, final Vector2 velocity) {
        autoDashElapsed = 0;

        isAutoDashing = true;
        physicsBody.setLinearVelocity(0, 0);
        addAction(Actions.sequence(
            Actions.moveTo(position.x, position.y, 0.01f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    physicsBody.setLinearVelocity(velocity);
                }
            })
        ));
    }

    @Override
    public void onTeleport(Vector2 oldPos, Vector2 newPos) {
        physicsBody.setTransform(newPos, getAngle());
    }

    @Override
    public void destroy() {
        super.destroy();

        EventManager.getInstance().mute(AbilityActivatedEvent.class, this);
        EventManager.getInstance().mute(AbilityDeactivatedEvent.class, this);
        EventManager.getInstance().mute(AutoDashEvent.class, this);
        EventManager.getInstance().mute(TeleportEvent.class, this);

        emitter.stopImmediately();
    }

    @Override
    protected Body createPhysicsBody(Level level, Shape2D shape, Properties properties, BodyType bodyType) {
        // Make player's body slightly smaller than a tile
        float size = ConversionUtils.getPixelsPerTile() * 0.9f;

        properties.put("width", size);
        properties.put("height", size);
        return super.createPhysicsBody(level, shape, properties, BodyType.DynamicBody);
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    private void playCollisionSound() {
        float scale = TimeUtils.timeSinceMillis(lastCollideSoundTime) < FULL_VOLUME_COLLIDE_SOUND_COOLDOWN ? 0.6f : 1;
        AudioManager.playSound(AudioManager.getInstance().collideSound, scale);
        currCollisionSoundId = Math.max(1, (currCollisionSoundId + 1) % 4);
    }

    private AbilityType getAbilityType() {
        AbilityType abilityType = null;
        for (Component component : components) {
            if (component instanceof Ability) {
                abilityType = ((Ability)component).getType();
            }
        }

        return abilityType;
    }

    private boolean checkCameraPosition() {
        CameraHandler cameraHandler = level.getCameraHandler();
        if (cameraHandler.isRepositioning() || cameraHandler.isMovingToTarget()) {
            return false;
        }

        boolean hasCameraCaughtUp = false;
        Vector2 cameraVelocity = level.getCameraVelocity();
        if (cameraVelocity.x > 0) {
            hasCameraCaughtUp = getRight() + (width / 2.0f) < level.getCameraLeft();
        } else if (cameraVelocity.x < 0) {
            hasCameraCaughtUp = getLeft() - (width / 2.0f) > level.getCameraRight();
        } else if (cameraVelocity.y > 0) {
            hasCameraCaughtUp = getTop() + (height / 2.0f) < level.getCameraBottom();
        } else if (cameraVelocity.y < 0) {
            hasCameraCaughtUp = getBottom() - (height / 2.0f) > level.getCameraTop();
        }

        if (hasCameraCaughtUp && level.isRunning()) {
            Vector2 pos = new Vector2(getCenterX(), getCenterY());
            ParticleEffectEmitter emitter = ParticleEffects.getEmitter(ParticleEffects.CAMERA_CAUGHT_UP, pos);
            level.addActor(emitter);
            emitter.emit();

            AudioManager.playSound(AudioManager.getInstance().deathSound);
            EventManager.getInstance().notify(
                    new LevelStateChangeEvent(level.getState(), LevelState.DONE_FAILURE));
            setDone();
            return true;
        }

        return false;
    }

    private boolean isSideSensor(Fixture fixture) {
        return fixture == topSensor || fixture == bottomSensor ||
                fixture == leftSensor || fixture == rightSensor;
    }

    private void handleSensorCollision(Contact contact, GameObject gameObject, boolean isObjectA, boolean isActivated) {
        Fixture ownFixture = isObjectA ? contact.getFixtureA() : contact.getFixtureB();
        Fixture otherFixture = isObjectA ? contact.getFixtureB() : contact.getFixtureA();
        if ((gameObject == null || !gameObject.isContactDisabled()) &&
                otherFixture.getFilterData().categoryBits != CollisionFilters.LINE_CATEGORY) {
            if (ownFixture == topSensor) {
                isTopSensorActivated = isActivated;
            } else if (ownFixture == bottomSensor) {
                isBottomSensorActivated = isActivated;
            } else if (ownFixture == leftSensor) {
                isLeftSensorActivated = isActivated;
            } else if (ownFixture == rightSensor) {
                isRightSensorActivated = isActivated;
            }
        }
    }

    private boolean checkSideSensors() {
        if ((isLeftSensorActivated && isRightSensorActivated) ||
                (isBottomSensorActivated && isTopSensorActivated)) {
            Vector2 pos = new Vector2(getCenterX(), getCenterY());
            ParticleEffectEmitter emitter = ParticleEffects.getEmitter(ParticleEffects.FATAL_COLLISION, pos);
            level.addActor(emitter);
            emitter.emit();

            AudioManager.playSound(AudioManager.getInstance().deathSound);
            EventManager.getInstance().notify(
                    new LevelStateChangeEvent(level.getState(), LevelState.DONE_FAILURE));
            setDone();
            return true;
        }

        return false;
    }

    private void createSensors() {
        PolygonShape shape = new PolygonShape();
        Vector2 top = physicsBody.getLocalPoint(new Vector2(getCenterX(), getTop()));
        shape.setAsBox(width * 0.15f, 0.01f, top, 0);
        topSensor = physicsBody.createFixture(shape, 0);
        topSensor.setSensor(true);

        Vector2 bottom = physicsBody.getLocalPoint(new Vector2(getCenterX(), getBottom()));
        shape.setAsBox(width * 0.15f, 0.01f, bottom, 0);
        bottomSensor = physicsBody.createFixture(shape, 0);
        bottomSensor.setSensor(true);

        Vector2 left = physicsBody.getLocalPoint(new Vector2(getLeft(), getCenterY()));
        shape.setAsBox(0.01f, height * 0.15f, left, 0);
        leftSensor = physicsBody.createFixture(shape, 0);
        leftSensor.setSensor(true);

        Vector2 right = physicsBody.getLocalPoint(new Vector2(getRight(), getCenterY()));
        shape.setAsBox(0.01f, height * 0.15f, right, 0);
        rightSensor = physicsBody.createFixture(shape, 0);
        rightSensor.setSensor(true);

        Vector2 center = physicsBody.getLocalPoint(new Vector2(getCenterX(), getCenterY()));
        shape.setAsBox(width * 0.35f, height * 0.35f, center, 0);
        centerSensor = physicsBody.createFixture(shape, 0);
        centerSensor.setSensor(true);

        shape.dispose();
    }

    private void emitCollisionParticles(Contact contact, boolean isObjectA, boolean isFatal) {
        Vector2 position = contact.getWorldManifold().getPoints()[0];

        ParticleEffect effect = isFatal ? ParticleEffects.FATAL_COLLISION : ParticleEffects.NORMAL_COLLISION;
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(effect, position);
        level.addActor(emitter);
        emitter.emit();
    }

    public boolean isDashing() {
        AbilityType abilityType = getAbilityType();
        return isAutoDashing || (isAbilityActivated && abilityType != null && abilityType.equals(AbilityType.DASH));
    }

    private void initEmitter() {
        Vector2 startSize = new Vector2(getWidth(), getHeight());
        emitter.getEffect().setStartSizeRange(startSize, startSize);
        Vector2 endSize = new Vector2(startSize).scl(1.4f);
        emitter.getEffect().setEndSizeRange(endSize, endSize);
        level.addActor(emitter);
        emitter.emit();
    }
}
