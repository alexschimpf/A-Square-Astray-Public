package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.utils.ConversionUtils;

public class FinishLineComponent extends Component implements ICollide {

    private boolean isDone;
    private Sprite sprite1;
    private Sprite sprite2;
    private final Vector2 direction;
    private final ParticleEffectEmitter emitter;

    public FinishLineComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        direction = properties.getVector2("direction");
        emitter = ParticleEffects.getEmitter(ParticleEffects.FINISH_LINE, new Vector2(), 20);
    }

    @Override
    public void init() {
        isDone = false;
        parent.setVisible(false);
        parent.setDisableContacts(true);
        emitParticles();

        float size = ConversionUtils.getMetersPerTile() * 0.6f;
        Color color = new Color(level.getColorScheme().lineColor);
        color.a = 0.2f;
        sprite1 = AssetManager.getInstance().getSprite("circle");
        sprite1.setColor(color);
        sprite1.setSize(size, size);
        sprite2 = AssetManager.getInstance().getSprite("circle");
        sprite2.setColor(color);
        sprite2.setSize(size, size);
    }

    @Override
    public void render(Batch batch) {
        sprite1.draw(batch);
        sprite2.draw(batch);
    }

    @Override
    public boolean update() {
        if (direction.x > 0) {
            emitter.setPosition(parent.getRight(), parent.getCenterY());
        } else if (direction.x < 0) {
            emitter.setPosition(parent.getLeft(), parent.getCenterY());
        } else if (direction.y > 0) {
            emitter.setPosition(parent.getCenterX(), parent.getTop());
        } else if (direction.y < 0) {
            emitter.setPosition(parent.getCenterX(), parent.getBottom());
        }

        float size = sprite1.getWidth();
        if (Math.abs(direction.x) > 0) {
            sprite1.setPosition(parent.getCenterX() - (size * 0.5f), parent.getTop() - (size * 0.5f));
            sprite2.setPosition(parent.getCenterX() - (size * 0.5f), parent.getBottom() - (size * 0.5f));
        } else {
            sprite1.setPosition(parent.getLeft() - (size * 0.5f), parent.getCenterY() - (size * 0.5f));
            sprite2.setPosition(parent.getRight() - (size * 0.5f), parent.getCenterY() - (size * 0.5f));
        }

        return false;
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (!isDone && !level.isDone() && level.isRunning() &&
                Player.isPlayer(contact, gameObject, isObjectA)) {
            isDone = true;
            emitter.stop();
            EventManager.getInstance().notify(
                    new LevelStateChangeEvent(level.getState(), LevelState.DONE_SUCCESS));
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
    }

    private void emitParticles() {
        // Note: This only handles strictly vertical/horizontal objects
        if (direction.x > 0) {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(0, -parent.getHeight() * 0.45f), new Vector2(0, parent.getHeight() * 0.45f));
            emitter.getEffect().setAccelerationRange(0, 3);
            emitter.getEffect().setVelocityRange(new Vector2(2, -1), new Vector2(4, 1));
        } else if (direction.x < 0) {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(0, -parent.getHeight() * 0.45f), new Vector2(0, parent.getHeight() * 0.45f));
            emitter.getEffect().setAccelerationRange(-3, 0);
            emitter.getEffect().setVelocityRange(new Vector2(-4, -1), new Vector2(-2, 1));
        } else if (direction.y > 0) {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(-parent.getWidth() * 0.45f, 0), new Vector2(parent.getWidth() * 0.45f, 0));
            emitter.getEffect().setAccelerationRange(0, 3);
            emitter.getEffect().setVelocityRange(new Vector2(-1, 2), new Vector2(1, 4));
        } else if (direction.y < 0) {
            emitter.getEffect().setPositionOffsetRange(
                    new Vector2(-parent.getWidth() * 0.45f, 0), new Vector2(parent.getWidth() * 0.45f, 0));
            emitter.getEffect().setAccelerationRange(-3, 0);
            emitter.getEffect().setVelocityRange(new Vector2(-1, -4), new Vector2(1, -2));
        }

        emitter.emit();
        level.addActor(emitter);
    }
}