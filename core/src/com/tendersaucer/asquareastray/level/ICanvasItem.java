package com.tendersaucer.asquareastray.level;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ICanvasItem {

    boolean render(SpriteBatch batch);

    void setLayer(int layer);

    int getLayer();
}
