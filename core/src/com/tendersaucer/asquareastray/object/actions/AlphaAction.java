package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.asquareastray.component.AnimationComponent;
import com.tendersaucer.asquareastray.object.actions.TemporalAction;

public class AlphaAction extends TemporalAction {

    private float start, end;
    private Color color;

    @Override
    protected void begin() {
        if (color == null) {
            color = getStartColor();
        }
        start = color.a;
    }

    @Override
    protected void update(float percent) {
        if (percent == 0) {
            color.a = start;
        } else if (percent == 1) {
            color.a = end;
        } else {
            color.a = start + (end - start) * percent;
        }

        updateTargetColor();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getAlpha() {
        return end;
    }

    public void setAlpha(float alpha) {
        this.end = alpha;
    }

    private void updateTargetColor() {
        if (target.getSprite() != null) {
            target.getSprite().setColor(color);
        } else if (target.getFillSprite() != null) {
            target.getFillSprite().setColor(color);
        } else if (target.hasComponent(AnimationComponent.class)) {
            AnimationComponent animation = (AnimationComponent)target.getComponentByType(AnimationComponent.class);
            animation.getAnimation().setColor(color);
        }
    }

    private Color getStartColor() {
        Color color = null;
        if (target.getSprite() != null) {
            color = target.getSprite().getColor();
        } else if (target.getFillSprite() != null) {
            color = target.getFillSprite().getColor();
        } else if (target.hasComponent(AnimationComponent.class)) {
            AnimationComponent animation = (AnimationComponent)target.getComponentByType(AnimationComponent.class);
            color = animation.getAnimation().getColor();
        }
        return color;
    }
}
