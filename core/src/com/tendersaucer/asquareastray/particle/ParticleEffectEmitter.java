package com.tendersaucer.asquareastray.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.ICanvasItem;
import com.tendersaucer.asquareastray.utils.RandomUtils;

import java.util.Iterator;

public class ParticleEffectEmitter extends Actor implements ICanvasItem {

    private int layer;
    private boolean isStopped;
    private float elapsed;
    private ParticleEffect effect;
    private Integer emitFrequency;
    private Array<Particle> particles;

    public ParticleEffectEmitter(ParticleEffect effect, Vector2 position, Integer emitFrequency) {
        isStopped = true;
        this.effect = effect;
        this.emitFrequency = emitFrequency;
        particles = new Array<>();
        setPosition(position.x, position.y);
    }

    @Override
    public void act(float delta) {
        Iterator<Particle> iter = particles.iterator();
        while (iter.hasNext()) {
            Particle particle = iter.next();
            if (particle.act()) {
                iter.remove();
            }
        }

        float dt = effect.ignoreTimeScale() ? Gdx.graphics.getDeltaTime() :  Time.getInstance().getDeltaTime();
        elapsed += dt * 1000;
        if (!isStopped && emitFrequency != null &&
                elapsed > emitFrequency) {
            createParticles();
        }
    }

    @Override
    public void draw(Batch batch, float delta) {
        if (emitFrequency == null && particles.size == 0) {
            remove();
            return;
        }

        for (Particle particle : particles) {
            particle.draw(batch);
        }
    }

    @Override
    public boolean render(SpriteBatch batch) {
        this.act(Gdx.graphics.getDeltaTime());

        if (emitFrequency == null && particles.size == 0) {
            return true;
        }

        for (Particle particle : particles) {
            particle.draw(batch);
        }

        return false;
    }

    @Override
    public int getLayer() {
        return layer;
    }

    @Override
    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void start() {
        isStopped = false;
    }

    public void emit() {
        start();
        createParticles();
    }

    public void stop() {
        isStopped = true;
    }

    public void stopImmediately() {
        stop();
        particles.clear();
    }

    public void setEmitFrequency(Integer emitFrequency) {
        this.emitFrequency = emitFrequency;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    private void createParticles() {
        elapsed = 0;
        int numParticles = (int)RandomUtils.pickFromRange(effect.getNumParticlesRange());
        for (int i = 0; i < numParticles; i++) {
            Particle particle = new Particle(this);
            particles.add(particle);
        }
    }
}
