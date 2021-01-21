package com.tendersaucer.asquareastray;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.tendersaucer.asquareastray.utils.FontGenerator;

public class Fonts {

    private static final float screenWidth = Gdx.graphics.getWidth();
    private static final float screenHeight = Gdx.graphics.getHeight();
    private static final FontGenerator fontGenerator = new FontGenerator();

    // Main menu screen
    public static final BitmapFont MAIN_MENU_LEVEL_BUTTON =
            fontGenerator.createFont("MAIN_MENU_LEVEL_BUTTON", screenWidth * 0.04f);
    public static final BitmapFont MAIN_MENU_NUM_STARS =
            fontGenerator.createFont("MAIN_MENU_NUM_STARS", screenHeight * 0.08f);
    public static final BitmapFont MAIN_MENU_REMOVE_ADS =
            fontGenerator.createFont("MAIN_MENU_REMOVE_ADS", screenWidth * 0.02f);
    public static final BitmapFont MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION =
            fontGenerator.createFont("MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION", screenHeight * 0.06f);

    // Settings screen
    public static final BitmapFont SETTINGS_THEMES_SELECT =
            fontGenerator.createFont("SETTINGS_THEMES_SELECT", screenHeight * 0.03f);
    public static final BitmapFont SETTINGS_DIFFICULTY_SELECT = SETTINGS_THEMES_SELECT;
    public static final BitmapFont SETTINGS_CHECKBOX_LABEL = SETTINGS_THEMES_SELECT;
    public static final BitmapFont SETTINGS_SLIDER_LABEL = SETTINGS_THEMES_SELECT;
    public static final BitmapFont SETTINGS_RESTORE_PURCHASE = SETTINGS_THEMES_SELECT;

    // HUD
    public static final BitmapFont HUD_CLOCK_TIME =
            fontGenerator.createFont("HUD_CLOCK_TIME", screenHeight * 0.08f);
    public static final BitmapFont HUD_LEVEL_ID = HUD_CLOCK_TIME;

    // Success Screen
    public static final BitmapFont SUCCESS_TITLE =
            fontGenerator.createFont("SUCCESS_TITLE", screenWidth * 0.035f);
    public static final BitmapFont SUCCESS_NEXT_BUTTON =
            fontGenerator.createFont("SUCCESS_NEXT_BUTTON", screenWidth * 0.025f);
    public static final BitmapFont SUCCESS_ELAPSED_TIME = SUCCESS_NEXT_BUTTON;

    // Tutorial
    public static final BitmapFont TUTORIAL =
            fontGenerator.createFont("TUTORIAL", screenHeight * 0.04f);

    public static void dispose() {
        MAIN_MENU_LEVEL_BUTTON.dispose();
        MAIN_MENU_NUM_STARS.dispose();
        MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION.dispose();
        SETTINGS_THEMES_SELECT.dispose();
        SETTINGS_DIFFICULTY_SELECT.dispose();
        SETTINGS_CHECKBOX_LABEL.dispose();
        SETTINGS_SLIDER_LABEL.dispose();
        SETTINGS_RESTORE_PURCHASE.dispose();
        HUD_CLOCK_TIME.dispose();
        HUD_LEVEL_ID.dispose();
        SUCCESS_TITLE.dispose();
        SUCCESS_NEXT_BUTTON.dispose();
        SUCCESS_ELAPSED_TIME.dispose();
        TUTORIAL.dispose();
    }
}
