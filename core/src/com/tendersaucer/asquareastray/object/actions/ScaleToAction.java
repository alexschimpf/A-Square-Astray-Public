package com.tendersaucer.asquareastray.object.actions;


import com.tendersaucer.asquareastray.component.AnimationComponent;
import com.tendersaucer.asquareastray.object.actions.TemporalAction;

public class ScaleToAction extends TemporalAction {

    private float startX, startY;
    private float endX, endY;

    protected void begin() {
        if (target.getSprite() != null) {
            startX = target.getSprite().getScaleX();
            startY = target.getSprite().getScaleY();
        } else if (target.getFillSprite() != null) {
            startX = target.getFillSprite().getScaleX();
            startY = target.getFillSprite().getScaleY();
        } else if (target.hasComponent(AnimationComponent.class)) {
            AnimationComponent animation = (AnimationComponent)target.getComponentByType(AnimationComponent.class);
            startX = animation.getAnimation().getScaleX();
            startY = animation.getAnimation().getScaleY();
        }
    }

    protected void update(float percent) {
        float x, y;
        if (percent == 0) {
            x = startX;
            y = startY;
        } else if (percent == 1) {
            x = endX;
            y = endY;
        } else {
            x = startX + (endX - startX) * percent;
            y = startY + (endY - startY) * percent;
        }
        if (target.getSprite() != null) {
            target.getSprite().setScale(x, y);
        } else if (target.getFillSprite() != null) {
            target.getFillSprite().setScale(x, y);
        } else if (target.hasComponent(AnimationComponent.class)) {
            AnimationComponent animation = (AnimationComponent)target.getComponentByType(AnimationComponent.class);
            animation.getAnimation().setScale(x, y);
        }
    }

    public void setScale(float x, float y) {
        endX = x;
        endY = y;
    }

    public void setScale(float scale) {
        endX = scale;
        endY = scale;
    }

    public float getX() {
        return endX;
    }

    public void setX(float x) {
        this.endX = x;
    }

    public float getY() {
        return endY;
    }

    public void setY(float y) {
        this.endY = y;
    }
}
