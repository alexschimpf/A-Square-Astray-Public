package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public class ContinuousRotationComponent extends Component {

    private int currIndex;
    private float elapsed;
    private float startAngle;
    private Float[] rotations;
    private Float[] durations;

    public ContinuousRotationComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        currIndex = 0;
        elapsed = 0;
        rotations = properties.getFloatArray("rotations", ",");
        durations = properties.getFloatArray("durations", ",");
    }

    @Override
    public void init() {
        startAngle = parent.getAngle();

        if (parent.getPhysicsBody().getType() == BodyType.StaticBody) {
            parent.getPhysicsBody().setType(BodyType.KinematicBody);
        }

        parent.getPhysicsBody().setFixedRotation(false);
    }

    @Override
    public boolean update() {
        if (!level.hasStarted()) {
            return false;
        }

        elapsed += Time.getInstance().getDeltaTime() * 1000;
        float rotation = rotations[currIndex];
        float endAngle = startAngle + rotation;
        float progress = MathUtils.clamp(elapsed / durations[currIndex], 0, 1);
        float angle = MathUtils.lerp(startAngle, endAngle, progress) * MathUtils.degreesToRadians;
        parent.getPhysicsBody().setTransform(parent.getPhysicsBody().getPosition(), angle);

        if (elapsed > durations[currIndex]) {
            elapsed = 0;
            startAngle = endAngle;
            currIndex = currIndex + 1 > rotations.length - 1 ? 0 : currIndex + 1;
        }

        return false;
    }
}
