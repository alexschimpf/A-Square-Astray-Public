package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public class DiscreteRotationComponent extends Component {

    private int currIndex;
    private float elapsed;
    private Float[] angles;
    private Float[] durations;

    public DiscreteRotationComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        currIndex = 0;
        elapsed = 0;
        angles = properties.getFloatArray("angles", ",");
        durations = properties.getFloatArray("durations", ",");
    }

    @Override
    public void init() {
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
        if (elapsed > durations[currIndex]) {
            currIndex = currIndex + 1 > angles.length - 1 ? 0 : currIndex + 1;
            elapsed = 0;
        }

        float angle = angles[currIndex];
        parent.setAngle(angle);

        return false;
    }
}
