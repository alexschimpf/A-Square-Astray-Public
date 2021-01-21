package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.ColorScheme;
import com.tendersaucer.asquareastray.Fonts;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.Strings;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;
import com.tendersaucer.asquareastray.utils.ColorUtils;

public class SettingsScreen implements Screen {

    private boolean restorePurchasesButtonClicked;
    private Table middleTable;
    private final ColorScheme colorScheme;
    private final Stage stage;

    public SettingsScreen() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        colorScheme = ColorScheme.getColorScheme();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        setKeyListener();

        AssetManager.getInstance().load();

        buildUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        stage.getViewport().apply();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    private void buildUI() {
        stage.setDebugAll(false);

        buildBackground();

        buildParticleEffects();

        Table mainTable = new Table();
        mainTable.top();
        mainTable.setFillParent(true);

        buildTopOutline(mainTable);
        mainTable.row();
        buildLeftOutline(mainTable);

        mainTable.add().expand();

        middleTable = new Table();
        buildMiddleTable();
        mainTable.add(middleTable)
            .fill()
            .padBottom(Gdx.graphics.getHeight() * 0.02f);

        mainTable.add().expand();

        buildRightOutline(mainTable);
        mainTable.row();
        buildBottomOutline(mainTable);

        stage.addActor(mainTable);
    }

    private void buildBackground() {
        Sprite sprite = AssetManager.getInstance().getSprite("main_menu_background");
        Image image = new Image(sprite);
        image.setScaling(Scaling.fill);
        image.setFillParent(true);
        stage.addActor(image);
    }

    private void buildParticleEffects() {
        Vector2 position = new Vector2(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
        ParticleEffectEmitter emitter = ParticleEffects.getEmitter(
                ParticleEffects.MAIN_MENU_BACKGROUND, position, 0);
        emitter.getEffect().setStartColorRange(
                colorScheme.menuBackgroundStartColorRange.x, colorScheme.menuBackgroundStartColorRange.y);
        emitter.getEffect().setEndColorRange(
                colorScheme.menuBackgroundEndColorRange.x, colorScheme.menuBackgroundEndColorRange.y);
        stage.addActor(emitter);
        emitter.emit();
    }

    private void buildTopOutline(Table table) {
        int cornerSize = (int)(Gdx.graphics.getHeight() * 0.015f);
        Image leftCorner = new Image(ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, cornerSize, cornerSize));
        table.add(leftCorner).size(cornerSize).align(Align.bottomRight);

        Drawable topOutline = ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, 1, (int)(Gdx.graphics.getHeight() * 0.03f));
        Image topOutlineImage = new Image(topOutline);
        table.add(topOutlineImage)
                .colspan(3)
                .fillX()
                .expandX()
                .padTop(Gdx.graphics.getHeight() * 0.05f);

        Image rightCorner = new Image(ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, cornerSize, cornerSize));
        table.add(rightCorner).size(cornerSize).align(Align.bottomLeft);

        topOutlineImage.getColor().a = 0;
        topOutlineImage.addAction(Actions.sequence(
                Actions.delay(0.6f),
                Actions.fadeIn(0.5f)
        ));

        leftCorner.getColor().a = 0;
        leftCorner.addAction(Actions.sequence(
                Actions.delay(0.6f),
                Actions.fadeIn(0.5f)
        ));

        rightCorner.getColor().a = 0;
        rightCorner.addAction(Actions.sequence(
                Actions.delay(0.6f),
                Actions.fadeIn(0.5f)
        ));
    }

    private void buildBottomOutline(Table table) {
        int cornerSize = (int)(Gdx.graphics.getHeight() * 0.015f);
        Image leftCorner = new Image(ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, cornerSize, cornerSize));
        table.add(leftCorner).size(cornerSize).align(Align.topRight);

        Drawable bottomOutline = ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, 1, (int)(Gdx.graphics.getHeight() * 0.03f));
        Image bottomOutlineImage = new Image(bottomOutline);
        table.add(bottomOutlineImage)
                .colspan(3)
                .fillX()
                .expandX()
                .padBottom(Gdx.graphics.getHeight() * 0.05f);

        Image rightCorner = new Image(ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, cornerSize, cornerSize));
        table.add(rightCorner).size(cornerSize).align(Align.topLeft);

        bottomOutlineImage.getColor().a = 0;
        bottomOutlineImage.addAction(Actions.sequence(
                Actions.delay(0.8f),
                Actions.fadeIn(0.5f)
        ));

        leftCorner.getColor().a = 0;
        leftCorner.addAction(Actions.sequence(
                Actions.delay(0.8f),
                Actions.fadeIn(0.5f)
        ));

        rightCorner.getColor().a = 0;
        rightCorner.addAction(Actions.sequence(
                Actions.delay(0.8f),
                Actions.fadeIn(0.5f)
        ));
    }

    private void buildLeftOutline(Table table) {
        Drawable leftOutline = ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, (int)(Gdx.graphics.getHeight() * 0.03f), 1);
        Image leftOutlineImage = new Image(leftOutline);
        table.add(leftOutlineImage)
            .fillY()
            .expandY()
            .padLeft(Gdx.graphics.getHeight() * 0.05f);

        leftOutlineImage.getColor().a = 0;
        leftOutlineImage.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildRightOutline(Table table) {
        Drawable rightOutline = ColorUtils.getSolidColorDrawable(
                colorScheme.menuBorderColor, (int)(Gdx.graphics.getHeight() * 0.03f), 1);
        Image rightOutlineImage = new Image(rightOutline);
        table.add(rightOutlineImage)
            .fillY()
            .expandY()
            .padRight(Gdx.graphics.getHeight() * 0.05f);

        rightOutlineImage.getColor().a = 0;
        rightOutlineImage.addAction(Actions.sequence(
            Actions.delay(0.7f),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildMiddleTable() {
        buildBackButton();
        middleTable.row();

        buildTitleLabel();
        middleTable.row();

        buildSettings();
    }

    private void buildBackButton() {
        Sprite sprite = AssetManager.getInstance().getSprite("home");
        sprite.flip(true, false);

        float size = Gdx.graphics.getHeight() * 0.09f;
        final Image button = new Image(sprite);
        middleTable.add(button)
            .align(Align.left)
            .size(size);

        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                button.setColor(Color.LIGHT_GRAY);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                Screen mainMenuScreen = new MainMenuScreen();
                EventManager.getInstance().notify(new ScreenChangeEvent(mainMenuScreen));
            }
        });

        button.getColor().a = 0;
        button.addAction(Actions.sequence(
            Actions.delay(1f),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildTitleLabel() {
        Sprite sprite = AssetManager.getInstance().getSprite("settings_menu_title");
        Image image = new Image(sprite);
        image.setAlign(Align.center);
        image.setScaling(Scaling.fillX);
        image.setColor(ColorUtils.getShade(colorScheme.menuTitleColor, 2f));
        middleTable.add(image)
            .colspan(5)
            .fillX()
            .width(Gdx.graphics.getWidth() * 0.55f)
            .pad(Gdx.graphics.getHeight() * 0.01f);

        image.setOrigin(Align.center);
        image.getColor().a = 0;
        image.addAction(Actions.parallel(
            Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(0.5f)
            ),
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(1.05f, 1.05f, 1, Interpolation.smooth),
                    Actions.scaleTo(1, 1, 1, Interpolation.smooth)
                )
            )
        ));
    }

    private void buildSettings() {
        buildDifficultySelectBox();
        middleTable.row();

        buildThemesSelectBox();
        middleTable.row();

        final Slider musicVolumeSlider = buildSlider(Strings.getInstance().get("background_music"),
                "settings_enable_background_music", 1.75f, 1);
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().musicVolume = musicVolumeSlider.getValue();
            }
        });
        middleTable.row();

        final Slider soundEffectVolumeSlider = buildSlider(Strings.getInstance().get("sound_effects"),
                "settings_enable_sound_effects", 2f, 1);
        soundEffectVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().soundVolume = soundEffectVolumeSlider.getValue();
            }
        });
        middleTable.row();

        buildCheckbox(Strings.getInstance().get("left_handed_mode"),
                "settings_enable_left_handed_mode", 2.25f, false);
        middleTable.row();

        //buildRestorePurchasesButton();
    }

    private void buildDifficultySelectBox() {
        Table table = new Table();
        LabelStyle labelStyle = new LabelStyle(Fonts.SETTINGS_THEMES_SELECT, Color.WHITE);
        String text = Strings.getInstance().get("difficulty");
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        table.add(label).fillX().align(Align.center).padRight(20);

        label.getColor().a = 0;
        label.addAction(Actions.sequence(
                Actions.delay(1.25f),
                Actions.fadeIn(0.5f)
        ));

        float padding = Gdx.graphics.getHeight() * 0.01f;
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = Fonts.SETTINGS_DIFFICULTY_SELECT;
        selectBoxStyle.fontColor = colorScheme.settingsMenuInputTextColor;
        selectBoxStyle.background = ColorUtils.getSolidColorDrawable(colorScheme.settingsMenuInputBackgroundColor);
        selectBoxStyle.background.setTopHeight(padding);
        selectBoxStyle.background.setBottomHeight(padding);

        padding = Gdx.graphics.getHeight() * 0.05f;
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Fonts.SETTINGS_DIFFICULTY_SELECT;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = ColorUtils.getSolidColorDrawable(Color.BLACK);
        listStyle.selection.setTopHeight(padding);
        listStyle.selection.setBottomHeight(padding);

        selectBoxStyle.listStyle = listStyle;

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = ColorUtils.getSolidColorDrawable(Color.BLACK);
        selectBoxStyle.scrollStyle = scrollStyle;

        final Array<String> difficulties = new Array<>();
        difficulties.add(Strings.getInstance().get("casual"));
        difficulties.add(Strings.getInstance().get("normal"));
        difficulties.add(Strings.getInstance().get("hardcore"));

        int difficulty = (int) Persistence.getInstance().getLong("difficulty", 1);
        String difficultyName = difficulties.get(difficulty);

        SelectBox selectBox = new SelectBox(selectBoxStyle);
        selectBox.setItems(difficulties.toArray());
        selectBox.setSelected(difficultyName);
        selectBox.setAlignment(Align.center);
        selectBox.getList().setAlignment(Align.center);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String difficultyName = ((SelectBox)actor).getSelected().toString();
                Persistence.getInstance().putLong("difficulty", difficulties.indexOf(difficultyName, false));
            }
        });

        table.add(selectBox).fillX().width(Gdx.graphics.getWidth() * 0.15f).align(Align.center);

        table.getColor().a = 0;
        table.addAction(Actions.sequence(
                Actions.delay(1.25f),
                Actions.fadeIn(0.5f)
        ));

        middleTable.add(table).expandX().padTop(Gdx.graphics.getHeight() * 0.08f);
    }

    private void buildThemesSelectBox() {
        Table table = new Table();
        LabelStyle labelStyle = new LabelStyle(Fonts.SETTINGS_THEMES_SELECT, Color.WHITE);
        String text = Strings.getInstance().get("theme");
        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.center);
        table.add(label).fillX().align(Align.center).padRight(20);

        label.getColor().a = 0;
        label.addAction(Actions.sequence(
            Actions.delay(1.25f),
            Actions.fadeIn(0.5f)
        ));

        float padding = Gdx.graphics.getHeight() * 0.01f;
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = Fonts.SETTINGS_THEMES_SELECT;
        selectBoxStyle.fontColor = colorScheme.settingsMenuInputTextColor;
        selectBoxStyle.background = ColorUtils.getSolidColorDrawable(colorScheme.settingsMenuInputBackgroundColor);
        selectBoxStyle.background.setBottomHeight(padding);
        selectBoxStyle.background.setTopHeight(padding);

        padding = Gdx.graphics.getHeight() * 0.025f;
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = Fonts.SETTINGS_THEMES_SELECT;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = ColorUtils.getSolidColorDrawable(Color.BLACK);
        listStyle.selection.setBottomHeight(padding);
        listStyle.selection.setTopHeight(padding);
        selectBoxStyle.listStyle = listStyle;

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = ColorUtils.getSolidColorDrawable(Color.BLACK);
        selectBoxStyle.scrollStyle = scrollStyle;

        String[] themes = getAllThemes();
        SelectBox selectBox = new SelectBox(selectBoxStyle);
        selectBox.setItems(themes);
        selectBox.setSelected(ColorScheme.getColorSchemeName().toUpperCase());
        selectBox.setAlignment(Align.center);
        selectBox.getList().setAlignment(Align.center);

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String themeName = ((SelectBox)actor).getSelected().toString();
                Persistence.getInstance().putString("color_scheme", themeName.toLowerCase());

                EventManager.getInstance().notify(new ScreenChangeEvent(new SettingsScreen()));
            }
        });

        table.add(selectBox).fillX().width(Gdx.graphics.getWidth() * 0.15f).align(Align.center);

        table.getColor().a = 0;
        table.addAction(Actions.sequence(
            Actions.delay(1.5f),
            Actions.fadeIn(0.5f)
        ));

        middleTable.add(table).expandX().padTop(Gdx.graphics.getHeight() * 0.05f);
    }

    private void buildRestorePurchasesButton() {
        if (Persistence.getInstance().getBoolean("purchases_restored", false)) {
            return;
        }

        LabelStyle labelStyle = new LabelStyle(
                Fonts.SETTINGS_RESTORE_PURCHASE, colorScheme.settingsMenuRestorePurchaseButtonColor);
        String text = Strings.getInstance().get("restore_purchases");
        final Label label = new Label(text, labelStyle);
        middleTable.add(label).expandX().padTop(Gdx.graphics.getHeight() * 0.05f);;

        label.getColor().a = 0;
        label.addAction(Actions.sequence(
            Actions.delay(2.5f),
            Actions.fadeIn(0.5f)
        ));

        label.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                label.setColor(ColorUtils.getShade(colorScheme.settingsMenuRestorePurchaseButtonColor, 0.6f));
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                label.setColor(colorScheme.settingsMenuRestorePurchaseButtonColor);
                if (!restorePurchasesButtonClicked) {
                    restorePurchasesButtonClicked = true;
                    PurchaseManager.getInstance().restorePurchases();
                }
            }
        });
    }

    private void buildCheckbox(String text, final String settingsKey, float delay, boolean isCheckedByDefault) {
        CheckBox.CheckBoxStyle style = getCheckboxStyle(Color.WHITE);
        final CheckBox checkbox = new CheckBox(text, style);

        boolean isChecked = Persistence.getInstance().getBoolean(settingsKey, isCheckedByDefault);
        checkbox.setChecked(isChecked);

        checkbox.getLabelCell().padLeft(Gdx.graphics.getWidth() * 0.015f);

        checkbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                Persistence.getInstance().putBoolean(settingsKey, checkbox.isChecked());
            }
        });

        middleTable.add(checkbox).expandX().padTop(Gdx.graphics.getHeight() * 0.05f);;

        checkbox.getColor().a = 0;
        checkbox.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));
    }

    private CheckBox.CheckBoxStyle getCheckboxStyle(Color fontColor) {
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        style.font = Fonts.SETTINGS_CHECKBOX_LABEL;
        style.fontColor = fontColor;

        int checkboxSize = (int)(Gdx.graphics.getHeight() * 0.03f);
        Sprite checkboxOff = AssetManager.getInstance().getSprite("checkbox_off");
        checkboxOff.setColor(colorScheme.settingsMenuInputBackgroundColor);
        style.checkboxOff = new SpriteDrawable(checkboxOff);
        style.checkboxOff.setMinWidth(checkboxSize);
        style.checkboxOff.setMinHeight(checkboxSize);
        Sprite checkboxOn = AssetManager.getInstance().getSprite("checkbox_on");
        checkboxOn.setColor(colorScheme.settingsMenuInputBackgroundColor);
        style.checkboxOn = new SpriteDrawable(checkboxOn);
        style.checkboxOn.setMinWidth(checkboxSize);
        style.checkboxOn.setMinHeight(checkboxSize);

        return style;
    }

    private Slider buildSlider(String text, final String settingsKey, float delay, float defaultValue) {
        Table table = new Table();

        LabelStyle labelStyle = new LabelStyle(Fonts.SETTINGS_SLIDER_LABEL, Color.WHITE);
        Label label = new Label(text, labelStyle);

        table.add(label).expand().padRight(Gdx.graphics.getWidth() * 0.015f);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        float size = Gdx.graphics.getHeight() * 0.04f;
        sliderStyle.background = ColorUtils.getSolidColorDrawable(
                colorScheme.settingsMenuInputBackgroundColor, (int)size, (int)size);
        sliderStyle.knob = ColorUtils.getSolidColorDrawable(
                colorScheme.settingsMenuSliderKnobColor, (int)size, (int)size);
        final Slider slider = new Slider(0, 1, 0.1f, false, sliderStyle);

        float sliderValue = Persistence.getInstance().getFloat(settingsKey, defaultValue);
        slider.setValue(sliderValue);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Persistence.getInstance().putFloat(settingsKey, slider.getValue());
            }
        });

        table.add(slider).expand().width(Gdx.graphics.getWidth() * 0.3f);

        table.getColor().a = 0;
        table.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));

        middleTable.add(table).expandX().padTop(Gdx.graphics.getHeight() * 0.05f);

        return slider;
    }

    private void setKeyListener() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                }
                return true;
            }
        });
    }

    private String[] getAllThemes() {
        return new String[] {
            Strings.getInstance().get("theme_twilight").toUpperCase(),
            Strings.getInstance().get("theme_sandstorm").toUpperCase(),
            Strings.getInstance().get("theme_aquatic").toUpperCase(),
            Strings.getInstance().get("theme_grayscale").toUpperCase()
        };
    }
}
