package com.tendersaucer.asquareastray;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.asquareastray.ads.IAdListener;
import com.tendersaucer.asquareastray.event.listener.ISettingsChangeListener;

public class AudioManager implements ISettingsChangeListener, IAdListener {

    private static final AudioManager instance = new AudioManager();

    public float soundVolume;
    public float musicVolume;

    public final Sound collideSound;
    public final Sound clickSound;
    public final Sound dashSound;
    public final Sound autoDashSound;
    public final Sound countdown1Sound;
    public final Sound countdown2Sound;
    public final Sound countdown3Sound;
    public final Sound star1Sound;
    public final Sound star2Sound;
    public final Sound star3Sound;
    public final Sound deathSound;
    public final Sound unlockSound;
    public final Sound unlockFinalSound;
    public final Sound teleportSound;
    public final Sound gravitySwitchSound;
    public final Sound unlockLevelSound;

    // These should be treated as sounds
    public final Music successMusic;
    public final Music failureMusic;

    public final Music music;
    public final Music musicSlow;

    public AudioManager() {
        soundVolume = 1;
        musicVolume = 1;

        collideSound = AssetManager.getInstance().getSound("collide");
        clickSound = AssetManager.getInstance().getSound("click");
        dashSound = AssetManager.getInstance().getSound("dash");
        autoDashSound = AssetManager.getInstance().getSound("auto_dash");
        countdown1Sound = AssetManager.getInstance().getSound("countdown_1");
        countdown2Sound = AssetManager.getInstance().getSound("countdown_2");
        countdown3Sound = AssetManager.getInstance().getSound("countdown_3");
        star1Sound = AssetManager.getInstance().getSound("star_1");
        star2Sound = AssetManager.getInstance().getSound("star_2");
        star3Sound = AssetManager.getInstance().getSound("star_3");
        deathSound = AssetManager.getInstance().getSound("death");
        unlockSound = AssetManager.getInstance().getSound("unlock");
        unlockFinalSound = AssetManager.getInstance().getSound("unlock_final");
        teleportSound = AssetManager.getInstance().getSound("teleport");
        gravitySwitchSound = AssetManager.getInstance().getSound("gravity_switch");
        unlockLevelSound = AssetManager.getInstance().getSound("unlock_level");

        successMusic = AssetManager.getInstance().getMusic("success");
        failureMusic = AssetManager.getInstance().getMusic("failure");

        music = AssetManager.getInstance().getMusic("music");
        music.setLooping(true);

        musicSlow = AssetManager.getInstance().getMusic("music_slow");
        musicSlow.setLooping(true);

        handleUserSettings();
    }

    public static AudioManager getInstance() {
        return instance;
    }

    @Override
    public void onSettingsChange(String key, Object value) {
        if (key.equals("settings_enable_background_music")) {
            setMusicVolumeImmediately((float)value);
        } else if (key.equals("settings_enable_sound_effects")) {
            soundVolume = (float)value;
        }
    }

    @Override
    public void onAdOpened() {
        music.pause();
        musicSlow.pause();;
    }

    @Override
    public void onAdClosed() {
        music.play();
        musicSlow.play();
    }

    public void dispose() {
        collideSound.dispose();
        clickSound.dispose();
        dashSound.dispose();
        autoDashSound.dispose();
        countdown1Sound.dispose();
        countdown2Sound.dispose();
        countdown3Sound.dispose();
        star1Sound.dispose();
        star2Sound.dispose();
        star3Sound.dispose();
        deathSound.dispose();
        unlockSound.dispose();
        unlockFinalSound.dispose();
        teleportSound.dispose();
        gravitySwitchSound.dispose();
        unlockLevelSound.dispose();
        music.dispose();
        musicSlow.dispose();
        successMusic.dispose();
        failureMusic.dispose();
    }

    public static void playSound(Sound sound) {
        playSound(sound, 1);
    }

    public static void playSound(Sound sound, float scale) {
        long soundId = sound.play();
        float volume = MathUtils.clamp(AudioManager.getInstance().soundVolume * scale, 0 , 1);
        sound.setVolume(soundId, volume);
    }

    public static void playMusic(Music music) {
        playMusic(music, 1);
    }

    public static void playMusic(Music music, float scale) {
        if (!music.isPlaying()) {
            // Some of the "music" should really be thought of as sounds
            float volume = AudioManager.getInstance().musicVolume;
            if (music == AudioManager.getInstance().successMusic ||
                    music == AudioManager.getInstance().failureMusic) {
                volume = AudioManager.getInstance().soundVolume;
            }
            music.setVolume(volume * scale);
            music.play();
        }
    }

    public static void stopMusic(Music music) {
        music.stop();
    }

    private void setMusicVolumeImmediately(float volume) {
        music.setVolume(volume);
        musicSlow.setVolume(volume);
    }

    private void handleUserSettings() {
        musicVolume = Persistence.getInstance().getFloat("settings_enable_background_music", 1);
        soundVolume = Persistence.getInstance().getFloat("settings_enable_sound_effects", 1);
    }
}
