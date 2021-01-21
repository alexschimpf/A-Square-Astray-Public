package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Player;
import com.tendersaucer.asquareastray.utils.ShapeUtils;
import com.tendersaucer.asquareastray.object.Properties;

public class LevelLoader {

    private TiledMap tiledMap;
    private final World physicsWorld;
    private final Array<MapObject> gameObjects;

    public LevelLoader() {
        gameObjects = new Array<>();
        physicsWorld = new World(Level.DEFAULT_GRAVITY, true);
        physicsWorld.setContactListener(new CollisionListener());
    }

    public Level loadLevel(int levelId) throws Exception {
        String fileName = getTiledMapFileName(levelId);
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.flipY = true;
        tiledMap = new TmxMapLoader().load(fileName, params);

        loadLayers();

        Level level = buildLevel(levelId);

        if (level.getPlayer() == null) {
            throw new Exception("No player was added to level");
        }
        
        return level;
    }

    private void loadLayers() throws Exception {
        for (MapLayer layer : tiledMap.getLayers()) {
            loadLayer(layer);
        }
    }

    private void loadLayer(MapLayer layer) {
        for (MapObject mapObject : layer.getObjects()) {
            gameObjects.add(mapObject);
        }
    }

    private Level buildLevel(int levelId) throws Exception {
        Level level = new Level(levelId, physicsWorld);

        Properties mapProperties = new Properties(tiledMap.getProperties());

        if (mapProperties.isPropertyEmpty("gravity")) {
            level.getCameraHandler().setCameraVelocity(CameraHandler.DEFAULT_CAMERA_SPEED, 0);
        } else {
            Vector2 gravity = mapProperties.getVector2("gravity");
            gravity.nor().scl(Level.DEFAULT_GRAVITY.len());
            physicsWorld.setGravity(gravity);
            Vector2 newCameraVelocity = new Vector2(gravity).nor().scl(CameraHandler.DEFAULT_CAMERA_SPEED);
            level.getCameraHandler().setCameraVelocity(newCameraVelocity.x, newCameraVelocity.y);
        }

        if (mapProperties.isPropertyEmpty("time_limit_1") ||
                mapProperties.isPropertyEmpty("time_limit_2")) {
            throw new Exception("Level " + levelId + " is missing a time limit");
        }
        level.setTimeLimit1(mapProperties.getInt("time_limit_1"));
        level.setTimeLimit2(mapProperties.getInt("time_limit_2"));

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        Array<GameObject> gameObjects = getGameObjects(level);
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof Player) {
                level.setPlayer((Player)gameObject);
            }
            level.addGameObject(gameObject);

            minX = Math.min(minX, gameObject.getCenterX());
            minY = Math.min(minY, gameObject.getCenterY());
            maxX = Math.max(maxX, gameObject.getCenterX());
            maxY = Math.max(maxY, gameObject.getCenterY());
        }

        level.setSize(maxX - minX, maxY - minY);
        level.setCenter(minX + ((maxX - minX) * 0.5f), minY + ((maxY - minY) * 0.5f));

        return level;
    }

    private Array<GameObject> getGameObjects(Level level) {
        Array<GameObject> gameObjects = new Array<>();
        for (MapObject gameObject : this.gameObjects) {
            Properties properties = new Properties(gameObject.getProperties());
            Shape2D shape = ShapeUtils.getShapeFromMapObject(gameObject);
            if ("player".equals(properties.getString("type"))) {
                gameObjects.add(new Player(level, shape, properties));
            } else {
                gameObjects.add(new GameObject(level, shape, properties, gameObject.getName()));
            }
        }

        return gameObjects;
    }

    private String getTiledMapFileName(int levelId) {
        return AssetManager.getFilePath("levels",  "level_" + levelId + ".tmx");
    }
}
