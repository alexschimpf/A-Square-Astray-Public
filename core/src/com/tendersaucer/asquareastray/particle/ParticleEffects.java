package com.tendersaucer.asquareastray.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.utils.ConversionUtils;

public final class ParticleEffects {

    public static ParticleEffectEmitter getEmitter(ParticleEffect effect, Vector2 position, Integer emitFrequency) {
        ParticleEffect effectClone = effect.clone();
        return new ParticleEffectEmitter(effectClone, position, emitFrequency);
    }

    public static ParticleEffectEmitter getEmitter(ParticleEffect effect, Vector2 position) {
        ParticleEffect effectClone = effect.clone();
        return new ParticleEffectEmitter(effectClone, position, null);
    }

    public static final ParticleEffect MAIN_MENU_BACKGROUND = new ParticleEffect()
            .setSprites("square_outline")
            .setDurationRange(1000, 5000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(20, 90)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(
                    new Vector2(-Gdx.graphics.getWidth() * 0.7f, -Gdx.graphics.getHeight() * 0.7f),
                    new Vector2(Gdx.graphics.getWidth() * 0.7f, Gdx.graphics.getHeight() * 0.7f))
            .setStartSizeRange(
                    new Vector2(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getWidth() * 0.15f),
                    new Vector2(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getWidth() * 0.3f))
            .setEndSizeRange(
                    new Vector2(Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getWidth() * 0.08f),
                    new Vector2(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.1f))
            .setVelocityRange(new Vector2(-30, -30), new Vector2(30, 30))
            .setFadeInRatio(0.5f)
            .setFadeOutRatio(0.5f)
            .setGrayscale(false)
            .setIgnoreTimeScale(true);

    public static final ParticleEffect LEVEL_COMPLETE = MAIN_MENU_BACKGROUND.clone();

    public static final ParticleEffect GAME_BACKGROUND = new ParticleEffect()
            .setSprites("default")
            .setDurationRange(10000, 15000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 10)
            .setNumParticlesRange(30, 30)
            .setPositionOffsetRange(
                    new Vector2(-ConversionUtils.getMetersPerScreenWidth() * 1.5f,
                            -ConversionUtils.getMetersPerScreenHeight() * 1.5f),
                    new Vector2(ConversionUtils.getMetersPerScreenWidth() * 1.5f,
                            ConversionUtils.getMetersPerScreenHeight() * 1.5f))
            .setStartSizeRange(
                    new Vector2(ConversionUtils.getMetersPerScreenWidth() * 0.5f,
                            ConversionUtils.getMetersPerScreenWidth() * 0.5f),
                    new Vector2(ConversionUtils.getMetersPerScreenWidth() * 1f,
                            ConversionUtils.getMetersPerScreenWidth() * 1f))
            .setVelocityRange(new Vector2(-2, -2), new Vector2(2, 2))
            .setStartColorRange(new Color(), new Color())
            .setGrayscale(true)
            .setKeepSquare(true)
            .setIgnoreTimeScale(true);

    public static final ParticleEffect GAME_BACKGROUND_2 = new ParticleEffect()
            .setSprites("background_star")
            .setDurationRange(10000, 15000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 0.2f)
            .setStartSizeRange(
                    new Vector2(ConversionUtils.getMetersPerScreenWidth() * 0.005f,
                            ConversionUtils.getMetersPerScreenWidth() * 0.005f),
                    new Vector2(ConversionUtils.getMetersPerScreenWidth() * 0.01f,
                            ConversionUtils.getMetersPerScreenWidth() * 0.01f))
            .setVelocityRange(new Vector2(-2f, -2f), new Vector2(2f, 2f))
            .setStartColorRange(
                    new Color(0f, 0f, 0f, 0.05f),
                    new Color(1, 1, 1, 0.15f))
            .setGrayscale(true)
            .setKeepSquare(true)
            .setIgnoreTimeScale(true);

    public static final ParticleEffect DOOR_UNLOCK_COLLISION = new ParticleEffect()
            .setSprites("circle")
            .setDurationRange(300, 1500)
            .setAccelerationRange(0, 2f)
            .setAngularVelocityRange(0, 180)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(50, 50)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.05f, ConversionUtils.getMetersPerTile() * 0.05f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.2f, ConversionUtils.getMetersPerTile() * 0.2f))
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.5f, ConversionUtils.getMetersPerTile() * 0.5f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 1f, ConversionUtils.getMetersPerTile() * 1f))
            .setVelocityRange(new Vector2(-3, -3), new Vector2(3, 3))
            .setStartColorRange(new Color(0.8f, 0.8f, 0.8f, 0.1f), new Color(1, 1, 1, 0.1f))
            .setEndColorRange(new Color(1, 1, 1, 0), new Color(1, 1, 1, 0))
            .setKeepSquare(true);

    public static final ParticleEffect PROJECTILE = new ParticleEffect()
            .setSprites("circle")
            .setDurationRange(200, 400)
            .setAccelerationRange(0, 1f)
            .setAngularVelocityRange(0, 180)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setVelocityRange(new Vector2(-1, -1), new Vector2(1, 1))
            .setStartColorRange(new Color(Color.RED), new Color(Color.RED))
            .setEndColorRange(new Color(1, 0, 0, 0), new Color(1, 0, 0, 0))
            .setKeepSquare(false)
            .setGrayscale(false);

    public static final ParticleEffect GRAVITY_SWITCH = new ParticleEffect()
            .setSprites("circle")
            .setDurationRange(300, 1500)
            .setAccelerationRange(0, 5f)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.1f, ConversionUtils.getMetersPerTile() * 0.1f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.3f, ConversionUtils.getMetersPerTile() * 0.3f))
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.5f, 0.5f, 0.5f, 0.1f), new Color(0.7f, 0.7f, 0.7f, 1))
            .setEndColorRange(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))
            .setKeepSquare(true)
            .setFadeInRatio(0.3f)
            .setFadeOutRatio(0.7f)
            .setGrayscale(true);

    public static final ParticleEffect FINISH_LINE = new ParticleEffect()
            .setSprites("circle")
            .setNumParticlesRange(1, 1)
            .setDurationRange(500, 2500)
            .setAngularVelocityRange(0, 180f)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.4f, ConversionUtils.getMetersPerTile() * 0.4f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.8f, ConversionUtils.getMetersPerTile() * 0.8f))
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.05f, ConversionUtils.getMetersPerTile() * 0.05f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.15f, ConversionUtils.getMetersPerTile() * 0.15f))
            .setStartColorRange(new Color(0, 0, 0, 1), new Color(1, 1, 1, 1))
            .setEndColorRange(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0))
            .setKeepSquare(false)
            .setFadeInRatio(0.3f)
            .setFadeOutRatio(0.7f)
            .setKeepSquare(true);

    public static final ParticleEffect LINE_PLACEMENT = new ParticleEffect()
            .setSprites("circle_outline")
            .setDurationRange(500, 500)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 0)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 1.3f, ConversionUtils.getMetersPerTile() * 1.3f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 1.3f, ConversionUtils.getMetersPerTile() * 1.3f))
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.5f, 0.5f, 0.5f, 1), new Color(0.5f, 0.5f, 0.5f, 1))
            .setEndColorRange(new Color(0.5f, 0.5f, 0.5f, 0), new Color(0.5f, 0.5f, 0.5f, 0))
            .setKeepSquare(true)
            .setKeepSizeRatio(true)
            .setGrayscale(true);

    public static final ParticleEffect DOOR_UNLOCK = new ParticleEffect()
            .setSprites("square_outline")
            .setDurationRange(500, 500)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 0)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 1f, ConversionUtils.getMetersPerTile() * 1f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 1f, ConversionUtils.getMetersPerTile() * 1f))
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.5f, 0.5f, 0.5f, 1), new Color(0.5f, 0.5f, 0.5f, 1))
            .setEndColorRange(new Color(0.5f, 0.5f, 0.5f, 0), new Color(0.5f, 0.5f, 0.5f, 0))
            .setKeepSquare(true)
            .setKeepSizeRatio(true)
            .setGrayscale(true);

    public static final ParticleEffect DOOR_UNLOCK_COLLISION_2 = new ParticleEffect()
            .setSprites("circle_outline")
            .setDurationRange(200, 200)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 0)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 15f, ConversionUtils.getMetersPerTile() * 15f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 15f, ConversionUtils.getMetersPerTile() * 15f))
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.5f, 0.5f, 0.5f, 1), new Color(0.5f, 0.5f, 0.5f, 1))
            .setEndColorRange(new Color(0.5f, 0.5f, 0.5f, 0), new Color(0.5f, 0.5f, 0.5f, 0))
            .setKeepSquare(true)
            .setKeepSizeRatio(true)
            .setGrayscale(true);

    public static final ParticleEffect AUTO_DASH = DOOR_UNLOCK_COLLISION_2.clone()
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 3f, ConversionUtils.getMetersPerTile() * 3f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 3f, ConversionUtils.getMetersPerTile() * 3f));

    public static final ParticleEffect NORMAL_COLLISION = DOOR_UNLOCK_COLLISION_2.clone()
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 2f, ConversionUtils.getMetersPerTile() * 2f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 2f, ConversionUtils.getMetersPerTile() * 2f));

    public static final ParticleEffect DESTRUCTIBLE_COLLISION = DOOR_UNLOCK_COLLISION_2.clone()
            .setSprites("circle")
            .setNumParticlesRange(10, 10)
            .setDurationRange(200, 600)
            .setPositionOffsetRange(
                    new Vector2(-ConversionUtils.getMetersPerTile() * 0.5f, -ConversionUtils.getMetersPerTile() * 0.5f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 0.5f, ConversionUtils.getMetersPerTile() * 0.5f))
            .setEndSizeRange(
                    new Vector2(ConversionUtils.getMetersPerTile() * 2f, ConversionUtils.getMetersPerTile() * 2f),
                    new Vector2(ConversionUtils.getMetersPerTile() * 2f, ConversionUtils.getMetersPerTile() * 2f))
            .setGrayscale(false);

    public static final ParticleEffect FATAL_COLLISION = DOOR_UNLOCK_COLLISION_2.clone();

    public static final ParticleEffect CAMERA_CAUGHT_UP = DOOR_UNLOCK_COLLISION_2.clone();

    public static final ParticleEffect TUTORIAL_INDICATOR = DOOR_UNLOCK_COLLISION_2.clone()
            .setDurationRange(500, 500)
            .setEndSizeRange(
                    new Vector2(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()),
                    new Vector2(Gdx.graphics.getHeight(), Gdx.graphics.getHeight()))
            .setIgnoreTimeScale(true);

    public static final ParticleEffect PORTAL = new ParticleEffect()
            .setSprites("circle_outline")
            .setDurationRange(1000, 1000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(360, 360)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setEndSizeRange(new Vector2(), new Vector2())
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.5f, 0.5f, 0.5f, 1), new Color(0.5f, 0.5f, 0.5f, 1))
            .setEndColorRange(new Color(0.5f, 0.5f, 0.5f, 0), new Color(0.5f, 0.5f, 0.5f, 0))
            .setKeepSquare(true)
            .setKeepSizeRatio(true)
            .setGrayscale(true);

    public static final ParticleEffect PLAYER = new ParticleEffect()
            .setSprites("player")
            .setDurationRange(1000, 1000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(0, 0)
            .setAngularAccelerationRange(0, 0)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(new Vector2(), new Vector2())
            .setStartSizeRange(new Vector2(), new Vector2())
            .setEndSizeRange(new Vector2(), new Vector2())
            .setVelocityRange(new Vector2(), new Vector2())
            .setStartColorRange(new Color(0.7f, 0.7f, 0.7f, 1), new Color(0.7f, 0.7f, 0.7f, 1))
            .setEndColorRange(new Color(0.3f, 0.3f, 0.3f, 0), new Color(0.3f, 0.3f, 0.3f, 0))
            .setKeepSquare(false)
            .setKeepSizeRatio(false)
            .setGrayscale(true);

    public static final ParticleEffect ALL_LEVELS_COMPLETE = new ParticleEffect()
            .setSprites("default_rounded")
            .setDurationRange(1000, 5000)
            .setAccelerationRange(0, 0)
            .setAngularVelocityRange(20, 90)
            .setNumParticlesRange(1, 1)
            .setPositionOffsetRange(
                    new Vector2(0, 0),
                    new Vector2(Gdx.graphics.getWidth(), 0))
            .setStartSizeRange(
                    new Vector2(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getWidth() * 0.05f),
                    new Vector2(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getWidth() * 0.15f))
            .setEndSizeRange(
                    new Vector2(Gdx.graphics.getWidth() * 0.01f, Gdx.graphics.getWidth() * 0.01f),
                    new Vector2(Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getWidth() * 0.05f))
            .setVelocityRange(new Vector2(-50, -300), new Vector2(50, -100))
            .setStartColorRange(new Color(0, 0, 0, 0.5f), new Color(1, 1, 1, 0.5f))
            .setFadeInRatio(0.5f)
            .setFadeOutRatio(0.5f)
            .setGrayscale(false)
            .setKeepSquare(true)
            .setIgnoreTimeScale(true);
}
