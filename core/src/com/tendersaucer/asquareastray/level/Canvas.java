package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.asquareastray.object.GameObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Canvas {

    public static final int NUM_LAYERS = 10;

    private final Map<ICanvasItem, Integer> objectLayerMap;
    private final Array<LinkedHashMap<ICanvasItem, Boolean>> layers; // 0 = Background, 9 = Foreground

    public Canvas() {
        objectLayerMap = new ConcurrentHashMap<>();
        layers = new Array<>();
        for (int i = 0; i < NUM_LAYERS; i++) {
            layers.add(new LinkedHashMap<ICanvasItem, Boolean>());
        }
    }

    public void render(SpriteBatch spriteBatch) {
        render(spriteBatch, null);
    }

    public void render(SpriteBatch spriteBatch, PolygonSpriteBatch polygonSpriteBatch) {
        for (int i = 0; i < layers.size; i++) {
            LinkedHashMap<ICanvasItem, Boolean> layer = layers.get(i);
            Iterator<ICanvasItem> iter = layer.keySet().iterator();
            while (iter.hasNext()) {
                ICanvasItem object = iter.next();

                // TODO: Clean up this hack!
                if (object instanceof GameObject && ((GameObject)object).getFillSprite() != null) {
                    spriteBatch.end();

                    polygonSpriteBatch.begin();
                    ((GameObject)object).render(polygonSpriteBatch);
                    polygonSpriteBatch.end();

                    spriteBatch.begin();
                }

                if (object.render(spriteBatch)) {
                    // TODO: Will this work?
                    iter.remove();
                }
            }
        }
    }

    public void clearLayers() {
        for (int i = 0; i < NUM_LAYERS; i++) {
            layers.get(i).clear();
        }

        objectLayerMap.clear();
    }

    public void clearLayer(int layer) {
        layers.get(layer).clear();

        Iterator<ICanvasItem> objectLayerMapIter = objectLayerMap.keySet().iterator();
        while (objectLayerMapIter.hasNext()) {
            ICanvasItem object = objectLayerMapIter.next();
            int objectLayer = objectLayerMap.get(object);
            if (objectLayer == layer) {
                objectLayerMapIter.remove();
            }
        }
    }

    public void addToLayer(ICanvasItem object, int layer) {
        layers.get(layer).put(object, true);
        objectLayerMap.put(object, layer);
    }

    public void remove(ICanvasItem object) {
        Integer layer = objectLayerMap.get(object);
        if (layer != null) {
            layers.get(layer).remove(object);
            objectLayerMap.remove(object);
        }
    }
}
