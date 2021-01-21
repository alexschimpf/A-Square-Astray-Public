package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.object.Properties;

public class SlowdownComponent extends Component implements ICollide {

    private static final float SLOWDOWN_FACTOR = 0.25f;

    private final float slowdownFactor;

    public SlowdownComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        parent.setLayer(0);
        slowdownFactor = properties.getFloat("slowdown_factor", SLOWDOWN_FACTOR);
    }

    @Override
    public boolean update() {
        return super.update();
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (Player.isPlayerCenter(contact, gameObject, isObjectA)) {
            ((Player)gameObject).setMaxSpeed(Player.DEFAULT_MAX_SPEED * slowdownFactor);
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (Player.isPlayerCenter(contact, gameObject, isObjectA)) {
            ((Player)gameObject).setMaxSpeed(Player.DEFAULT_MAX_SPEED);
        }
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
    }
}
