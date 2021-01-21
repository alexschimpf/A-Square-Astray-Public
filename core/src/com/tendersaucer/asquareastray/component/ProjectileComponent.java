package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;

public class ProjectileComponent extends Component {

    private final ParticleEffectEmitter emitter;

    public ProjectileComponent(Level level, GameObject parent) {
        super(level, parent);

        emitter = ParticleEffects.getEmitter(ParticleEffects.PROJECTILE, new Vector2(), 0);
        parent.setLayer(0);
    }

    @Override
    public void init() {
        emitParticles();
    }

    @Override
    public boolean update() {
        emitter.setPosition(parent.getCenterX(), parent.getCenterY());
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();

        emitter.stopImmediately();
    }

    private void emitParticles() {
        Vector2 velocity = parent.getPhysicsBody().getLinearVelocity();
        Vector2 v1 = new Vector2(velocity);
        v1.rotate(170).nor().scl(20);
        Vector2 v2 = new Vector2(velocity);
        v2.rotate(190).nor().scl(20);

        emitter.getEffect().setVelocityRange(
                new Vector2(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y)),
                new Vector2(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y)));

        float startWidth = parent.getWidth();
        float startHeight = parent.getHeight();
        emitter.getEffect().setStartSizeRange(
                new Vector2(startWidth, startHeight), new Vector2(startWidth, startHeight));

        float endWidth = parent.getWidth() * 0.1f;
        float endHeight = parent.getHeight() * 0.1f;
        emitter.getEffect().setEndSizeRange(new Vector2(endWidth, endHeight), new Vector2(endWidth, endHeight));

        level.getCanvas().addToLayer(emitter, 1);
        emitter.emit();
    }
}
