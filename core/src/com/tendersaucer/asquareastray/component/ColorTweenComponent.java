package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public class ColorTweenComponent extends Component {

    private int currIndex;
    private float elapsed;
    private Color[] colors;
    private Float[] durations;

    public ColorTweenComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);
    }

    @Override
    public void init() {
        currIndex = 0;
        elapsed = 0;
        colors = properties.getColorArray("colors");
        durations = properties.getFloatArray("durations", ",");
    }

    @Override
    public boolean update() {
        elapsed += Time.getInstance().getDeltaTime() * 1000;
        if (elapsed > durations[currIndex]) {
            currIndex = currIndex + 1 > colors.length - 1 ? 0 : currIndex + 1;
            elapsed = 0;
        }

        Color startColor = colors[currIndex];

        int nextIndex = currIndex + 1 > colors.length - 1 ? 0 : currIndex + 1;
        Color endColor = colors[nextIndex];

        float progress = MathUtils.clamp(elapsed / durations[currIndex], 0, 1);
        float r = MathUtils.lerp(startColor.r, endColor.r, progress);
        float g = MathUtils.lerp(startColor.g, endColor.g, progress);
        float b = MathUtils.lerp(startColor.b, endColor.b, progress);
        float a = MathUtils.lerp(startColor.a, endColor.a, progress);

        if (parent.getSprite() != null) {
            parent.getSprite().setColor(r, g, b, a);
        }
        if (parent.getOutlineSprites().size > 0) {
            parent.setOutlineColor(r, g, b, a);
        }

        if (parent.getFillSprite() != null) {
            getParent().getFillSprite().setColor(r, g, b, a);
        }

        if (parent.hasComponent(AnimationComponent.class)) {
            AnimatedImage animation = ((AnimationComponent)parent.getComponentByType(
                    AnimationComponent.class)).getAnimation();
            animation.setColor(r, g, b, a);
        }

        return false;
    }
}
