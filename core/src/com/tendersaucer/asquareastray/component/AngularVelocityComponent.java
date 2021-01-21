package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public class AngularVelocityComponent extends Component {

    private float angularVelocity;

    public AngularVelocityComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        angularVelocity = properties.getFloat("angular_velocity");
    }

    @Override
    public void init() {
        if (parent.getPhysicsBody().getType() == BodyDef.BodyType.StaticBody) {
            parent.getPhysicsBody().setType(BodyDef.BodyType.KinematicBody);
        }
        parent.getPhysicsBody().setFixedRotation(false);
    }

    @Override
    public boolean update() {
        if (level.hasStarted()) {
            parent.getPhysicsBody().setAngularVelocity(angularVelocity);
        }

        return false;
    }
}
