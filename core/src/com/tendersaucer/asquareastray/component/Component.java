package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.object.Properties;

public abstract class Component {

    protected final Level level;
    protected final GameObject parent;
    protected final Properties properties;

    public Component(Level level, GameObject parent) {
        this(level, parent, null);
    }

    public Component(Level level, GameObject parent, Properties properties) {
        this.level = level;
        this.parent = parent;
        this.properties = properties;
    }

    public static final Component buildComponent(String componentType, Level level, GameObject parent,
                                                 Properties properties) throws Exception {
        componentType = componentType.substring(1);

        Class<?> c = ClassReflection.forName("com.tendersaucer.asquareastray.component." + componentType + "Component");
        Constructor constructor = ClassReflection.getDeclaredConstructor(
                c, Level.class, GameObject.class, Properties.class);
        constructor.setAccessible(true);
        return (Component)constructor.newInstance(level, parent, properties);
    }

    public void init() {
    }

    public boolean update() {
        return false;
    }

    public void render(Batch batch) {
    }

    public void destroy() {
    }

    public GameObject getParent() {
        return parent;
    }
}
