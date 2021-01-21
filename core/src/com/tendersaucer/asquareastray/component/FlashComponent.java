package com.tendersaucer.asquareastray.component;

import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public class FlashComponent extends Component {

    private float elapsed;
    private final float delay;

    public FlashComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        elapsed = 0;

        // TODO: Rename this to frequency!
        delay = properties.getInt("delay");
    }

    @Override
    public boolean update() {
        elapsed += Time.getInstance().getDeltaTime() * 1000;
        if (elapsed >= delay) {
            parent.setVisible(!parent.isVisible());
            elapsed = 0;
        }

        return false;
    }
}
