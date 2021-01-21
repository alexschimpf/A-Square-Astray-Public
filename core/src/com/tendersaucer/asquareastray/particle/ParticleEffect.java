package com.tendersaucer.asquareastray.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.utils.Tuple;

public class ParticleEffect {

    // If both of these are set, `keepSizeRatio` and `endSizeRange` are ignored
    private Float widthScale;
    private Float heightScale;

    // If either of these are set, the end color alpha component is ignored
    private Float fadeInRatio;
    private Float fadeOutRatio;

    // If this is set, the end size height component and `heightScale` are ignored
    private boolean keepSquare;

    private boolean ignoreTimeScale;
    private boolean isGrayscale;
    private boolean keepSizeRatio;
    private final Array<String> spriteKeys;
    private final Vector2 durationRange;
    private final Vector2 accelerationRange;
    private final Vector2 angularVelocityRange;
    private final Vector2 angularAccelerationRange;
    private final Vector2 numParticlesRange;
    private final Tuple<Vector2, Vector2> positionOffsetRange;
    private final Tuple<Vector2, Vector2> startSizeRange;
    private final Tuple<Vector2, Vector2> endSizeRange;
    private final Tuple<Vector2, Vector2> velocityRange;
    private final Tuple<Color, Color> startColorRange;
    private final Tuple<Color, Color> endColorRange;

    public ParticleEffect() {
        widthScale = null;
        heightScale = null;
        keepSquare = true;
        isGrayscale = false;
        keepSizeRatio = true;
        ignoreTimeScale = false;
        spriteKeys = new Array<>();
        durationRange = new Vector2();
        accelerationRange = new Vector2();
        angularVelocityRange = new Vector2();
        angularAccelerationRange = new Vector2();
        numParticlesRange = new Vector2();
        positionOffsetRange = new Tuple<>();
        startSizeRange = new Tuple<>();
        endSizeRange = new Tuple<>();
        velocityRange = new Tuple<>();
        startColorRange = new Tuple<>();
        endColorRange = new Tuple<>();
    }

    public ParticleEffect clone() {
        return new ParticleEffect()
            .setSprites(spriteKeys)
            .setDurationRange(durationRange.x, durationRange.y)
            .setAccelerationRange(accelerationRange.x, accelerationRange.y)
            .setAngularVelocityRange(angularVelocityRange.x, angularVelocityRange.y)
            .setAngularAccelerationRange(angularAccelerationRange.x, angularAccelerationRange.y)
            .setNumParticlesRange(numParticlesRange.x, numParticlesRange.y)
            .setPositionOffsetRange(positionOffsetRange.x, positionOffsetRange.y)
            .setStartSizeRange(startSizeRange.x, startSizeRange.y)
            .setEndSizeRange(endSizeRange.x, endSizeRange.y)
            .setVelocityRange(velocityRange.x, velocityRange.y)
            .setStartColorRange(startColorRange.x, startColorRange.y)
            .setEndColorRange(endColorRange.x, endColorRange.y)
            .setGrayscale(isGrayscale)
            .setKeepSizeRatio(keepSizeRatio)
            .setWidthScale(widthScale)
            .setHeightScale(heightScale)
            .setKeepSquare(keepSquare)
            .setFadeInRatio(fadeInRatio)
            .setFadeOutRatio(fadeOutRatio)
            .setIgnoreTimeScale(ignoreTimeScale);
    }

    public ParticleEffectEmitter createEmitter(Vector2 position, Integer emitFrequency) {
        ParticleEffectEmitter emitter = new ParticleEffectEmitter(this, position, emitFrequency);
        return emitter;
    }

    public ParticleEffect setSprites(String... spriteKeys) {
        this.spriteKeys.clear();
        this.spriteKeys.addAll(spriteKeys);
        return this;
    }

    public ParticleEffect setSprites(Array<String> spriteKeys) {
        this.spriteKeys.clear();
        this.spriteKeys.addAll(spriteKeys);
        return this;
    }

    public ParticleEffect setDurationRange(float min, float max) {
        durationRange.set(min, max);
        return this;
    }

    public boolean isGrayscale() {
        return isGrayscale;
    }

    public boolean keepSizeRatio() {
        return keepSizeRatio;
    }

    public Float getWidthScale() {
        return widthScale;
    }

    public ParticleEffect setWidthScale(Float widthScale) {
        this.widthScale = widthScale;
        return this;
    }

    public Float getHeightScale() {
        return heightScale;
    }

    public ParticleEffect setHeightScale(Float heightScale) {
        this.heightScale = heightScale;
        return this;
    }

    public Float getFadeInRatio() {
        return fadeInRatio;
    }

    public ParticleEffect setFadeInRatio(Float fadeInRatio) {
        this.fadeInRatio = fadeInRatio;
        return this;
    }

    public Float getFadeOutRatio() {
        return fadeOutRatio;
    }

    public ParticleEffect setFadeOutRatio(Float fadeOutRatio) {
        this.fadeOutRatio = fadeOutRatio;
        return this;
    }

    public ParticleEffect setAccelerationRange(float min, float max) {
        accelerationRange.set(min, max);
        return this;
    }

    public ParticleEffect setAngularVelocityRange(float min, float max) {
        angularVelocityRange.set(min, max);
        return this;
    }

    public ParticleEffect setAngularAccelerationRange(float min, float max) {
        angularAccelerationRange.set(min, max);
        return this;
    }

    public ParticleEffect setNumParticlesRange(float min, float max) {
        numParticlesRange.set(min, max);
        return this;
    }

    public ParticleEffect setPositionOffsetRange(Vector2 min, Vector2 max) {
        positionOffsetRange.set(min, max);
        return this;
    }

    public ParticleEffect setStartSizeRange(Vector2 min, Vector2 max) {
        startSizeRange.set(min, max);
        if (!endSizeRange.isSet()) {
            setEndSizeRange(min, max);
        }
        return this;
    }

    public ParticleEffect setEndSizeRange(Vector2 min, Vector2 max) {
        endSizeRange.set(min, max);
        return this;
    }

    public ParticleEffect setVelocityRange(Vector2 min, Vector2 max) {
        velocityRange.set(min, max);
        return this;
    }

    public ParticleEffect setStartColorRange(Color min, Color max) {
        startColorRange.set(min, max);
        if (!endColorRange.isSet()) {
            setEndColorRange(min, max);
        }
        return this;
    }

    public ParticleEffect setEndColorRange(Color min, Color max) {
        endColorRange.set(min, max);
        return this;
    }

    public ParticleEffect setGrayscale(boolean grayscale) {
        isGrayscale = grayscale;
        return this;
    }

    public ParticleEffect setKeepSizeRatio(boolean keepSizeRatio) {
        this.keepSizeRatio = keepSizeRatio;
        return this;
    }

    public ParticleEffect setIgnoreTimeScale(boolean ignoreTimeScale) {
        this.ignoreTimeScale = ignoreTimeScale;
        return this;
    }

    public ParticleEffect setKeepSquare(boolean keepSquare) {
        this.keepSquare = keepSquare;
        return this;
    }

    public Array<String> getSpriteKeys() {
        return spriteKeys;
    }

    public Vector2 getDurationRange() {
        return durationRange;
    }

    public Vector2 getAccelerationRange() {
        return accelerationRange;
    }

    public Vector2 getAngularVelocityRange() {
        return angularVelocityRange;
    }

    public Vector2 getAngularAccelerationRange() {
        return angularAccelerationRange;
    }

    public Vector2 getNumParticlesRange() {
        return numParticlesRange;
    }

    public Tuple<Vector2, Vector2> getPositionOffsetRange() {
        return positionOffsetRange;
    }

    public Tuple<Vector2, Vector2> getStartSizeRange() {
        return startSizeRange;
    }

    public Tuple<Vector2, Vector2> getEndSizeRange() {
        return endSizeRange;
    }

    public Tuple<Vector2, Vector2> getVelocityRange() {
        return velocityRange;
    }

    public Tuple<Color, Color> getStartColorRange() {
        return startColorRange;
    }

    public Tuple<Color, Color> getEndColorRange() {
        return endColorRange;
    }

    public boolean keepSquare() {
        return keepSquare;
    }

    public boolean ignoreTimeScale() {
        return ignoreTimeScale;
    }
}
