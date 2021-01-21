package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.Properties;

public class AnimationComponent extends Component {

    protected AnimatedImage animation;

    public AnimationComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        String animationKey = properties.getString("name");
        int duration = properties.getInt("duration");
        boolean ignoreTimeScale = properties.getBoolean("ignore_time_scale", false);
        Integer numLoops = properties.propertyExists("num_loops") ? properties.getInt("num_loops") : null;
        AnimatedImage.State state = "stopped".equals(properties.getString("state")) ?
                AnimatedImage.State.STOPPED : AnimatedImage.State.PLAYING;
        animation = new AnimatedImage(animationKey, duration, numLoops, state, ignoreTimeScale);

        animation.setSize(parent.getWidth(), parent.getHeight());
        if (!properties.isPropertyEmpty("scale")) {
            Vector2 scale = properties.getVector2("scale");
            animation.setScale(scale.x, scale.y);
        }
        if (!properties.isPropertyEmpty("color")) {
            Color color = properties.getColor("color");
            animation.setColor(color);
        }
        animation.setOrigin(Align.center);

        if (!properties.isPropertyEmpty("flip")) {
            boolean[] flipDirs = properties.getBooleanArray("flip", ",");
            animation.flip(flipDirs[0], flipDirs[1]);
        }
        if (!properties.isPropertyEmpty("angle")) {
            animation.setRotation(properties.getFloat("angle"));
        }
    }

    @Override
    public boolean update() {
        animation.setPosition(parent.getLeft(), parent.getBottom());
        animation.setRotation(MathUtils.radiansToDegrees * parent.getAngle());
        animation.act(Time.getInstance().getDeltaTime());
        return false;
    }

    @Override
    public void render(Batch batch) {
        animation.draw(batch, 1);
    }

    public AnimatedImage getAnimation() {
        return animation;
    }
}
