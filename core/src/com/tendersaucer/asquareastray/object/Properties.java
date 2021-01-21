package com.tendersaucer.asquareastray.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.asquareastray.utils.StringUtils;


public class Properties extends MapProperties {

    public Properties() {
        super();
    }

    public Properties(MapProperties properties) {
        super();

        putAll(properties);
    }

    public Properties(String properties) {
        super();

        if (!StringUtils.isEmpty(properties)) {
            String[] keyValues = properties.split("\n");
            for (String keyValue : keyValues) {
                String[] splitKeyValue = keyValue.split(":");
                String key = splitKeyValue[0].trim();
                String value = splitKeyValue[1].trim();
                put(key, value);
            }
        }
    }
    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        Object obj = get(key);
        return obj != null ? obj.toString() : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        if (isPropertyEmpty(key)) {
            return defaultValue;
        } else {
            return Boolean.parseBoolean(getString(key));
        }
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        if (isPropertyEmpty(key)) {
            return defaultValue;
        } else {
            return Integer.parseInt(getString(key));
        }
    }

    public Short getShort(String key) {
        return getShort(key, 10, null);
    }

    public Short getShort(String key, int radix) {
        return getShort(key, radix, null);
    }

    public Short getShort(String key, int radix, Short defaultValue) {
        if (isPropertyEmpty(key)) {
            return defaultValue;
        } else {
            return Short.parseShort(getString(key), radix);
        }
    }

    public Float getFloat(String key) {
        return getFloat(key, null);
    }

    public Float getFloat(String key, Float defaultValue) {
        if (isPropertyEmpty(key)) {
            return defaultValue;
        } else {
            return Float.parseFloat(getString(key));
        }
    }

    public Vector2 getVector2(String key) {
        if (getString(key).equals("")) {
            return new Vector2();
        }

        Float[] vals = getFloatArray(key, ",");
        return new Vector2(vals[0], vals[1]);
    }

    public Color getColor(String key) {
        if (isPropertyEmpty(key)) {
            return null;
        }

        String[] colorComponents = getStringArray(key, ",");
        float r = Float.parseFloat(colorComponents[0]);
        float g = Float.parseFloat(colorComponents[1]);
        float b = Float.parseFloat(colorComponents[2]);
        float a = Float.parseFloat(colorComponents[3]);
        return new Color(r, g, b, a);
    }

    public boolean[] getBooleanArray(String key, String delim) {
        String full = getString(key);
        if (full.equals("")) {
            return new boolean[0];
        }

        String[] strArr = full.split(delim);
        boolean[] booleanArr = new boolean[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            booleanArr[i++] = Boolean.parseBoolean(elem);
        }

        return booleanArr;
    }

    public Integer[] getIntArray(String key, String delim) {
        String full = getString(key);
        if (full.equals("")) {
            return new Integer[0];
        }

        String[] strArr = full.split(delim);
        Integer[] intArr = new Integer[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            if (StringUtils.isEmpty(elem)) {
                intArr[i++] = null;
            } else {
                intArr[i++] = Integer.parseInt(elem);
            }
        }

        return intArr;
    }

    public Float[] getFloatArray(String key, String delim) {
        String full = getString(key);
        if (full.equals("")) {
            return new Float[0];
        }

        String[] strArr = full.split(delim);
        Float[] floatArr = new Float[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            if (StringUtils.isEmpty(elem)) {
                floatArr[i++] = null;
            } else {
                floatArr[i++] = Float.parseFloat(elem);
            }
        }

        return floatArr;
    }

    public String[] getStringArray(String key, String delim) {
        String full = getString(key);
        if (full.equals("")) {
            return new String[0];
        }

        return full.split(delim);
    }

    public Color[] getColorArray(String key) {
        String[] colorStrings = getStringArray(key, " ");
        Color[] colors = new Color[colorStrings.length];

        int i = 0;
        for (String colorString : colorStrings) {
            String[] colorComponents = colorString.split(",");
            float r = Float.parseFloat(colorComponents[0]);
            float g = Float.parseFloat(colorComponents[1]);
            float b = Float.parseFloat(colorComponents[2]);
            float a = Float.parseFloat(colorComponents[3]);
            colors[i++] = new Color(r, g, b, a);
        }

        return colors;
    }

    public boolean hasProperties() {
        return getKeys().hasNext();
    }

    public boolean propertyExists(String key) {
        return get(key) != null;
    }

    public boolean isPropertyEmpty(String key) {
        return !propertyExists(key) || getString(key).equals("");
    }
}
