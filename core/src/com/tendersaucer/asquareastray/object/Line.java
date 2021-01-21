package com.tendersaucer.asquareastray.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.CollisionFilters;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.actions.Actions;
import com.tendersaucer.asquareastray.utils.ColorUtils;


public class Line extends GameObject {

    public static final int DEFAULT_LINE_DURATION = 650;

    private float elapsed;
    private final float duration;

    public Line(Level level, float x1, float y1, float x2, float y2, int duration, Color color) {
        super(level);

        this.duration = duration;
        elapsed = 0;
        layer = 0;

        createSprite(color, x1, y1, x2, y2);
        createPhysicsBody(x1, y1, x2, y2);

        width = Math.abs(x2 - x1);
        height = Math.abs(y2 - y1);

        addAction(Actions.fadeOut(duration / 1000.0f));
    }

    private void createSprite(Color color, float x1, float y1, float x2, float y2) {
        sprite = new Sprite();
        sprite.setTexture(ColorUtils.getSolidColorDrawable(color).getRegion().getTexture());

        float length = (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        sprite.setSize(length, Level.LINE_WIDTH);
        sprite.setRotation(MathUtils.radiansToDegrees * (float)Math.atan2(y2 - y1, x2 - x1));
        sprite.setPosition(x1, y1);
        sprite.setOrigin(0, 0);
    }

    private void createPhysicsBody(float x1, float y1, float x2, float y2) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0, 0);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        physicsBody = level.getPhysicsWorld().createBody(bodyDef);

        EdgeShape shape = new EdgeShape();
        shape.set(x1, y1, x2, y2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.2f;
        fixtureDef.density = 10;
        fixtureDef.restitution = Level.DEFAULT_RESTITUTION;
        fixtureDef.filter.categoryBits = CollisionFilters.LINE_CATEGORY;
        fixtureDef.filter.maskBits = CollisionFilters.LINE_MASK;
        physicsBody.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }

    @Override
    public void update() {
        elapsed += Time.getInstance().getDeltaTime() * 1000;
        if (elapsed > duration) {
            setDone();
        }

        updateActions();

        if (isDone) {
            destroy();
        }
    }
}
