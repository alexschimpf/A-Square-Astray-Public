package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.tendersaucer.asquareastray.Fonts;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.Strings;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;

public class Tutorial {

    private static final float FADE_DURATION = 0.5f;

    private int step;
    private boolean isBetweenSteps;
    private Label label;
    private EventListener clickListener;
    private final Level level;
    private final HUD hud;

    public Tutorial(Level level, HUD hud) {
        this.level = level;
        this.hud = hud;
        this.step = 0;
        this.isBetweenSteps = false;
    }

    public static boolean showTutorial() {
        return !Persistence.getInstance().getBoolean("tutorial_seen", false);
    }

    public void start() {
        buildUI();

        clickListener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                advanceStep();
            }
        };
        hud.addListener(clickListener);
    }

    private void finish() {
        label.remove();
        hud.removeListener(clickListener);
        Persistence.getInstance().putBoolean("tutorial_seen", true);
        EventManager.getInstance().notify(new LevelStateChangeEvent(LevelState.TUTORIAL, LevelState.COUNTDOWN));
    }

    private void advanceStep() {
        if (isBetweenSteps) {
            return;
        }

        isBetweenSteps = true;
        step++;

        if (step > 5) {
            finish();
        } else {
            SequenceAction sequence = new SequenceAction();
            sequence.addAction(Actions.fadeOut(FADE_DURATION));
            sequence.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    handleStep(step);
                }
            }));
            sequence.addAction(Actions.fadeIn(FADE_DURATION));
            sequence.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    isBetweenSteps = false;
                }
            }));
            label.addAction(sequence);
        }
    }

    private void handleStep(int step) {
        label.setText(Strings.getInstance().get("tutorial_" + (step + 1)));

        if (step == 1) {
            hud.getDashButton().addAction(Actions.forever(Actions.sequence(
                Actions.color(Color.RED, 0.3f),
                Actions.color(level.getColorScheme().dashButtonColor, 0.3f)
            )));
            emitParticles(0, 0);
        } else if (step == 2) {
            hud.getDashButton().clearActions();
            hud.getDashButton().setColor(level.getColorScheme().dashButtonColor);
        } else if (step == 3) {
            hud.getBackButton().addAction(Actions.forever(Actions.sequence(
                Actions.color(Color.RED, 0.3f),
                Actions.color(level.getColorScheme().otherButtonColor, 0.3f)
            )));
            emitParticles(0, Gdx.graphics.getHeight());
        } else if (step == 4) {
            hud.getBackButton().clearActions();
            hud.getBackButton().setColor(level.getColorScheme().otherButtonColor);
            hud.getRestartButton().addAction(Actions.forever(Actions.sequence(
                Actions.color(Color.RED, 0.3f),
                Actions.color(level.getColorScheme().otherButtonColor, 0.3f)
            )));
            emitParticles(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else if (step == 5) {
            hud.getRestartButton().clearActions();
            hud.getRestartButton().setColor(level.getColorScheme().otherButtonColor);
        }
    }

    private void buildUI() {
        buildLabel();
    }

    private void buildLabel() {
        Label.LabelStyle style = new Label.LabelStyle(Fonts.TUTORIAL, Color.WHITE);
        String text = Strings.getInstance().get("tutorial_1");
        label = new Label(text, style);
        label.setAlignment(Align.center);
        label.setSize(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.4f);
        label.setPosition(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.3f);
        label.setWrap(true);
        label.setColor(Color.WHITE);
        label.getColor().a = 0;
        label.addAction(Actions.fadeIn(FADE_DURATION));
        hud.addActor(label);
    }

    private void emitParticles(float x, float y) {
        Vector2 position = new Vector2(x, y);
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(ParticleEffects.TUTORIAL_INDICATOR, position);
        hud.addActor(emitter);
        emitter.emit();
    }
}
