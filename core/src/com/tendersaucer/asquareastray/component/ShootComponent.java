package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.level.CollisionFilters;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.object.Properties;

public class ShootComponent extends Component {

    private float elapsedSinceLastShot;
    private final int frequency;
    private final Float projectileSize;
    private final Vector2 projectileVelocity;

    public ShootComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        frequency = properties.getInt("frequency");
        projectileVelocity = properties.getVector2("velocity");
        projectileSize = properties.getFloat("size");

        if (!properties.isPropertyEmpty("delay")) {
            elapsedSinceLastShot -= properties.getInt("delay");
        }

        parent.setLayer(GameObject.DEFAULT_LAYER - 1);
    }

    @Override
    public boolean update() {
        if (level.hasStarted()) {
            elapsedSinceLastShot += Time.getInstance().getDeltaTime() * 1000;
            if (elapsedSinceLastShot > frequency) {
                shoot();
            }
        }

        return false;
    }

    private void shoot() {
        elapsedSinceLastShot = 0;

        float size = getProjectileSize();
        Vector2 position = getProjectileStartPosition();
        Rectangle rectangle = new Rectangle(position.x, position.y, size, size);

        AnimationComponent animationComponent =
                (AnimationComponent)parent.getComponentByType(AnimationComponent.class);
        animationComponent.getAnimation().setState(AnimatedImage.State.PLAYING);

        Properties properties = new Properties();
        properties.put("body_type", "dynamic");
        properties.put("fixed_rotation", true);
        properties.put("disable_outlines", true);
        properties.put("width", size);
        properties.put("height", size);
        properties.put("category_bits", CollisionFilters.PROJECTILE_CATEGORY);
        properties.put("mask_bits", CollisionFilters.PROJECTILE_MASK);

        GameObject projectile = new GameObject(level, rectangle, properties);
        projectile.addComponent(new ProjectileComponent(level, projectile));

        Properties destructibleProperties = new Properties();
        destructibleProperties.put("emitter_start_color", "1,0,0,1");
        destructibleProperties.put("emitter_end_color", "1,0,0,0");
        projectile.addComponent(new DestructibleComponent(level, projectile, destructibleProperties));

        Properties fatalProperties = new Properties();
        fatalProperties.put("add_color_tween", false);
        projectile.addComponent(new FatalComponent(level, projectile, fatalProperties));

        projectile.getPhysicsBody().setGravityScale(0);
        projectile.getPhysicsBody().setLinearVelocity(projectileVelocity);
        projectile.init();

        level.addGameObject(projectile);
    }

    private Vector2 getProjectileStartPosition() {
        GameObject parent = getParent();
        float projectileSize = getProjectileSize();
        float x = (parent.getCenterX() * ConversionUtils.getPixelsPerMeter()) - (projectileSize * 0.5f);
        float y = (parent.getCenterY() * ConversionUtils.getPixelsPerMeter())  - (projectileSize * 0.5f);
        return new Vector2(x, y);
    }

    private float getProjectileSize() {
        float size;
        if (projectileSize != null) {
            size = projectileSize * ConversionUtils.getPixelsPerTile();
        } else {
            size = Math.min(parent.getWidth(), parent.getHeight()) *
                    ConversionUtils.getPixelsPerMeter() * 0.5f;
        }
        return size;
    }
}
