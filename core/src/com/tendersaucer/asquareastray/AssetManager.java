package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public final class AssetManager extends com.badlogic.gdx.assets.AssetManager {

    private static final AssetManager instance = new AssetManager();

    private static final String SOUNDS_DIR = "audio/sounds";
    private static final String MUSIC_DIR = "audio/music";
    private static final String TEXTURE_ATLAS_EXTENSION = ".atlas";
    private static final String SOUND_EXTENSION = ".mp3";
    private static final String MUSIC_EXTENSION = ".mp3";

    private AssetManager() {
    }

    public static AssetManager getInstance() {
        return instance;
    }

    public static String getFilePath(String first, String... more) {
        StringBuilder path = new StringBuilder(first);
        for (String p : more) {
            path.append("/").append(p);
        }

        return path.toString();
    }

    public void load() {
        loadTextureAtlas("textures");
        loadSounds();
        loadAllMusic();
        finishLoading();
    }

    public TextureRegion getTextureRegion(String regionId) {
        return getTextureAtlasRegion("textures", regionId);
    }

    public Array<AtlasRegion> getTextureRegions(String regionId) {
        return getTextureAtlasRegions("textures", regionId);
    }

    public Sprite getSprite(String regionId) {
        return new Sprite(getTextureRegion(regionId));
    }

    public Sound getSound(String id) {
        String fileName = getFilePath(SOUNDS_DIR, id + SOUND_EXTENSION);
        if (!isLoaded(fileName)) {
            Gdx.app.log("assets", "Asset '" + fileName + "' has not been loaded");
        }

        return get(fileName, Sound.class);
    }

    public Music getMusic(String id) {
        String fileName = getFilePath(MUSIC_DIR, id + MUSIC_EXTENSION);
        if (!isLoaded(fileName)) {
            Gdx.app.log("assets", "Music '" + fileName + "' has not been loaded");
        }

        return get(fileName, Music.class);
    }

    private void loadTextureAtlas(String id) {
        String fileName = id + TEXTURE_ATLAS_EXTENSION;
        if (!isLoaded(fileName)) {
            load(fileName, TextureAtlas.class);
        }
    }

    private void unloadTextureAtlas(String id) {
        String fileName = id + TEXTURE_ATLAS_EXTENSION;
        if (isLoaded(fileName)) {
            unload(fileName);
        }
    }

    private void loadSound(String id) {
        String fileName = getFilePath(SOUNDS_DIR, id + SOUND_EXTENSION);
        if (!isLoaded(fileName)) {
            load(fileName, Sound.class);
        }
    }

    private void unloadSound(String id) {
        String fileName = getFilePath(SOUNDS_DIR, id + SOUND_EXTENSION);
        if (isLoaded(fileName)) {
            unload(fileName);
        }
    }

    private void loadSounds() {
        FileHandle dir = Gdx.files.internal(SOUNDS_DIR);
        for (FileHandle soundFile : dir.list()) {
            loadSound(soundFile.nameWithoutExtension());
        }
    }

    private void unloadSounds() {
        FileHandle dir = Gdx.files.internal(MUSIC_DIR);
        for (FileHandle soundFile : dir.list()) {
            unloadSound(soundFile.nameWithoutExtension());
        }
    }

    private void loadMusic(String id) {
        String fileName = getFilePath(MUSIC_DIR, id + MUSIC_EXTENSION);
        if (!isLoaded(fileName)) {
            load(fileName, Music.class);
        }
    }

    private void unloadMusic(String id) {
        String fileName = getFilePath(MUSIC_DIR, id + MUSIC_EXTENSION);
        if (isLoaded(fileName)) {
            unload(fileName);
        }
    }

    private void loadAllMusic() {
        FileHandle dir = Gdx.files.internal(MUSIC_DIR);
        for (FileHandle musicFile : dir.list()) {
            loadMusic(musicFile.nameWithoutExtension());
        }
    }

    private void unloadAllMusic() {
        FileHandle dir = Gdx.files.internal(MUSIC_DIR);
        for (FileHandle musicFile : dir.list()) {
            unloadMusic(musicFile.nameWithoutExtension());
        }
    }

    private TextureAtlas getTextureAtlas(String id) {
        String fileName = id + TEXTURE_ATLAS_EXTENSION;
        if (!isLoaded(fileName)) {
            Gdx.app.log("assets", "Asset '" + fileName + "' has not been loaded");
        }

        return get(fileName, TextureAtlas.class);
    }

    private TextureRegion getTextureAtlasRegion(String atlasName, String regionId) {
        TextureAtlas textureAtlas = getTextureAtlas(atlasName);
        TextureRegion textureRegion = textureAtlas.findRegion(regionId);
        return textureRegion;
    }

    private Array<AtlasRegion> getTextureAtlasRegions(String atlasName, String regionId) {
        TextureAtlas textureAtlas = getTextureAtlas(atlasName);
        Array<AtlasRegion> atlasRegions = textureAtlas.findRegions(regionId);
        return atlasRegions;
    }
}