package com.tendersaucer.asquareastray.object.actions;

import com.tendersaucer.asquareastray.object.actions.TemporalAction;

public class MoveToAction extends TemporalAction {

    private float startX, startY;
    private float endX, endY;

    protected void begin() {
        startX = target.getCenterX();
        startY = target.getCenterY();
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
        target.getPhysicsBody().setTransform(x, y, target.getPhysicsBody().getAngle());
    }

    public void setStartPosition(float x, float y) {
        startX = x;
        startY = y;
    }

    public void setPosition(float x, float y) {
        endX = x;
        endY = y;
    }

    public float getX() {
        return endX;
    }

    public void setX(float x) {
        endX = x;
    }

    public float getY() {
        return endY;
    }

    public void setY(float y) {
        endY = y;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }
}
