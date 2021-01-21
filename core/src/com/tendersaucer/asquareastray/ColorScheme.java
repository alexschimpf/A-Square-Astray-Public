package com.tendersaucer.asquareastray;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.asquareastray.utils.Tuple;

import java.util.HashMap;
import java.util.Map;

public class ColorScheme {

    public static final Map<String, ColorScheme> schemes = new HashMap<>();
    static {
        schemes.put("twilight", new ColorScheme()
            .setShaderName("fragment_purple")
            .setBackgroundGrayscale(true)
            .setBackgroundColor1(0, 0, 0, 1)
            .setMinBackgroundColor2(0.1f, 0.1f, 0.1f, 1)
            .setMaxBackgroundColor2(0.3f, 0.3f, 0.3f, 1)
            .setMinBackgroundColor3(0, 0, 0, 0.05f)
            .setMaxBackgroundColor3(1, 1, 1, 0.15f)
            .setPlayerColor(1, 1, 1, 1)
            .setDashButtonColor(1, 0, 1, 1)
            .setOtherButtonColor(1, 0, 1, 1)
            .setLineColor(1, 1, 1, 1)
            .setFillColor(0, 0, 0, 1)
            .setMenuBackgroundStartColorRange(new Color(0x80008040), new Color(0x80008050))
            .setMenuBackgroundEndColorRange(new Color(0x80008000), new Color(0x80008000))
            .setMenuTitleColor(1, 0, 1, 1)
            .setMenuBorderColor(1, 1, 1, 1)
            .setMainMenuSecondaryColor(1, 1, 1, 1)
            .setMainMenuStarColor(1, 1, 0, 1)
            .setMainMenuLevelButtonTextColor(1, 1, 1, 1)
            .setSettingsMenuBackButtonColor(1, 1, 1, 1)
            .setSettingsMenuInputBackgroundColor(1, 0, 1, 1)
            .setSettingsMenuInputTextColor(0, 0, 0, 1)
            .setSettingsMenuSliderKnobColor(1, 1, 1, 1)
            .setSettingsMenuLabelColor(1, 1, 1, 1)
            .setSettingsMenuRestorePurchaseButtonColor(1, 0, 1, 1)
            .setSuccessMenuSecondaryColor(1, 1, 1, 1)
            .setSuccessMenuStarColor(1, 1, 0, 1)
            .setModalPrimaryButtonColor(1, 0, 1, 1)
        );
        schemes.put("sandstorm", new ColorScheme()
            .setShaderName("fragment_yellow")
            .setBackgroundGrayscale(true)
            .setBackgroundColor1(0, 0, 0, 1)
            .setMinBackgroundColor2(0.1f, 0.1f, 0.1f, 1)
            .setMaxBackgroundColor2(0.3f, 0.3f, 0.3f, 1)
            .setMinBackgroundColor3(0, 0, 0, 0.05f)
            .setMaxBackgroundColor3(1, 1, 1, 0.15f)
            .setPlayerColor(1, 1, 1, 1)
            .setDashButtonColor(1, 1, 0, 1)
            .setOtherButtonColor(1, 1, 0, 1)
            .setLineColor(1, 1, 1, 1)
            .setFillColor(0, 0, 0, 1)
            .setMenuBackgroundStartColorRange(new Color(0x80800040), new Color(0x80800040))
            .setMenuBackgroundEndColorRange(new Color(0x80800000), new Color(0x80800000))
            .setMenuTitleColor(1, 1, 0, 1)
            .setMenuBorderColor(1, 1, 0, 1)
            .setMainMenuSecondaryColor(1, 1, 1, 1)
            .setMainMenuStarColor(1, 1, 0, 1)
            .setMainMenuLevelButtonTextColor(0.7f, 0.7f, 0.7f, 1)
            .setSettingsMenuBackButtonColor(1, 1, 1, 1)
            .setSettingsMenuInputBackgroundColor(1, 1, 0, 1)
            .setSettingsMenuInputTextColor(0, 0, 0, 1)
            .setSettingsMenuSliderKnobColor(1, 1, 1, 1)
            .setSettingsMenuLabelColor(1, 1, 1, 1)
            .setSettingsMenuRestorePurchaseButtonColor(1, 1, 0, 1)
            .setSuccessMenuSecondaryColor(1, 1, 1, 1)
            .setSuccessMenuStarColor(1, 1, 0, 1)
            .setModalPrimaryButtonColor(1, 1, 0, 1)
        );
        schemes.put("aquatic", new ColorScheme()
            .setShaderName("fragment_cyan")
            .setBackgroundGrayscale(true)
            .setBackgroundColor1(0, 0, 0, 1)
            .setMinBackgroundColor2(0.1f, 0.1f, 0.1f, 1)
            .setMaxBackgroundColor2(0.3f, 0.3f, 0.3f, 1)
            .setMinBackgroundColor3(0, 0, 0, 0.05f)
            .setMaxBackgroundColor3(1, 1, 1, 0.15f)
            .setPlayerColor(1, 1, 1, 1)
            .setDashButtonColor(0, 1, 1, 1)
            .setOtherButtonColor(0, 1, 1, 1)
            .setLineColor(1, 1, 1, 1)
            .setFillColor(0, 0, 0, 1)
            .setMenuBackgroundStartColorRange(new Color(0x00808040), new Color(0x00808040))
            .setMenuBackgroundEndColorRange(new Color(0x00808000), new Color(0x00808000))
            .setMenuTitleColor(0, 1, 1, 1)
            .setMenuBorderColor(0, 1, 1, 1)
            .setMainMenuSecondaryColor(1, 1, 1, 1)
            .setMainMenuStarColor(1, 1, 0, 1)
            .setMainMenuLevelButtonTextColor(1, 1, 1, 1)
            .setSettingsMenuBackButtonColor(1, 1, 1, 1)
            .setSettingsMenuInputBackgroundColor(0, 1, 1, 1)
            .setSettingsMenuInputTextColor(0, 0, 0, 1)
            .setSettingsMenuSliderKnobColor(1, 1, 1, 1)
            .setSettingsMenuLabelColor(1, 1, 1, 1)
            .setSettingsMenuRestorePurchaseButtonColor(0, 1, 1, 1)
            .setSuccessMenuSecondaryColor(1, 1, 1, 1)
            .setSuccessMenuStarColor(1, 1, 0, 1)
            .setModalPrimaryButtonColor(0, 1, 1, 1)
        );
        schemes.put("grayscale", new ColorScheme()
            .setShaderName("fragment_grayscale")
            .setBackgroundGrayscale(true)
            .setBackgroundColor1(0, 0, 0, 1)
            .setMinBackgroundColor2(0.1f, 0.1f, 0.1f, 1)
            .setMaxBackgroundColor2(0.3f, 0.3f, 0.3f, 1)
            .setMinBackgroundColor3(0, 0, 0, 0.05f)
            .setMaxBackgroundColor3(1, 1, 1, 0.15f)
            .setPlayerColor(1, 1, 1, 1)
            .setDashButtonColor(1, 1, 1, 1)
            .setOtherButtonColor(1, 1, 1, 1)
            .setLineColor(1, 1, 1, 1)
            .setFillColor(0, 0, 0, 1)
            .setMenuBackgroundStartColorRange(new Color(0x80808040), new Color(0x80808040))
            .setMenuBackgroundEndColorRange(new Color(0x80808000), new Color(0x80808000))
            .setMenuTitleColor(1, 1, 1, 1)
            .setMenuBorderColor(1, 1, 1, 1)
            .setMainMenuSecondaryColor(1, 1, 1, 1)
            .setMainMenuStarColor(1, 1, 0, 1)
            .setMainMenuLevelButtonTextColor(1, 1, 1, 1)
            .setSettingsMenuBackButtonColor(1, 1, 1, 1)
            .setSettingsMenuInputBackgroundColor(1, 1, 1, 1)
            .setSettingsMenuInputTextColor(0, 0, 0, 1)
            .setSettingsMenuSliderKnobColor(0, 0, 0, 1)
            .setSettingsMenuLabelColor(1, 1, 1, 1)
            .setSettingsMenuRestorePurchaseButtonColor(1, 1, 1, 1)
            .setSuccessMenuSecondaryColor(1, 1, 1, 1)
            .setSuccessMenuStarColor(1, 1, 0, 1)
            .setModalPrimaryButtonColor(1, 1, 1, 1)
        );
    }

    // TODO: Missing a lot of colors
    public boolean isBackgroundGrayscale;
    public String shaderName;
    public final Color dashButtonColor;
    public final Color otherButtonColor;
    public final Color playerColor;
    public final Color backgroundColor1;
    public final Color minBackgroundColor2;
    public final Color maxBackgroundColor2;
    public final Color minBackgroundColor3;
    public final Color maxBackgroundColor3;
    public final Color lineColor;
    public final Color fillColor;
    public final Color menuTitleColor;
    public final Color menuBorderColor;
    public final Tuple<Color, Color> menuBackgroundStartColorRange;
    public final Tuple<Color, Color> menuBackgroundEndColorRange;
    public final Color mainMenuSecondaryColor;
    public final Color mainMenuStarColor;
    public final Color mainMenuLevelButtonTextColor;
    public final Color successMenuStarColor;
    public final Color successMenuSecondaryColor;
    public final Color settingsMenuLabelColor;
    public final Color settingsMenuInputBackgroundColor;
    public final Color settingsMenuInputTextColor;
    public final Color settingsMenuSliderKnobColor;
    public final Color settingsMenuRestorePurchaseButtonColor;
    public final Color settingsMenuBackButtonColor;
    public final Color modalPrimaryButtonColor;

    public ColorScheme() {
        isBackgroundGrayscale = false;
        dashButtonColor = new Color();
        otherButtonColor = new Color();
        playerColor = new Color();
        backgroundColor1 = new Color();
        minBackgroundColor2 = new Color();
        maxBackgroundColor2 = new Color();
        minBackgroundColor3 = new Color();
        maxBackgroundColor3 = new Color();
        lineColor = new Color();
        fillColor = new Color();

        menuTitleColor = new Color();
        menuBorderColor = new Color();
        menuBackgroundStartColorRange = new Tuple<>();
        menuBackgroundEndColorRange = new Tuple<>();
        mainMenuSecondaryColor = new Color();
        mainMenuStarColor = new Color();
        mainMenuLevelButtonTextColor = new Color();
        successMenuStarColor = new Color();
        successMenuSecondaryColor = new Color();
        settingsMenuLabelColor = new Color();
        settingsMenuInputBackgroundColor = new Color();
        settingsMenuInputTextColor = new Color();
        settingsMenuSliderKnobColor = new Color();
        settingsMenuRestorePurchaseButtonColor = new Color();
        settingsMenuBackButtonColor = new Color();
        modalPrimaryButtonColor = new Color();
    }

    public static ColorScheme getColorScheme() {
        return schemes.get(getColorSchemeName());
    }

    public static String getColorSchemeName() {
        return Persistence.getInstance().getString("color_scheme", "twilight");
    }

    public ColorScheme setShaderName(String name) {
        shaderName = name;
        return this;
    }

    public ColorScheme setBackgroundGrayscale(boolean isBackgroundGrayscale) {
        this.isBackgroundGrayscale = isBackgroundGrayscale;
        return this;
    }

    public ColorScheme setDashButtonColor(float r, float g, float b, float a) {
        dashButtonColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setOtherButtonColor(float r, float g, float b, float a) {
        otherButtonColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setPlayerColor(float r, float g, float b, float a) {
        playerColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setBackgroundColor1(float r, float g, float b, float a) {
        backgroundColor1.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMinBackgroundColor2(float r, float g, float b, float a) {
        minBackgroundColor2.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMaxBackgroundColor2(float r, float g, float b, float a) {
        maxBackgroundColor2.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMinBackgroundColor3(float r, float g, float b, float a) {
        minBackgroundColor3.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMaxBackgroundColor3(float r, float g, float b, float a) {
        maxBackgroundColor3.set(r, g, b, a);
        return this;
    }

    public ColorScheme setLineColor(float r, float g, float b, float a) {
        lineColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setFillColor(float r, float g, float b, float a) {
        fillColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMenuTitleColor(float r, float g, float b, float a) {
        menuTitleColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMenuBorderColor(float r, float g, float b, float a) {
        menuBorderColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMenuBackgroundStartColorRange(Color minColor, Color maxColor) {
        menuBackgroundStartColorRange.set(minColor, maxColor);
        return this;
    }

    public ColorScheme setMenuBackgroundEndColorRange(Color minColor, Color maxColor) {
        menuBackgroundEndColorRange.set(minColor, maxColor);
        return this;
    }

    public ColorScheme setMainMenuSecondaryColor(float r, float g, float b, float a) {
        mainMenuSecondaryColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMainMenuStarColor(float r, float g, float b, float a) {
        mainMenuStarColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setMainMenuLevelButtonTextColor(float r, float g, float b, float a) {
        mainMenuLevelButtonTextColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSuccessMenuStarColor(float r, float g, float b, float a) {
        successMenuStarColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSuccessMenuSecondaryColor(float r, float g, float b, float a) {
        successMenuSecondaryColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuLabelColor(float r, float g, float b, float a) {
        settingsMenuLabelColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuInputBackgroundColor(float r, float g, float b, float a) {
        settingsMenuInputBackgroundColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuInputTextColor(float r, float g, float b, float a) {
        settingsMenuInputTextColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuSliderKnobColor(float r, float g, float b, float a) {
        settingsMenuSliderKnobColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuRestorePurchaseButtonColor(float r, float g, float b, float a) {
        settingsMenuRestorePurchaseButtonColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setSettingsMenuBackButtonColor(float r, float g, float b, float a) {
        settingsMenuBackButtonColor.set(r, g, b, a);
        return this;
    }

    public ColorScheme setModalPrimaryButtonColor(float r, float g, float b, float a) {
        modalPrimaryButtonColor.set(r, g, b, a);
        return this;
    }
}
