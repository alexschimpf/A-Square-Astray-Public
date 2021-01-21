package com.tendersaucer.asquareastray.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class RandomUtils {

    public static Color randomColor(boolean isOpaque) {
        return new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(),
                isOpaque ? 1 : MathUtils.random());
    }

    public static <T> T pick(T a, T b) {
        return MathUtils.random() < 0.5f ? a : b;
    }

    public static <T> T pickFrom(T... t) {
        return t[MathUtils.random(0, t.length - 1)];
    }

    public static <T> T pickFrom(List<T> t) {
        return t.get(MathUtils.random(0, t.size() - 1));
    }

    public static <T> T pickFrom(Array<T> t) {
        return t.get(MathUtils.random(0, t.size - 1));
    }

    /**
     * Returns random number from range
     *
     * @param range - x = inclusive, y = EXCLUSIVE
     * @return
     */
    public static float pickFromRange(Vector2 range) {
        return MathUtils.random(range.x, range.y);
    }

    public static float pickFromRange(float min, float max) {
        return MathUtils.random(min, max);
    }

    public static float pickFromSplitRange(Vector2 range, float split) {
        return pickFromSplitRange(range.x, range.y, split);
    }

    public static float pickFromSplitRange(float a, float b, float split) {
        if (split == 0) {
            return MathUtils.random(a, b);
        }

        split = Math.abs(split);
        return pick(MathUtils.random(a, -split), MathUtils.random(split, b));
    }

    public static <T> void shuffle(T[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int swapIndex = MathUtils.random(0, i);
            T temp = array[i];
            array[i] = array[swapIndex];
            array[swapIndex] = temp;
        }
    }

    public static <T> void shuffle(Array<T> array) {
        for (int i = array.size - 1; i > 0; i--) {
            int swapIndex = MathUtils.random(0, i);
            T temp = array.get(i);
            array.set(i, array.get(swapIndex));
            array.set(swapIndex, temp);
        }
    }
}
