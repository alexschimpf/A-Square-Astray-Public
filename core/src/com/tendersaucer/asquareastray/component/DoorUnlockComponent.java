package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.component.ColorTweenComponent;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.UnlockEvent;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.utils.RandomUtils;

public class DoorUnlockComponent extends Component implements ICollide {

    private final String doorName;
    private final ParticleEffectEmitter emitter;

    public DoorUnlockComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        doorName = properties.getString("door_name");
        parent.setLayer(0);

        parent.getPhysicsBody().setAngularVelocity(RandomUtils.pickFromRange(1.8f, 3.2f));

        emitter = ParticleEffects.getEmitter(ParticleEffects.DOOR_UNLOCK,
                new Vector2(parent.getCenterX(), parent.getCenterY()), 500);
    }

    @Override
    public void init() {
        parent.setDisableContacts(true);

        Properties properties = new Properties();
        properties.put("colors", "1,1,1,1 0.35,0.35,0.35,1");
        properties.put("durations", "750,750");
        parent.addComponent(new ColorTweenComponent(level, parent, properties));

        initParticleEmitter();
    }

    @Override
    public boolean update() {
        emitter.setPosition(parent.getCenterX(), parent.getCenterY());
        emitter.setRotation(MathUtils.radiansToDegrees * parent.getAngle());

        return super.update();
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (Player.isPlayer(contact, gameObject, isObjectA)) {
            emitCollisionParticles();
            parent.setDone();
            emitter.stopImmediately();
            EventManager.getInstance().postNotify(new UnlockEvent(doorName));
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

    private void initParticleEmitter() {
        Vector2 startSize = new Vector2(parent.getWidth() * 1f, parent.getHeight() * 1f);
        emitter.getEffect().setStartSizeRange(startSize, startSize);
        Vector2 endSize = new Vector2(parent.getWidth() * 1.75f, parent.getHeight() * 1.75f);
        emitter.getEffect().setEndSizeRange(endSize, endSize);
        level.addActor(emitter);
        emitter.emit();
    }

    private void emitCollisionParticles() {
        Vector2 pos = new Vector2(parent.getCenterX(), parent.getCenterY());
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(ParticleEffects.DOOR_UNLOCK_COLLISION, pos);
        emitter.getEffect().setStartColorRange(new Color(Color.BLACK), new Color(Color.WHITE));
        level.addActor(emitter);
        emitter.emit();

        ParticleEffectEmitter emitter2 = ParticleEffects.getEmitter(
                ParticleEffects.DOOR_UNLOCK_COLLISION_2, new Vector2(pos));
        level.addActor(emitter2);
        emitter2.emit();
    }
}
