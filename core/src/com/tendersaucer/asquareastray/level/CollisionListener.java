package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.utils.PhysicsBodyData;

public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        GameObject gameObjectA = getGameObject(contact.getFixtureA());
        GameObject gameObjectB = getGameObject(contact.getFixtureB());
        if (gameObjectA != null) {
            gameObjectA.onBeginContact(contact, gameObjectB, true);
        }
        if (gameObjectB != null) {
            gameObjectB.onBeginContact(contact, gameObjectA, false);
        }
    }

    @Override
    public void endContact(Contact contact) {
        GameObject gameObjectA = getGameObject(contact.getFixtureA());
        GameObject gameObjectB = getGameObject(contact.getFixtureB());
        if (gameObjectA != null) {
            gameObjectA.onEndContact(contact, gameObjectB, true);
        }
        if (gameObjectB != null) {
            gameObjectB.onEndContact(contact, gameObjectA, false);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        GameObject gameObjectA = getGameObject(contact.getFixtureA());
        GameObject gameObjectB = getGameObject(contact.getFixtureB());
        if (gameObjectA != null) {
            gameObjectA.onPreSolve(contact, oldManifold, gameObjectB, true);
        }
        if (gameObjectB != null) {
            gameObjectB.onPreSolve(contact, oldManifold, gameObjectA, false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        GameObject gameObjectA = getGameObject(contact.getFixtureA());
        GameObject gameObjectB = getGameObject(contact.getFixtureB());
        if (gameObjectA != null) {
            gameObjectA.onPostSolve(contact, impulse, gameObjectB, true);
        }
        if (gameObjectB != null) {
            gameObjectB.onPostSolve(contact, impulse, gameObjectA, false);
        }
    }

    private GameObject getGameObject(Fixture fixture) {
        Body physicsBody = fixture.getBody();
        PhysicsBodyData physicsBodyData = (PhysicsBodyData)physicsBody.getUserData();
        if (physicsBodyData == null) {
            return null;
        }

        return physicsBodyData.gameObject;
    }
}
