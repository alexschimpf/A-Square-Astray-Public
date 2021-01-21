package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.asquareastray.component.AnimationComponent;
import com.tendersaucer.asquareastray.object.actions.TemporalAction;

public class ColorAction extends TemporalAction {

    private float startR, startG, startB, startA;
    private Color color;
    private final Color end = new Color();

    @Override
    protected void begin () {
        if (color == null) {
            color = getStartColor();
        }
        startR = color.r;
        startG = color.g;
        startB = color.b;
        startA = color.a;
    }

    @Override
    protected void update (float percent) {
        if (percent == 0) {
            color.set(startR, startG, startB, startA);
        } else if (percent == 1) {
            color.set(end);
        } else {
            float r = startR + (end.r - startR) * percent;
            float g = startG + (end.g - startG) * percent;
            float b = startB + (end.b - startB) * percent;
            float a = startA + (end.a - startA) * percent;
            color.set(r, g, b, a);
        }

        updateTargetColor();
    }

    public Color getColor () {
        return color;
    }

    public void setColor (Color color) {
        this.color = color;
    }

    public Color getEndColor () {
        return end;
    }

    public void setEndColor (Color color) {
        end.set(color);
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
