package com.tendersaucer.asquareastray.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.utils.RandomUtils;

public class Particle {

    private ParticleEffectEmitter emitter;
    private float elapsed;
    private float duration;
    private float startWidth;
    private float endWidth;
    private float startHeight;
    private float endHeight;
    private float positionOffsetX;
    private float positionOffsetY;
    private float velocityX;
    private float velocityY;
    private float acceleration;
    private float angularVelocity;
    private float angularAcceleration;
    private float fadeInToAlpha;
    private Float fadeInDuration;
    private Float fadeOutDuration;
    private Color startColor;
    private Color endColor;
    private Sprite sprite;

    public Particle(ParticleEffectEmitter emitter) {
        this.emitter = emitter;
        init(emitter.getEffect());
    }

    public boolean act() {
        float dt = emitter.getEffect().ignoreTimeScale() ?
                Gdx.graphics.getDeltaTime() : Time.getInstance().getDeltaTime();
        elapsed += dt * 1000;
        float progress = elapsed / duration;
        if (progress >= 1) {
            return true;
        }

        return false;
    }

    public void draw(Batch batch) {
        float elapsedSeconds = elapsed / 1000.f;
        float progress = MathUtils.clamp(elapsed / duration, 0, 1);

        float width = MathUtils.lerp(startWidth, endWidth, progress);
        float height = MathUtils.lerp(startHeight, endHeight, progress);
        sprite.setSize(width, height);

        float ax = acceleration *
                (Math.abs(velocityX) / (Math.max(Math.abs(velocityX + velocityY), 1)));
        float dx = positionOffsetX + (velocityX * elapsedSeconds) +
                (0.5f * ax * (float)Math.pow(elapsedSeconds, 2));
        float ay = acceleration * (Math.abs(velocityY) /
                (Math.max(Math.abs(velocityX + velocityY), 1)));
        float dy = positionOffsetY + (velocityY * elapsedSeconds) +
                (0.5f * ay * (float)Math.pow(elapsedSeconds, 2));
        float x = emitter.getX() + dx;
        float y = emitter.getY() + dy;
        sprite.setCenter(x, y);
        sprite.setOriginCenter();

        float da = (angularVelocity * elapsedSeconds) -
                (0.5f * angularAcceleration * (float)Math.pow(elapsedSeconds, 2));
        float angle = emitter.getRotation() + da;
        sprite.setRotation(angle);

        float r = MathUtils.lerp(startColor.r, endColor.r, progress);
        float g = MathUtils.lerp(startColor.g, endColor.g, progress);
        float b = MathUtils.lerp(startColor.b, endColor.b, progress);

        float a;
        if (fadeInDuration != null && elapsed < fadeInDuration) {
            a = Math.min(elapsed / fadeInDuration, 1) * fadeInToAlpha;
        } else if (fadeOutDuration != null) {
            if (duration - elapsed < fadeOutDuration) {
                // TODO: Maybe this should max out at endColor.a?
                a = Math.max((duration - elapsed) / fadeOutDuration, 0) * fadeInToAlpha;
            } else {
                a = sprite.getColor().a;
            }
        } else {
            a = MathUtils.lerp(startColor.a, endColor.a, progress);
        }
        sprite.setColor(r, g, b, a);

        sprite.draw(batch);
    }

    private void init(ParticleEffect effect) {
        elapsed = 0;
        duration = RandomUtils.pickFromRange(effect.getDurationRange());
        positionOffsetX = RandomUtils.pickFromRange(
                effect.getPositionOffsetRange().x.x, effect.getPositionOffsetRange().y.x);
        positionOffsetY = RandomUtils.pickFromRange(
                effect.getPositionOffsetRange().x.y, effect.getPositionOffsetRange().y.y);
        velocityX = RandomUtils.pickFromRange(
                effect.getVelocityRange().x.x, effect.getVelocityRange().y.x);
        velocityY = RandomUtils.pickFromRange(
                effect.getVelocityRange().x.y, effect.getVelocityRange().y.y);
        acceleration = RandomUtils.pickFromRange(effect.getAccelerationRange());
        angularVelocity = RandomUtils.pickFromRange(effect.getAngularVelocityRange());
        angularAcceleration = RandomUtils.pickFromRange(effect.getAngularAccelerationRange());

        if (effect.getFadeInRatio() != null) {
            fadeInDuration = duration * effect.getFadeInRatio();
        }
        if (effect.getFadeOutRatio() != null) {
            fadeOutDuration = duration * effect.getFadeOutRatio();
        }

        initSize(effect);
        initColor(effect);

        sprite = AssetManager.getInstance().getSprite(RandomUtils.pickFrom(effect.getSpriteKeys()));
    }

    private void initSize(ParticleEffect effect) {
        startWidth = RandomUtils.pickFromRange(
                effect.getStartSizeRange().x.x, effect.getStartSizeRange().y.x);
        startHeight = RandomUtils.pickFromRange(
                effect.getStartSizeRange().x.y, effect.getStartSizeRange().y.y);

        // To avoid divide-by-zero errors
        startWidth = Math.max(startWidth, 0.0001f);
        startHeight = Math.max(startHeight, 0.0001f);

        if (effect.getWidthScale() != null && effect.getHeightScale() != null) {
            endWidth = startWidth * effect.getWidthScale();
            endHeight = startHeight * effect.getHeightScale();
        } else {
            endWidth = RandomUtils.pickFromRange(
                    effect.getEndSizeRange().x.x, effect.getEndSizeRange().y.x);
            endHeight = (endWidth / startWidth) * startHeight;
            if (!effect.keepSizeRatio()) {
                endHeight = RandomUtils.pickFromRange(
                        effect.getEndSizeRange().x.y, effect.getEndSizeRange().y.y);
            }
        }

        if (effect.keepSquare()) {
            this.startHeight = startWidth;
            this.endHeight = endWidth;
        }
    }

    private void initColor(ParticleEffect effect) {
        float r = RandomUtils.pickFromRange(effect.getStartColorRange().x.r, effect.getStartColorRange().y.r);
        float g = r, b = r;
        if (!effect.isGrayscale()) {
            g = RandomUtils.pickFromRange(effect.getStartColorRange().x.g, effect.getStartColorRange().y.g);
            b = RandomUtils.pickFromRange(effect.getStartColorRange().x.b, effect.getStartColorRange().y.b);
        }
        float a = RandomUtils.pickFromRange(effect.getStartColorRange().x.a, effect.getStartColorRange().y.a);
        if (fadeInDuration != null) {
            fadeInToAlpha = a;
            a = 0;
        }

        startColor = new Color(r, g, b, a);

        r = RandomUtils.pickFromRange(effect.getEndColorRange().x.r, effect.getEndColorRange().y.r);
        g = r;
        b = r;
        if (!effect.isGrayscale()) {
            g = RandomUtils.pickFromRange(effect.getEndColorRange().x.g, effect.getEndColorRange().y.g);
            b = RandomUtils.pickFromRange(effect.getEndColorRange().x.b, effect.getEndColorRange().y.b);
        }
        a = RandomUtils.pickFromRange(effect.getEndColorRange().x.a, effect.getEndColorRange().y.a);
        endColor = new Color(r, g, b, a);
    }
}
