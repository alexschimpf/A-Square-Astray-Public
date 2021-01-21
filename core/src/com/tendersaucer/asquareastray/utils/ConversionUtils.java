package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.tendersaucer.asquareastray.level.Level;

public class ConversionUtils {

    /**
     *  Suppose the world is 100m x 100m.
     *  Suppose the screen is 500px x 1000px.
     *  Suppose we always want to show 20 tiles per screen width.
     *  In pixels, each tile's width = 500px / 20 = 25px.
     *  In meters, each tile's width = 100m / 20 = 5m.
     *  Since each tile is a square, each pixel is 25px x 25px or 5m x 5m.
     *  Thus, there are 5m/25px = .2 meters per pixel.
     */
    public static float getMetersPerPixel() {
        float metersPerTile = ConversionUtils.getMetersPerTile();
        return metersPerTile / Level.PIXELS_PER_TILE;
    }

    public static float getPixelsPerMeter() {
        return 1 / ConversionUtils.getMetersPerPixel();
    }

    public static float getPixelsPerTile() {
        return getMetersPerTile() / getMetersPerPixel();
    }

    public static float getMetersPerTile() {
        return Level.MIN_VIEWPORT_WIDTH / (float) Level.TILES_PER_SCREEN_WIDTH;
    }

    public static float getMetersPerScreenWidth() {
        return Gdx.graphics.getWidth() * ConversionUtils.getMetersPerPixel();
    }

    public static float getMetersPerScreenHeight() {
        return Gdx.graphics.getHeight() * ConversionUtils.getMetersPerPixel();
    }

    public static Color toColor(JsonValue jsonVal) {
        float[] rgb = jsonVal.asFloatArray();
        return new Color(rgb[0], rgb[1], rgb[2], rgb[3]);
    }

    public static Color toColor(String string) {
        String[] rgbaStrings = string.split(",");
        float[] rgba = new float[rgbaStrings.length];
        for (int i = 0; i < rgba.length; i++) {
            rgba[i] = Float.parseFloat(rgbaStrings[i]);
        }

        return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static Vector2 toVector2(JsonValue jsonVal) {
        float[] components = jsonVal.asFloatArray();
        return new Vector2(components[0], components[1]);
    }

    public static Vector2 toVector2(String str) {
        String[] pieces = str.split(", ");
        return new Vector2(Float.parseFloat(pieces[0]), Float.parseFloat(pieces[1]));
    }
}
