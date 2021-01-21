package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.utils.ConversionUtils;

public class DestructibleComponent extends Component implements ICollide {

    private float requiredMinSpeed;

    public DestructibleComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        if (properties != null) {
            requiredMinSpeed = properties.getFloat("required_min_speed", 0f);
        }
    }

    public DestructibleComponent(Level level, GameObject parent) {
        this(level, parent, null);
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (shouldDestroy(gameObject)) {
            emitParticles();
            parent.setDone();
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
        if (shouldDestroy(gameObject)) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
    }

    private void emitParticles() {
        Vector2 position = new Vector2(parent.getCenterX(), parent.getCenterY());
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(ParticleEffects.DESTRUCTIBLE_COLLISION, position);
        float scale = parent.getWidth() / ConversionUtils.getMetersPerTile() + 0.5f;
        emitter.getEffect().setEndSizeRange(
                new Vector2(ConversionUtils.getMetersPerTile() * scale, ConversionUtils.getMetersPerTile() * scale),
                new Vector2(ConversionUtils.getMetersPerTile() * scale, ConversionUtils.getMetersPerTile() * scale));
        if (!properties.isPropertyEmpty("emitter_start_color")) {
            Color color = properties.getColor("emitter_start_color");
            emitter.getEffect().setStartColorRange(new Color(color), new Color(color));
        }
        if (!properties.isPropertyEmpty("emitter_end_color")) {
            Color color = properties.getColor("emitter_end_color");
            emitter.getEffect().setEndColorRange(new Color(color), new Color(color));
        }

        level.addActor(emitter);
        emitter.emit();

    }

    private boolean shouldDestroy(GameObject gameObject) {
        if (gameObject == null) {
            // TODO: This is a hack.
            return requiredMinSpeed == 0;
        }

        boolean isObjectSolid = !gameObject.isContactDisabled() || gameObject.hasComponent(FatalComponent.class);
        float collisionSpeed = Math.max(
                parent.getPhysicsBody().getLinearVelocity().len(),
                gameObject.getPhysicsBody().getLinearVelocity().len());
        return collisionSpeed >= requiredMinSpeed && isObjectSolid;
    }
}
