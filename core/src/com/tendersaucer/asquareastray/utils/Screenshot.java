package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.UUID;

public class Screenshot {

    public static void takeScreenshot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), true);
        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external("screenshot_" + UUID.randomUUID().toString() + ".png"), pixmap);
        pixmap.dispose();
    }
}
