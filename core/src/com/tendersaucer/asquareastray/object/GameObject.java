package com.tendersaucer.asquareastray.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.Time;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.actions.ParallelAction;
import com.tendersaucer.asquareastray.utils.PhysicsBodyData;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.level.ICanvasItem;
import com.tendersaucer.asquareastray.component.Component;
import com.tendersaucer.asquareastray.object.actions.Action;
import com.tendersaucer.asquareastray.object.actions.RepeatAction;
import com.tendersaucer.asquareastray.object.actions.SequenceAction;
import com.tendersaucer.asquareastray.utils.ColorUtils;
import com.tendersaucer.asquareastray.utils.ConversionUtils;
import com.tendersaucer.asquareastray.utils.ShapeUtils;
import com.tendersaucer.asquareastray.utils.StringUtils;

import java.util.Iterator;

public class GameObject implements ICanvasItem, ICollide {

    public static final int DEFAULT_LAYER = 2;

    protected boolean isDone;
    protected float width;
    protected float height;
    protected boolean isVisible;
    protected boolean areComponentsVisible;
    protected boolean disableContacts;
    protected int layer;
    protected Body physicsBody;
    protected Shape2D shape;
    protected Sprite sprite;
    protected PolygonSprite fillSprite;
    protected final String name;
    protected final Level level;
    protected final Color outlineColor;
    protected final Array<String> tags;
    protected final Array<Component> components;
    protected final Rectangle bounds;
    protected final Array<Sprite> outlineSprites;
    protected final Properties properties;
    protected final Array<Action> actions;

    public GameObject(Level level, Shape2D shape, Properties properties, String name) {
        this.level = level;
        this.properties = properties;
        this.name = name;
        isDone = false;
        isVisible = !properties.getBoolean("invisible", false);
        areComponentsVisible = !properties.getBoolean("components_invisible", false);
        outlineSprites = new Array<>();
        tags = new Array<>();
        components = new Array<>();
        bounds = new Rectangle();
        outlineColor = new Color(Color.WHITE);
        disableContacts = false;
        layer = DEFAULT_LAYER;
        actions = new Array<>();

        if (properties.propertyExists("width") && properties.propertyExists("height")) {
            width = properties.getFloat("width") * ConversionUtils.getMetersPerPixel();
            height = properties.getFloat("height") * ConversionUtils.getMetersPerPixel();
        }

        if (properties.propertyExists("tags")) {
            String[] tags = properties.getStringArray("tags", ",");
            this.tags.addAll(tags);
        }

        BodyType bodyType = getBodyType(properties);
        physicsBody = createPhysicsBody(level, shape, properties, bodyType);
        if (!properties.isPropertyEmpty("angle")) {
            float angle = MathUtils.degreesToRadians * properties.getInt("angle");
            physicsBody.setTransform(getCenterX(), getCenterY(), angle);
        }

        if (isVisible) {
            if (!properties.isPropertyEmpty("image")) {
                String imageKey = properties.getString("image");
                sprite = AssetManager.getInstance().getSprite(imageKey);
                if (width == 0 && height == 0) {
                    width = sprite.getWidth();
                    height = sprite.getHeight();
                }
                if (!properties.isPropertyEmpty("flip_image")) {
                    boolean[] flipDirs = properties.getBooleanArray("flip_image", ",");
                    sprite.flip(flipDirs[0], flipDirs[1]);
                }
            }

            if (properties.getBoolean("fill", false)) {
                Color color = properties.getColor("fill_color");
                if (color == null) {
                    color = new Color(level.getColorScheme().fillColor);
                }
                fillSprite = getFillSprite(shape, color, 1);
                if (width == 0 && height == 0) {
                    width = fillSprite.getWidth();
                    height = fillSprite.getHeight();
                }
            }
        }

        buildComponents(properties);

        // If the object itself has a layer property,
        // it will override any layer set by its components
        if (!properties.isPropertyEmpty("layer")) {
            layer = properties.getInt("layer");
        }
    }

    public GameObject(Level level, Shape2D shape, Properties properties) {
        this(level, shape, properties, null);
    }

    public GameObject(Level level) {
        this.level = level;

        name = null;
        isDone = false;
        isVisible = true;
        outlineSprites = new Array<>();
        tags = new Array<>();
        components = new Array<>();
        bounds = new Rectangle();
        outlineColor = new Color(Color.WHITE);
        disableContacts = false;
        layer = DEFAULT_LAYER;
        actions = new Array<>();
        properties = null;
    }

    public void init() {
        if (isVisible) {
            buildShape();
            if (!properties.getBoolean("disable_outlines", false)) {
                buildOutlineSprites();
            }
            if (!properties.isPropertyEmpty("line_color")) {
                setOutlineColor(properties.getColor("line_color"));
            } else {
                setOutlineColor(level.getColorScheme().lineColor);
            }
        }

        for (Component component : components) {
            component.init();
        }

        if (sprite != null) {
            sprite.setSize(width, height);
            sprite.setOrigin(width / 2, height / 2);
        }
    }

    @Override
    public void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        for (Component component : components) {
            if (component instanceof ICollide) {
                ((ICollide) component).onBeginContact(contact, gameObject, isObjectA);
            }
        }
    }

    @Override
    public void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA) {
        for (Component component : components) {
            if (component instanceof ICollide) {
                ((ICollide) component).onEndContact(contact, gameObject, isObjectA);
            }
        }
    }

    @Override
    public void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA) {
        if (disableContacts) {
            contact.setEnabled(false);
        }

        for (Component component : components) {
            if (component instanceof ICollide) {
                ((ICollide) component).onPreSolve(contact, oldManifold, gameObject, isObjectA);
            }
        }
    }

    @Override
    public void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA) {
        for (Component component : components) {
            if (component instanceof ICollide) {
                ((ICollide) component).onPostSolve(contact, impulse, gameObject, isObjectA);
            }
        }
    }

    @Override
    public int getLayer() {
        return layer;
    }

    /**
     * Note: This will only take affect if it is called before the object is added to the canvas
     */
    @Override
    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void update() {
        if (sprite != null) {
            sprite.setPosition(getLeft(), getBottom());
            sprite.setRotation(MathUtils.radiansToDegrees * physicsBody.getAngle());
        }
        if (fillSprite != null) {
            fillSprite.setPosition(getCenterX(), getCenterY());
            fillSprite.setRotation(MathUtils.radiansToDegrees * physicsBody.getAngle());
        }

        if (shape != null) {
            Vector2 position = physicsBody.getPosition();
            float angle = MathUtils.radiansToDegrees * physicsBody.getAngle();
            if (shape instanceof Polygon) {
                Polygon polygon = (Polygon) shape;
                polygon.setPosition(position.x, position.y);
                polygon.setRotation(angle);
            } else if (shape instanceof Polyline) {
                Polyline polyline = (Polyline) shape;
                polyline.setPosition(position.x, position.y);
                polyline.setRotation(angle);
            }

            updateOutlineSprites();
        }

        updateComponents();
        updateActions();

        if (isDone) {
            destroy();
        }
    }

    private void updateComponents() {
        Iterator<Component> iter = components.iterator();
        while (iter.hasNext()) {
            Component component = iter.next();
            if (component.update()) {
                component.destroy();
                iter.remove();
            }
        }
    }

    protected void updateActions() {
        Iterator<Action> iter = actions.iterator();
        while (iter.hasNext()) {
            Action action = iter.next();

            float delta = Time.getInstance().getDeltaTime();
            if (action.act(delta)) {
                action.setTarget(null);
                iter.remove();
            }
        }
    }

    @Override
    public boolean render(SpriteBatch batch) {
        // TODO: This is kinda hacky
        if (areComponentsVisible) {
            for (Component component : components) {
                component.render(batch);
            }
        }

        if (!isVisible) {
            return false;
        }

        if (sprite != null) {
            sprite.draw(batch);
        }
        for (Sprite outlineSprite : outlineSprites) {
            outlineSprite.draw(batch);
        }

        return false;
    }

    public void render(PolygonSpriteBatch batch) {
        if (!isVisible) {
            return;
        }

        if (fillSprite != null) {
            fillSprite.draw(batch);
        }
    }

    public String getName() {
        return name;
    }

    public Array<String> getTags() {
        return tags;
    }

    public Body getPhysicsBody() {
        return physicsBody;
    }

    public float getCenterX() {
        return physicsBody.getWorldCenter().x;
    }

    public float getCenterY() {
        return physicsBody.getWorldCenter().y;
    }

    public float getLeft() {
        return getCenterX() - (width / 2);
    }

    public float getRight() {
        return getCenterX() + (width / 2);
    }

    public float getTop() {
        return getCenterY() + (height / 2);
    }

    public float getBottom() {
        return getCenterY() - (height / 2);
    }

    public float getAngle() {
        return physicsBody.getAngle();
    }

    public void setAngle(float angle) {
        Vector2 pos = physicsBody.getTransform().getPosition();
        physicsBody.setTransform(pos.x, pos.y, MathUtils.degreesToRadians * angle);
    }

    public Rectangle getBounds() {
        bounds.set(getLeft(), getBottom(), width, height);
        return bounds;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean overlaps(Rectangle bounds) {
        return getBounds().overlaps(bounds);
    }

    public Array<Component> getComponents() {
        return components;
    }

    public <T extends Component> Component getComponentByType(Class<T> componentClass) {
        for (int i = 0; i < components.size; i++) {
            Component component = components.get(i);
            if (componentClass.isInstance(component)) {
                return component;
            }
        }

        return null;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size; i++) {
            Component component = components.get(i);
            if (componentClass.isInstance(component)) {
                return true;
            }
        }

        return false;
    }

    public <T extends Action> boolean hasAction(Class<T> actionClass) {
        return hasAction(actionClass, actions);
    }

    private <T extends Action> boolean hasAction(Class<T> actionClass, Array<Action> actions) {
        for (Action action : actions) {
            if (actionClass.isInstance(action)) {
                return true;
            }
            if (action instanceof SequenceAction) {
                if (hasAction(actionClass, ((SequenceAction)action).getActions())) {
                    return true;
                }
            }
            else if (action instanceof ParallelAction) {
                if (hasAction(actionClass, ((ParallelAction)action).getActions())) {
                    return true;
                }
            }
            else if (action instanceof RepeatAction) {
                Array<Action> repeatAction = new Array<>();
                repeatAction.add(((RepeatAction)action).getAction());
                if (hasAction(actionClass, repeatAction)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setDone() {
        isDone = true;
    }

    public void destroy() {
        for (Component component : components) {
            component.destroy();
        }

        if (physicsBody != null && level != null) {
            level.getPhysicsWorld().destroyBody(physicsBody);
        }

        level.removeGameObject(this);
    }

    public Properties getProperties() {
        return properties;
    }

    public Shape2D getShape() {
        return shape;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(float r, float g, float b, float a) {
        outlineColor.set(r, g, b, a);
        for (Sprite sprite : outlineSprites) {
            sprite.setColor(outlineColor);
        }
    }

    public void setOutlineColor(Color color) {
        setOutlineColor(color.r, color.g, color.b, color.a);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        width = sprite.getWidth();
        height = sprite.getHeight();
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void removeComponent(Component component) {
        components.removeValue(component, true);
    }

    public void setDisableContacts(boolean disableContacts) {
        this.disableContacts = disableContacts;
    }

    public boolean isContactDisabled() {
        return disableContacts;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean areComponentsVisible() {
        return areComponentsVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void setComponentsVisible(boolean areComponentsVisible) {
        this.areComponentsVisible = areComponentsVisible;
    }

    protected void updateOutlineSprites() {
        if (outlineSprites == null || outlineSprites.isEmpty()) {
            return;
        }

        float[] vertices = getShapeTransformedVertices();

        for (int i = 2; i < vertices.length; i += 2) {
            float x1 = vertices[i - 2];
            float x2 = vertices[i];
            float y1 = vertices[i - 1];
            float y2 = vertices[i + 1];

            Sprite sprite = outlineSprites.get(i / 2 - 1);
            float length = (float)Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            sprite.setOrigin(Level.LINE_WIDTH / 2, Level.LINE_WIDTH / 2);
            sprite.setSize(length + Level.LINE_WIDTH, Level.LINE_WIDTH);
            sprite.setRotation(MathUtils.radiansToDegrees * (float)Math.atan2(y2 - y1, x2 - x1));
            sprite.setPosition(x1 - (Level.LINE_WIDTH / 2), y1 - (Level.LINE_WIDTH / 2));
        }

        if (shape instanceof Polygon) {
            float fx = vertices[0];
            float fy = vertices[1];
            float lx = vertices[vertices.length - 2];
            float ly = vertices[vertices.length - 1];
            float length = (float)Math.sqrt(Math.pow(fx - lx, 2) + Math.pow(fy - ly, 2));
            Sprite sprite = outlineSprites.get(outlineSprites.size - 1);
            sprite.setOrigin(Level.LINE_WIDTH / 2, Level.LINE_WIDTH / 2);
            sprite.setSize(length + Level.LINE_WIDTH, Level.LINE_WIDTH);
            sprite.setRotation(MathUtils.radiansToDegrees * MathUtils.atan2(fy - ly, fx - lx));
            sprite.setPosition(lx - (Level.LINE_WIDTH / 2), ly - (Level.LINE_WIDTH / 2));
        }
    }

    protected Body createPhysicsBody(Level level, Shape2D shape, Properties properties, BodyType bodyType) {
        Body body = ShapeUtils.createPhysicsBody(level.getPhysicsWorld(), shape, bodyType);
        physicsBody = body;
        if (body != null) {
            body.setUserData(new PhysicsBodyData(this));
            body.setFixedRotation(properties.getBoolean("fixed_rotation", false));

            setDisableContacts(properties.getBoolean("is_sensor", false));

            if (!properties.isPropertyEmpty("angular_velocity")) {
                body.setFixedRotation(false);
                body.setAngularVelocity(properties.getFloat("angular_velocity"));
            }

            Filter filter = getFixtureFilter(properties);
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
        }

        return body;
    }

    protected void buildComponents(Properties properties) {
        Iterator iter = properties.getKeys();
        while (iter.hasNext()) {
            String key = (String)iter.next();
            if (key.startsWith("_")) {
                try {
                    Properties componentProperties = new Properties(properties.getString(key));
                    Component component = Component.buildComponent(key, level, this, componentProperties);
                    components.add(component);
                } catch(Exception e) {
                    Gdx.app.error(Globals.LOG_TAG, e.toString(), e);
                    Gdx.app.exit();
                }
            }
        }
    }

    protected void buildShape() {
        Vector2 vertex = new Vector2();
        Shape shape = physicsBody.getFixtureList().get(0).getShape();

        if (shape instanceof ChainShape) {
            ChainShape physicsShape = (ChainShape)shape;
            float[] vertices = new float[physicsShape.getVertexCount() * 2];
            for (int i = 0; i < physicsShape.getVertexCount(); i++) {
                physicsShape.getVertex(i, vertex);
                vertices[i * 2] = vertex.x;
                vertices[i * 2 + 1] = vertex.y;
            }

            this.shape = new Polyline(vertices);
        } else {
            PolygonShape physicsShape = (PolygonShape)shape;
            float[] vertices = new float[physicsShape.getVertexCount() * 2];
            for (int i = 0; i < physicsShape.getVertexCount(); i++) {
                physicsShape.getVertex(i, vertex);
                vertices[i * 2] = vertex.x;
                vertices[i * 2 + 1] = vertex.y;
            }

            this.shape = new Polygon(vertices);
        }
    }

    protected void buildOutlineSprites() {
        float[] vertices = getShapeVertices();
        int numVertices = (vertices.length / 2) - 1;
        if (shape instanceof Polygon) {
            numVertices += 1;
        }
        for (int i = 0; i < numVertices; i++) {
            Sprite sprite = new Sprite();
            sprite.setTexture(ColorUtils.getSolidColorDrawable(Color.WHITE).getRegion().getTexture());
            sprite.setColor(outlineColor);
            outlineSprites.add(sprite);
        }
    }

    public float[] getShapeVertices() {
        if (shape == null) {
            return null;
        }

        return shape instanceof Polygon ?
                ((Polygon) shape).getVertices() : ((Polyline) shape).getVertices();
    }

    public float[] getShapeTransformedVertices() {
        if (shape == null) {
            return null;
        }

        return shape instanceof Polygon ?
                ((Polygon) shape).getTransformedVertices() : ((Polyline) shape).getTransformedVertices();
    }

    public Array<Sprite> getOutlineSprites() {
        return outlineSprites;
    }

    public PolygonSprite getFillSprite() {
        return fillSprite;
    }

    public void addAction(Action action) {
        action.setTarget(this);
        this.actions.add(action);
    }

    public void removeAction(Action action) {
        action.setTarget(null);
        this.actions.removeValue(action, true);
    }

    public void clearActions() {
        for (Action action : actions) {
            action.setTarget(null);
        }
        this.actions.clear();
    }

    private BodyType getBodyType(Properties properties) {
        String bodyTypeName = properties.getString("body_type", "static");
        return BodyType.valueOf(StringUtils.capitalize(bodyTypeName) + "Body");
    }

    private PolygonSprite getFillSprite(Shape2D shape, Color color, float scale) {
        Vector2 position = ShapeUtils.getShapeOrigin(shape);
        float[] vertices = ShapeUtils.getShapeVertices(shape);

        position.scl(scale);
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= scale;
        }

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color);
        pix.fill();

        Texture textureSolid = new Texture(pix);
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        short[] triangles = triangulator.computeTriangles(vertices).toArray();
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid), vertices, triangles);
        PolygonSprite polygonSprite = new PolygonSprite(polyReg);

        polygonSprite.setPosition(position.x, position.y);
        polygonSprite.setOrigin(0, 0);

        return polygonSprite;
    }

    private Filter getFixtureFilter(Properties properties) {
        Short maskBits = properties.getShort("mask_bits", 16, (short)-1);
        Short categoryBits = properties.getShort("category_bits", 16, (short)0x0001);
        Short groupIndex = properties.getShort("groupIndex", 10, (short)0);
        Filter filter = new Filter();
        filter.maskBits = maskBits;
        filter.categoryBits = categoryBits;
        filter.groupIndex = groupIndex;
        return filter;
    }
}
