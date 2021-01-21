package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class MoveByAction extends RelativeTemporalAction {

    private float amountX, amountY;

    protected void updateRelative(float percentDelta) {
        Body body = target.getPhysicsBody();
        Vector2 center = body.getWorldCenter();
        float newX = center.x + (amountX * percentDelta);
        float newY = center.y + (amountY * percentDelta);
        body.setTransform(newX, newY, body.getAngle());
    }

    public void setAmount(float x, float y) {
        amountX = x;
        amountY = y;
    }

    public float getAmountX() {
        return amountX;
    }

    public void setAmountX(float x) {
        amountX = x;
    }

    public float getAmountY() {
        return amountY;
    }

    public void setAmountY(float y) {
        amountY = y;
    }
}