package com.tendersaucer.asquareastray.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.Time;

public class AnimatedImage extends Image {

    public enum State {
        PLAYING, STOPPED
    }

    protected int frameIndex;
    protected float stateTime;
    protected int currNumLoops;
    protected float frameDuration;
    protected boolean ignoreTimeScale;
    protected State state;
    protected final Integer numLoops;
    protected final Array<TextureRegionDrawable> frames = new Array<>();

    public AnimatedImage(String key, int totalDuration, Integer numLoops, State state, boolean ignoreTimeScale) {
        super();

        this.ignoreTimeScale = ignoreTimeScale;

        Array<TextureAtlas.AtlasRegion> textureRegions = AssetManager.getInstance().getTextureRegions(key);
        if (textureRegions.size == 0) {
            Gdx.app.error(Globals.LOG_TAG, "Animation/image not found: " + key);
        }

        frameDuration = (float)totalDuration / textureRegions.size / 1000;

        for (TextureAtlas.AtlasRegion textureRegion : textureRegions) {
            TextureRegionDrawable frame = new TextureRegionDrawable(textureRegion);
            frames.add(frame);
        }

        currNumLoops = 0;
        this.numLoops = numLoops;
        this.state = state;

        setDrawable(frames.get(0));
    }

    public AnimatedImage(String key, int totalDuration, Integer numLoops) {
        this(key, totalDuration, numLoops, State.STOPPED, false);
    }

    public AnimatedImage(String key, int totalDuration) {
        this(key, totalDuration, 1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (state != State.PLAYING) {
            return;
        }

        if (ignoreTimeScale) {
            stateTime += Gdx.graphics.getDeltaTime();
        } else {
            stateTime += Time.getInstance().getDeltaTime();
        }

        int lastFrameIndex = frameIndex;
        frameIndex = (int)(stateTime / frameDuration) % frames.size;
        boolean hasFrameChanged = frameIndex != lastFrameIndex;
        if (hasFrameChanged && frameIndex == 0) {
            if (numLoops != null && ++currNumLoops >= numLoops) {
                setState(State.STOPPED);
                return;
            }
        }

        setDrawable(getCurrFrame());
    }

    public void setState(State state) {
        this.state = state;
        if (this.state == State.STOPPED) {
            frameIndex = 0;
            stateTime = 0;
            currNumLoops = 0;
        }
    }

    public float getTotalDuration() {
        return frameDuration * frames.size * 1000;
    }

    public TextureRegionDrawable getCurrFrame() {
        int frameIndex = (int)(stateTime / frameDuration) % frames.size;
        return frames.get(frameIndex);
    }

    public boolean isIgnoringTimeScale() {
        return this.ignoreTimeScale;
    }

    public void setIgnoreTimeScale(boolean ignoreTimeScale) {
        this.ignoreTimeScale = ignoreTimeScale;
    }

    public void flip(boolean x, boolean y) {
        for (TextureRegionDrawable frame : frames) {
            frame.getRegion().flip(x, y);
        }
    }
}
