package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.component.ability.DashAbility;
import com.tendersaucer.asquareastray.event.AutoDashEvent;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.particle.ParticleEffects;

public class AutoDashComponent extends Component implements ICollide {

    public AutoDashComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        parent.setLayer(0);
    }

    @Override
    public void init() {
        parent.setDisableContacts(true);

        // This component assumes the parent also has an animation component
        AnimatedImage animation = ((AnimationComponent)parent.getComponentByType(
                AnimationComponent.class)).getAnimation();
        animation.setColor(new Color(0.7f, 0.7f, 0.7f, 1));
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (Player.isPlayerCenter(contact, gameObject, isObjectA)) {
            ParticleEffectEmitter emitter = ParticleEffects.getEmitter(
                    ParticleEffects.AUTO_DASH, new Vector2(parent.getCenterX(), parent.getCenterY()));
            level.addActor(emitter);
            emitter.emit();

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    Vector2 parentCenter = new Vector2(parent.getCenterX(), parent.getCenterY());
                    float angle = MathUtils.radiansToDegrees * parent.getAngle();
                    Vector2 velocity = new Vector2(1, 0).rotate(angle).scl(
                            DashAbility.DEFAULT_SPEED, DashAbility.DEFAULT_SPEED);
                    EventManager.getInstance().notify(new AutoDashEvent(parentCenter, velocity));

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            parent.destroy();
                        }
                    });

                    AudioManager.playSound(AudioManager.getInstance().autoDashSound);
                }
            });
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
}
