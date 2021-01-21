package com.tendersaucer.asquareastray.desktop;


public class TexturePacker {

    private static final String TEXTURE_PACK_NAME = "textures";
    private static final String TEXTURES_DIR =  "/Users/alexschimpf/Projects/A Square Astray/android/assets/textures";
    private static final String DESTINATION_DIR = "/Users/alexschimpf/Projects/A Square Astray/android/assets";


    private TexturePacker() {
    }

    public static void main(String[] args) {
        com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings settings =
                new com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings();
        settings.duplicatePadding = true;
        com.badlogic.gdx.tools.texturepacker.TexturePacker.process(settings, TEXTURES_DIR,
                DESTINATION_DIR, TEXTURE_PACK_NAME);
    }
}