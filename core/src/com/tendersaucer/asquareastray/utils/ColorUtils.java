package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class ColorUtils {

    private ColorUtils() {
    }

    public static double dist(Color x, Color y) {
        return Math.pow(x.r - y.r, 2) + Math.pow(x.g - y.g, 2) + Math.pow(x.b - y.b, 2);
    }

    public static void shade(Color color, float amount) {
        float a = color.a;
        color.mul(amount).clamp();
        color.a = a;
    }

    public static Color getShade(Color color, float amount) {
        Color copy = new Color(color);
        shade(copy, amount);
        return copy;
    }

    public static Color getColorFromHSL(float h, float s, float l, float a) {
        float r = l, g = l, b = l;
        if(s != 0) {
            float q = (l < 0.5f) ? (l * (1.0f + s)) : (l + s - l * s);
            float p = 2.0f * l - q;
            r = ColorUtils.convertHueToRGB(p, q, h + 1.0f / 3.0f);
            g = ColorUtils.convertHueToRGB(p, q, h);
            b = ColorUtils.convertHueToRGB(p, q, h - 1.0f / 3.0f);
        }

        return new Color(r, g, b, a);
    }

    public static TextureRegionDrawable getSolidColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
    }

    public static TextureRegionDrawable getSolidColorDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
    }

    private static float convertHueToRGB(float p, float q, float t) {
        if(t < 0.0f) t += 1.0f;
        if(t > 1.0f) t -= 1.0f;
        if(t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if(t < 1.0f / 2.0f) return q;
        if(t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }
}
