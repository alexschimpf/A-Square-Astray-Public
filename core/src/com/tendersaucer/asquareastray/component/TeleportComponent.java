package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.TeleportEvent;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.level.Canvas;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.object.actions.Actions;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.utils.Tween;

public class TeleportComponent extends Component implements ICollide {

    private static final float TELEPORT_DELAY = 1000;

    private long lastTeleportTime;
    private GameObject exitPortal;
    private final ParticleEffectEmitter emitter;

    public TeleportComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        lastTeleportTime = 0;
        if (properties.isPropertyEmpty("exit_portal_name")) {
            parent.setLayer(0);
        } else {
            parent.setLayer(Canvas.NUM_LAYERS - 1);
        }

        int emitFrequency = (int)(ParticleEffects.PORTAL.getDurationRange().y * 0.5f);
        Vector2 position = new Vector2(parent.getCenterX(), parent.getCenterY());
        emitter = ParticleEffects.getEmitter(
                ParticleEffects.PORTAL, position, emitFrequency);
    }

    @Override
    public void init() {
        this.parent.setDisableContacts(true);

        parent.setComponentsVisible(false);

        String exitPortalName = properties.getString("exit_portal_name");
        if (exitPortalName != null) {
            exitPortal = level.getGameObjectByName(exitPortalName);
        }

        emitParticles();
    }

    @Override
    public boolean update() {
        emitter.setPosition(parent.getCenterX(), parent.getCenterY());

        return super.update();
    }

    @Override
    public void onBeginContact(Contact contact, final GameObject gameObject, boolean isObjectA) {
        if (exitPortal != null && Player.isPlayerCenter(contact, gameObject, isObjectA)) {
            if (TimeUtils.timeSinceMillis(lastTeleportTime) > TELEPORT_DELAY) {
                lastTeleportTime = TimeUtils.millis();
                final Vector2 oldPos = parent.getPhysicsBody().getPosition();
                final Vector2 newPos = exitPortal.getPhysicsBody().getPosition();

                AudioManager.playSound(AudioManager.getInstance().teleportSound);

                float fadeInDuration = level.getPlayer().isDashing() ? 0.01f : 0.3f;
                gameObject.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.fadeOut(0.3f),
                        Actions.moveTo(oldPos.x, oldPos.y, 0.3f)
                    ),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            EventManager.getInstance().postNotify(new TimeScaleEvent(new Tween(0f, 0, 0.01f)));
                            EventManager.getInstance().postNotify(new TeleportEvent(oldPos, newPos));
                        }
                    }),
                    Actions.fadeIn(fadeInDuration)
                ));
            }
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
        if (isExit()) {
            emitter.getEffect().setStartSizeRange(new Vector2(), new Vector2());
            emitter.getEffect().setEndSizeRange(
                    new Vector2(parent.getWidth(), parent.getHeight()),
                    new Vector2(parent.getWidth(), parent.getHeight())
            );
        } else {
            emitter.getEffect().setStartSizeRange(
                    new Vector2(parent.getWidth(), parent.getHeight()),
                    new Vector2(parent.getWidth(), parent.getHeight())
            );
        }

        level.addActor(emitter);
        emitter.emit();
    }

    private boolean isExit() {
        return exitPortal == null;
    }
}
