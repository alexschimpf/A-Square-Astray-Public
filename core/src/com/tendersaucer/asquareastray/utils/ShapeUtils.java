package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.tendersaucer.asquareastray.level.Level;

public class ShapeUtils {

    public static Shape2D getShapeFromMapObject(MapObject mapObject) {
        if (mapObject instanceof RectangleMapObject) {
            return ((RectangleMapObject) mapObject).getRectangle();
        } else if (mapObject instanceof CircleMapObject) {
            return ((CircleMapObject) mapObject).getCircle();
        } else if (mapObject instanceof EllipseMapObject) {
            return ((EllipseMapObject) mapObject).getEllipse();
        } else if (mapObject instanceof PolygonMapObject) {
            return ((PolygonMapObject) mapObject).getPolygon();
        } else if (mapObject instanceof PolylineMapObject) {
            return ((PolylineMapObject) mapObject).getPolyline();
        }

        return null;
    }

    public static float[] getShapeVertices(Shape2D shape) {
        // Note: This only handles rectangles and polygons
        Polygon polygon = ShapeUtils.getPolygonFromShape(shape);
        Vector2 origin = ShapeUtils.getShapeOrigin(shape);
        float[] vertices = polygon.getTransformedVertices();
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= ConversionUtils.getMetersPerPixel();
            vertices[i] -= i % 2 == 0 ? origin.x : origin.y;
        }

        return vertices;
    }

    public static Vector2 getShapeOrigin(Shape2D shape) {
        // Note: This only handles rectangles and polygons
        Vector2 origin = new Vector2();
        Polygon polygon = ShapeUtils.getPolygonFromShape(shape);
        float[] vertices = polygon.getTransformedVertices();
        for (int i = 0; i < vertices.length; i += 2) {
            origin.x += vertices[i] * ConversionUtils.getMetersPerPixel();
            origin.y += vertices[i + 1] * ConversionUtils.getMetersPerPixel();
        }
        origin.x /= (vertices.length / 2.0f);
        origin.y /= (vertices.length / 2.0f);

        return origin;
    }

    public static Body createPhysicsBody(World physicsWorld, Shape2D shape, BodyType bodyType) {
        if (shape instanceof Rectangle) {
            return ShapeUtils.createRectangleBody(physicsWorld, (Rectangle)shape, bodyType);
        } else if (shape instanceof Circle) {
            return ShapeUtils.createCircleBody(physicsWorld, (Circle)shape, bodyType);
        } else if (shape instanceof Ellipse) {
            return ShapeUtils.createEllipseBody(physicsWorld, (Ellipse)shape, bodyType);
        } else if (shape instanceof Polyline) {
            return ShapeUtils.createPolylineBody(physicsWorld, (Polyline)shape, bodyType);
        } else if (shape instanceof Polygon) {
            return ShapeUtils.createPolygonBody(physicsWorld, (Polygon)shape, bodyType);
        }

        return null;
    }

    private static Body createRectangleBody(World physicsWorld, Rectangle rectangle, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        float width = rectangle.getWidth();
        float height = rectangle.getHeight();
        bodyDef.position.x = rectangle.getX() + (width / 2);
        bodyDef.position.y = rectangle.getY() + (height / 2);
        bodyDef.position.scl(ConversionUtils.getMetersPerPixel());
        bodyDef.type = bodyType;

        Body body = physicsWorld.createBody(bodyDef);
        FixtureDef fixtureDef = ShapeUtils.getFixtureDef(rectangle);
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        return body;
    }

    private static Body createCircleBody(World physicsWorld, Circle circle, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = circle.x;
        bodyDef.position.y = circle.y;
        bodyDef.position.scl(ConversionUtils.getMetersPerPixel());
        bodyDef.type = bodyType;

        Body body = physicsWorld.createBody(bodyDef);
        FixtureDef fixtureDef = ShapeUtils.getFixtureDef(circle);
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        return body;
    }

    private static Body createEllipseBody(World physicsWorld, Ellipse ellipse, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.x = ellipse.x + (ellipse.width / 2);
        bodyDef.position.y = ellipse.y + (ellipse.height / 2);
        bodyDef.position.scl(ConversionUtils.getMetersPerPixel());
        bodyDef.type = bodyType;

        Body body = physicsWorld.createBody(bodyDef);
        FixtureDef fixtureDef = ShapeUtils.getFixtureDef(ellipse);
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        return body;
    }

    private static Body createPolygonBody(World physicsWorld, Polygon polygon, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;

        Vector2 origin = ShapeUtils.getShapeOrigin(polygon);
        bodyDef.position.set(origin);

        Body body = physicsWorld.createBody(bodyDef);
        FixtureDef fixtureDef = ShapeUtils.getFixtureDef(polygon);
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        return body;
    }

    private static Body createPolylineBody(World physicsWorld, Polyline polyline, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;

        Vector2 origin = ShapeUtils.getPolylineOrigin(polyline);
        bodyDef.position.set(origin);

        Body body = physicsWorld.createBody(bodyDef);
        FixtureDef fixtureDef = ShapeUtils.getFixtureDef(polyline);
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        return body;
    }

    private static FixtureDef getFixtureDef(Shape2D shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1;
        fixtureDef.friction = 0;
        fixtureDef.restitution = Level.DEFAULT_RESTITUTION;

        if (shape instanceof Rectangle) {
            fixtureDef.shape  = ShapeUtils.getRectangleFixtureShape((Rectangle)shape);
        } else if (shape instanceof Circle) {
            fixtureDef.shape  = ShapeUtils.getCircleFixtureShape((Circle)shape);
        } else if (shape instanceof Ellipse) {
            fixtureDef.shape  = ShapeUtils.getEllipseFixtureShape((Ellipse)shape);
        } else if (shape instanceof Polygon) {
            fixtureDef.shape  = ShapeUtils.getPolygonFixtureShape((Polygon)shape);
        } else if (shape instanceof Polyline) {
            fixtureDef.shape  = ShapeUtils.getPolylineFixtureShape((Polyline)shape);
        }

        return fixtureDef;
    }

    private static Shape getRectangleFixtureShape(Rectangle rectangle) {
        PolygonShape shape = new PolygonShape();
        float scale = ConversionUtils.getMetersPerPixel();
        shape.setAsBox((rectangle.width / 2) * scale, (rectangle.height / 2) * scale);
        return shape;
    }

    private static Shape getCircleFixtureShape(Circle circle) {
        CircleShape shape = new CircleShape();
        shape.setRadius(circle.radius * ConversionUtils.getMetersPerPixel());
        return shape;
    }

    // Just assume the ellipse is a circle for now.
    private static Shape getEllipseFixtureShape(Ellipse ellipse) {
        CircleShape shape = new CircleShape();
        shape.setRadius(ellipse.width / 2 * ConversionUtils.getMetersPerPixel());
        return shape;
    }

    private static Shape getPolygonFixtureShape(Polygon polygon) {
        float[] vertices = ShapeUtils.getShapeVertices(polygon);
        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return shape;
    }

    private static Shape getPolylineFixtureShape(Polyline polyline) {
        Vector2 origin = ShapeUtils.getPolylineOrigin(polyline);
        float[] vertices = polyline.getTransformedVertices();
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= ConversionUtils.getMetersPerPixel();
            vertices[i] -= i % 2 == 0 ? origin.x : origin.y;
        }

        ChainShape shape = new ChainShape();
        shape.createChain(vertices);

        return shape;
    }

    private static Vector2 getPolylineOrigin(Polyline polyline) {
        Vector2 origin = new Vector2();
        float[] vertices = polyline.getTransformedVertices();
        for (int i = 0; i < vertices.length; i += 2) {
            origin.x += vertices[i] * ConversionUtils.getMetersPerPixel();
            origin.y += vertices[i + 1] * ConversionUtils.getMetersPerPixel();
        }
        origin.x /= (vertices.length / 2.0f);
        origin.y /= (vertices.length / 2.0f);

        return origin;
    }

    private static Polygon getPolygonFromShape(Shape2D shape) {
        // Note: This only handles rectangles and polygons
        Polygon polygon = null;
        if (shape instanceof Polygon) {
            Polygon actualPolygon = (Polygon)shape;
            polygon = new Polygon();
            polygon.setVertices(actualPolygon.getVertices().clone());
            polygon.setOrigin(actualPolygon.getOriginX(), actualPolygon.getOriginY());
            polygon.setScale(actualPolygon.getScaleX(), actualPolygon.getScaleY());
            polygon.setRotation(actualPolygon.getRotation());
            polygon.setPosition(actualPolygon.getX(), actualPolygon.getY());
        } else if (shape instanceof Rectangle) {
            polygon = new Polygon();
            Rectangle rectangle = (Rectangle)shape;

            Vector2 center = new Vector2();
            rectangle.getCenter(center);
            polygon.setPosition(center.x, center.y);
            polygon.setOrigin(0, 0);

            float width = rectangle.getWidth();
            float height = rectangle.getHeight();

            float[] vertices = new float[8];
            vertices[0] = -width / 2;
            vertices[1] = -height / 2;
            vertices[2] = width / 2;
            vertices[3] = -height / 2;
            vertices[4] = width / 2;
            vertices[5] = height / 2;
            vertices[6] = -width / 2;
            vertices[7] = height / 2;

            polygon.setVertices(vertices);
        }

        return polygon;
    }
}
