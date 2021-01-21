package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;

public class Shaders {

    private static final Map<String, String> SHADERS_BY_NAME = new HashMap<>();
    static {
        SHADERS_BY_NAME.put("vertex", Gdx.files.internal("shaders/vertex.glsl").readString());
        SHADERS_BY_NAME.put("fragment_grayscale", Gdx.files.internal("shaders/fragment_grayscale.glsl").readString());
        SHADERS_BY_NAME.put("fragment_yellow", Gdx.files.internal("shaders/fragment_yellow.glsl").readString());
        SHADERS_BY_NAME.put("fragment_purple", Gdx.files.internal("shaders/fragment_purple.glsl").readString());
        SHADERS_BY_NAME.put("fragment_cyan", Gdx.files.internal("shaders/fragment_cyan.glsl").readString());
    }

    public static String getShader(String name) {
        return SHADERS_BY_NAME.get(name);
    }
}
