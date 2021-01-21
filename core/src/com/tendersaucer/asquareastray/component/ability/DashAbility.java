package com.tendersaucer.asquareastray.component.ability;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.actions.Actions;
import com.tendersaucer.asquareastray.object.actions.ScaleToAction;
import com.tendersaucer.asquareastray.event.AbilityActivatedEvent;
import com.tendersaucer.asquareastray.event.AbilityDeactivatedEvent;
import com.tendersaucer.asquareastray.event.AbilityReadyEvent;
import com.tendersaucer.asquareastray.utils.Tween;

public class DashAbility extends Ability {

    public static final float MUSIC_FADE_RATIO = 0.35f;
    public static final int DEFAULT_SPEED = 50;
    public static final int DEFAULT_DASH_DURATION = 1000;
    public static final int DEFAULT_DASH_COOLDOWN = 10000;

    private int speed;
    private int duration;
    private int cooldown;
    private float time;
    private boolean isActive;
    private float origMusicVolume;
    private Timer.Task cooldownCompletionTask;

    public DashAbility(Level level, GameObject parent, Object... params) {
        super(level, parent);

        speed = (int)params[0];
        duration = (int)params[1];
        cooldown = (int)params[2];

        time = 0;
        isActive = false;
        origMusicVolume = AudioManager.getInstance().musicVolume;
    }

    @Override
    public void init() {
        EventManager.getInstance().listen(AbilityActivatedEvent.class, this);
    }

    @Override
    public boolean update() {
        if (isActive) {
            time += Time.getInstance().getDeltaTime() * 1000;
            float progress = time / duration;

            handleMusicFade(progress);

            if (time > duration) {
                handleDurationEnd();
            }
        }

        return false;
    }

    @Override
    public void destroy() {
        super.destroy();

        EventManager.getInstance().mute(AbilityActivatedEvent.class, this);

        if (cooldownCompletionTask != null) {
            cooldownCompletionTask.cancel();
        }
    }

    @Override
    public AbilityType getType() {
        return AbilityType.DASH;
    }

    @Override
    public void onAbilityActivated() {
        isActive = true;
        AudioManager.playSound(AudioManager.getInstance().dashSound);
        dash();
    }

    private void dash() {
        if (parent.hasAction(ScaleToAction.class)) {
            // TODO: Clean this up at some point
            parent.clearActions();
        }
        parent.addAction(Actions.sequence(
            Actions.scaleTo(0.7f, 0.7f, DEFAULT_DASH_DURATION / 1000.0f * 0.1f, Interpolation.fastSlow),
            Actions.scaleTo(1, 1, DEFAULT_DASH_DURATION / 1000.0f * 0.5f, Interpolation.slowFast)));

        Body physicsBody = parent.getPhysicsBody();
        Vector2 v = new Vector2(physicsBody.getLinearVelocity()).nor().scl(speed, speed);
        if ((v.x < 0 && level.getCameraVelocity().x > 0) || (v.x > 0 &&
                level.getCameraVelocity().x < 0)) {
            v.x *= -1;
        }
        if ((v.y < 0 && level.getCameraVelocity().y > 0) || (v.y > 0 &&
                level.getCameraVelocity().y < 0)) {
            v.y *= -1;
        }
        physicsBody.setLinearVelocity(v);
        
        level.getCameraHandler().addZoomTween(new Tween(1, 0.92f, 100, Interpolation.pow2Out));
        level.getCameraHandler().addZoomTween(new Tween(0.92f, 1, 300, Interpolation.pow2Out));
    }

    private void handleDurationEnd() {
        time = 0;
        isActive = false;
        EventManager.getInstance().notify(new AbilityDeactivatedEvent());
        cooldownCompletionTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                EventManager.getInstance().notify(new AbilityReadyEvent(AbilityType.DASH));
            }
        }, cooldown / 1000.0f);
    }

    private void handleMusicFade(float progress) {
        if (level.isDone()) {
            AudioManager.getInstance().music.setVolume(origMusicVolume);
            AudioManager.getInstance().musicSlow.setVolume(0);
        } else {
            if (progress <= MUSIC_FADE_RATIO) {
                float musicVolume = origMusicVolume * MathUtils.clamp(1 - (progress / MUSIC_FADE_RATIO), 0 , 1);
                float musicSlowVolume = origMusicVolume - musicVolume;
                AudioManager.getInstance().music.setVolume(musicVolume);
                AudioManager.getInstance().musicSlow.setVolume(musicSlowVolume);
            }
            if (progress >= (1 - MUSIC_FADE_RATIO)) {
                float musicVolume = origMusicVolume *
                        MathUtils.clamp((progress - (1 - MUSIC_FADE_RATIO)) / MUSIC_FADE_RATIO, 0, 1);
                float musicSlowVolume = origMusicVolume - musicVolume;
                AudioManager.getInstance().music.setVolume(musicVolume);
                AudioManager.getInstance().musicSlow.setVolume(musicSlowVolume);
            }
        }
    }
}
