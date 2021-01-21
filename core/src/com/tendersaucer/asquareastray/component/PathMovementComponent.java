package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.actions.Actions;
import com.tendersaucer.asquareastray.object.actions.Action;
import com.tendersaucer.asquareastray.object.actions.SequenceAction;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.object.Properties;

public class PathMovementComponent extends Component {

    private Action action;
    private boolean isActionAdded;

    public PathMovementComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        isActionAdded = false;
    }

    @Override
    public void init() {
        Body parentBody = parent.getPhysicsBody();
        parentBody.setType(BodyDef.BodyType.KinematicBody);

        Array<Vector3> path = new Array<>();
        String[] pathStrings = properties.getStringArray("path", " ");
        for (String pathString : pathStrings) {
            String[] pathVectorStrings = pathString.trim().split(",");
            float x = Float.parseFloat(pathVectorStrings[0]);
            float y = Float.parseFloat(pathVectorStrings[1]);
            float duration = Float.parseFloat(pathVectorStrings[2]);

            Vector3 pathVector = new Vector3(x, y, duration);
            path.add(pathVector);
        }

        SequenceAction sequenceAction = new SequenceAction();
        for (int i = 0; i < path.size; i++) {
            Vector3 segment = path.get(i);
            float dx = segment.x * ConversionUtils.getMetersPerTile();
            float dy = segment.y * ConversionUtils.getMetersPerTile();
            sequenceAction.addAction(Actions.moveBy(dx, dy, segment.z / 1000.0f));
        }

        action = Actions.forever(sequenceAction);
    }

    @Override
    public boolean update() {
        if (!level.hasStarted()) {
            return false;
        } else if (!isActionAdded) {
            isActionAdded = true;
            parent.addAction(action);
        }

        return super.update();
    }
}
