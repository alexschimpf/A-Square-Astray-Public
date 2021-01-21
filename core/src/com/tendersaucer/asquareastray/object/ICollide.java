package com.tendersaucer.asquareastray.object;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public interface ICollide {

    void onBeginContact(Contact contact, GameObject gameObject, boolean isObjectA);

    void onEndContact(Contact contact, GameObject gameObject, boolean isObjectA);

    void onPreSolve(Contact contact, Manifold oldManifold, GameObject gameObject, boolean isObjectA);

    void onPostSolve(Contact contact, ContactImpulse impulse, GameObject gameObject, boolean isObjectA);
}
