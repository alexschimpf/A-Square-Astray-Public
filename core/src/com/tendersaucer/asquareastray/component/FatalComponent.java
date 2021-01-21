package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.component.ColorTweenComponent;
import com.tendersaucer.asquareastray.object.ICollide;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.object.Properties;

public class FatalComponent extends Component implements ICollide {

    private boolean isDone;

    public FatalComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        parent.setLayer(0);
    }

    public FatalComponent(Level level, GameObject parent) {
        this(level, parent, null);
    }

    @Override
    public void init() {
        isDone = false;
        parent.setDisableContacts(true);

        if (properties.getBoolean("add_color_tween", true)) {
            Properties colorTweenProperties = new Properties();
            Color lineColor = level.getColorScheme().lineColor;
            String lineColorStr = lineColor.r + "," + lineColor.g + "," + lineColor.b + "," + lineColor.a;
            colorTweenProperties.put("colors", "1,0,0,1 " + lineColorStr);
            colorTweenProperties.put("durations", "300,300");
            parent.addComponent(new ColorTweenComponent(level, parent, colorTweenProperties));
        }
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        if (!level.isDone() && !isDone && parent.isVisible() && level.isRunning() &&
                Player.isPlayer(contact, gameObject, isObjectA)) {
            isDone = true;
            AudioManager.playSound(AudioManager.getInstance().deathSound);
            EventManager.getInstance().notify(
                    new LevelStateChangeEvent(level.getState(), LevelState.DONE_FAILURE));
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
