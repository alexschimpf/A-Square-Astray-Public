package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.level.CameraHandler;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.GravitySwitchEvent;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.utils.Tween;

public class GravitySwitchComponent extends Component implements ICollide {

    private Vector2 gravity;
    private final ParticleEffectEmitter emitter;

    public GravitySwitchComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        emitter = ParticleEffects.getEmitter(ParticleEffects.GRAVITY_SWITCH, new Vector2(), 0);
    }

    @Override
    public void init() {
        gravity = properties.getVector2("gravity");
        parent.setDisableContacts(true);

        emitParticles();
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (Player.isPlayerCenter(contact, gameObject, isObjectA)) {
            AudioManager.playSound(AudioManager.getInstance().gravitySwitchSound);

            Vector2 oldGravity = level.getPhysicsWorld().getGravity();
            EventManager.getInstance().notify(new TimeScaleEvent(
                    new Tween(0.3f, Time.getInstance().getMaxTimeScale(), CameraHandler.REPOSITION_DURATION,
                            Interpolation.slowFast)
            ));
            EventManager.getInstance().notify(new GravitySwitchEvent(oldGravity, gravity));
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
    }

    private void emitParticles() {
        setEmitterPositionOffset();
        Vector2 position = getParticleEffectPosition();
        emitter.setPosition(position.x, position.y);
        emitter.getEffect().setVelocityRange(
                new Vector2(gravity).scl(0.3f), new Vector2(gravity).scl(0.6f));
        int numParticles = getNumParticles();
        emitter.getEffect().setNumParticlesRange(numParticles, numParticles);
        level.addActor(emitter);
        emitter.emit();
    }

    private Vector2 getParticleEffectPosition() {
        // TODO: This only handle purely vertical or horizontal gravity switches
        Vector2 position = new Vector2();
        if (gravity.x > 0) {
            position.set(parent.getLeft(), parent.getCenterY());
        } else if (gravity.x < 0) {
            position.set(parent.getRight(), parent.getCenterY());
        } else if (gravity.y > 0) {
            position.set(parent.getCenterX(), parent.getBottom());
        } else if (gravity.y < 0) {
            position.set(parent.getCenterX(), parent.getTop());
        }

        return position;
    }

    private void setEmitterPositionOffset() {
        // TODO: This only handle purely vertical or horizontal gravity switches
        if (Math.abs(gravity.x) > 0) {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(0, -parent.getHeight() * 0.48f),
                    new Vector2(0, parent.getHeight() * 0.48f));
        } else {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(-getParent().getWidth() * 0.48f, 0),
                    new Vector2(getParent().getWidth() * 0.48f, 0));
        }
    }

    private int getNumParticles() {
        int numParticles = 1;
        if (Math.abs(gravity.x) > 0) {
           numParticles = (int)(parent.getHeight() / (ConversionUtils.getMetersPerTile() * 5));
        } else {
            numParticles = (int)(parent.getWidth() / (ConversionUtils.getMetersPerTile() * 5));
        }

        return Math.max(numParticles, 1);
    }
}
