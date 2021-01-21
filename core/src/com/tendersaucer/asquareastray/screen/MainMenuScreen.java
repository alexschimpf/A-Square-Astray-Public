package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.ColorScheme;
import com.tendersaucer.asquareastray.Fonts;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.Shaders;
import com.tendersaucer.asquareastray.Strings;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.purchase.Offers;
import com.tendersaucer.asquareastray.purchase.PurchaseManager;
import com.tendersaucer.asquareastray.screen.transition.RandomWipeTransition;
import com.tendersaucer.asquareastray.screen.transition.ScreenTransition;
import com.tendersaucer.asquareastray.event.StartLevelEvent;
import com.tendersaucer.asquareastray.particle.ParticleEffects;
import com.tendersaucer.asquareastray.utils.ColorUtils;
import com.tendersaucer.asquareastray.utils.RandomUtils;


public class MainMenuScreen implements Screen {

    private static final int NUM_LEVELS_PER_PAGE = 10;

    private int currPage;
    private boolean levelButtonClicked;
    private boolean unlockButtonClicked;
    private int lastUnlockedLevelId;
    private long numStarsAvailable;
    private Table middleTable;
    private Image navButtonLeft;
    private Image navButtonRight;
    private final Stage stage;
    private final ColorScheme colorScheme;
    private final ScreenTransition transition;

    public MainMenuScreen() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        RandomWipeTransition.Direction direction = RandomUtils.pickFrom(
                RandomWipeTransition.Direction.LEFT, RandomWipeTransition.Direction.RIGHT,
                RandomWipeTransition.Direction.UP, RandomWipeTransition.Direction.DOWN);
        transition = new RandomWipeTransition(
                stage, ScreenTransition.DEFAULT_DURATION, direction, Interpolation.slowFast);

        colorScheme = ColorScheme.getColorScheme();
    }

    @Override
    public void show() {
        lastUnlockedLevelId = Persistence.getInstance().getInteger("level_id", 1);
        numStarsAvailable = Persistence.getInstance().getLong("total_earned_stars", 0);
        currPage = MathUtils.ceil(lastUnlockedLevelId / (float)NUM_LEVELS_PER_PAGE) - 1;

        Gdx.input.setInputProcessor(stage);
        setKeyListener();

        AssetManager.getInstance().load();

        buildUI();

        AudioManager.playMusic(AudioManager.getInstance().music);
        AudioManager.playMusic(AudioManager.getInstance().musicSlow, 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (transition.hasStarted()) {
            transition.update();
        }

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

        buildLeftNavButton(mainTable);

        middleTable = new Table();
        buildMiddleTable(true);
        mainTable.add(middleTable)
            .fill()
            .padTop(Gdx.graphics.getHeight() * 0.02f)
            .padBottom(Gdx.graphics.getHeight() * 0.02f);

        buildRightNavButton(mainTable);

        buildRightOutline(mainTable);
        mainTable.row();
        buildBottomOutline(mainTable);

        stage.addActor(mainTable);

        if (shouldShowDifficultyTip()) {
            Persistence.getInstance().putBoolean("difficulty_tip_shown", true);
            showDifficultyTipModal();
        } else if (shouldShowThemeTip()) {
            Persistence.getInstance().putBoolean("theme_tip_shown", true);
            showThemeTipModal();
        }
    }

    private void buildBackground() {
        Sprite sprite = AssetManager.getInstance().getSprite("main_menu_background");
        Image image = new Image(sprite);
        image.setScaling(Scaling.fill);
        image.setFillParent(true);
        stage.addActor(image);
    }

    private void buildParticleEffects() {
        if (allLevelsComplete()) {
            Vector2 position = new Vector2(0, Gdx.graphics.getHeight() * 1.1f);
            ParticleEffectEmitter emitter = ParticleEffects.getEmitter(
                    ParticleEffects.ALL_LEVELS_COMPLETE, position, 0);
            stage.addActor(emitter);
            emitter.emit();
        } else {
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

    private void buildLeftNavButton(Table table) {
        Sprite sprite = AssetManager.getInstance().getSprite("main_menu_nav_button");
        sprite.flip(true, false);

        float padding = Gdx.graphics.getWidth() * 0.03f;
        float width = Gdx.graphics.getWidth() * 0.05f;
        float height = width * (sprite.getHeight() / sprite.getWidth());

        navButtonLeft = new Image(sprite);
        table.add(navButtonLeft)
            .expand()
            .padRight(padding)
            .size(width, height)
            .align(Align.right);

        final int lastPage = MathUtils.ceil(Globals.NUM_LEVELS / 10.0f) - 1;
        navButtonLeft.setColor(new Color(1, 1, 1, 0));
        navButtonLeft.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                currPage = MathUtils.clamp(currPage - 1, 0, lastPage);
                onPageUpdated();
                return true;
            }
        });
        if (currPage > 0) {
            navButtonLeft.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.fadeIn(0.5f)
            ));
        } else {
            navButtonLeft.setVisible(false);
            navButtonLeft.setColor(colorScheme.mainMenuSecondaryColor);
        }
    }

    private void buildRightNavButton(Table table) {
        Sprite sprite = AssetManager.getInstance().getSprite("main_menu_nav_button");

        float padding = Gdx.graphics.getWidth() * 0.03f;
        float width = Gdx.graphics.getWidth() * 0.05f;
        float height = width * (sprite.getHeight() / sprite.getWidth());

        navButtonRight = new Image(sprite);
        table.add(navButtonRight)
            .expand()
            .padLeft(padding)
            .size(width, height)
            .align(Align.left);

        final int lastPage = MathUtils.ceil(Globals.NUM_LEVELS / 10.0f) - 1;
        navButtonRight.setColor(new Color(1, 1, 1, 0));
        navButtonRight.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                currPage = MathUtils.clamp(currPage + 1, 0, lastPage);
                onPageUpdated();
                return true;
            }
        });
        if (currPage < lastPage) {
            navButtonRight.addAction(Actions.sequence(
                    Actions.delay(2f),
                    Actions.fadeIn(0.5f)
            ));
        } else {
            navButtonRight.setVisible(false);
            navButtonRight.setColor(colorScheme.mainMenuSecondaryColor);
        }
    }

    private void buildMiddleTable(boolean isFirstTime) {
        middleTable.clear();

        buildTopBar(isFirstTime);
        middleTable.row();

        buildTitleLabel(isFirstTime);
        middleTable.row();

        Sprite levelButtonSprite = AssetManager.getInstance().getSprite("main_menu_level_box");
        float padding = Gdx.graphics.getWidth() * 0.01f;
        float width = Gdx.graphics.getWidth() * 0.1f;
        float height = width * (levelButtonSprite.getHeight() / levelButtonSprite.getWidth());
        int firstLevelOnPage = currPage * NUM_LEVELS_PER_PAGE;
        for (int i = 1; i <= NUM_LEVELS_PER_PAGE; i++) {
            final int levelId = firstLevelOnPage + i;
            boolean isFinalPage = levelId >= Globals.NUM_LEVELS - NUM_LEVELS_PER_PAGE;
            if (isFinalPage && levelId > Globals.NUM_LEVELS) {
                middleTable.add()
                        .pad(padding)
                        .expandX()
                        .size(width, height);
                if (i == NUM_LEVELS_PER_PAGE / 2) {
                    middleTable.row();
                }
            } else {
                float delay = isFirstTime ? 1 + (i * 0.1f) : i * 0.1f;
                addLevelButton(levelId, width, height, padding, delay);
            }
        }
    }

    private void buildTopBar(boolean isFirstTime) {
        buildSettingsButton(isFirstTime);
        //buildRemoveAdsLabel(isFirstTime);
        middleTable.add().colspan(3).expandX();
        buildStarsLabel(isFirstTime);
    }

    private void buildSettingsButton(boolean isFirstTime) {
        final Image settingsButton = new Image(AssetManager.getInstance().getSprite("settings"));
        float size = Gdx.graphics.getHeight() * 0.09f;
        middleTable.add(settingsButton)
            .align(Align.left)
            .size(size);

        settingsButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                settingsButton.setColor(Color.LIGHT_GRAY);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                Screen settingsScreen = new SettingsScreen();
                EventManager.getInstance().notify(new ScreenChangeEvent(settingsScreen));
            }
        });

        if (isFirstTime) {
            settingsButton.getColor().a = 0;
            settingsButton.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    private void buildRemoveAdsLabel(boolean isFirstTime) {
        if (Persistence.getInstance().getBoolean("purchased_remove_ads", false)) {
            middleTable.add().colspan(3).expandX();
        } else {
            LabelStyle style = new LabelStyle(Fonts.MAIN_MENU_REMOVE_ADS, Color.WHITE);
            String text = Strings.getInstance().get("remove_ads");
            Label label = new Label(text, style);
            if (isFirstTime) {
                label.getColor().a = 0;
                label.addAction(Actions.sequence(
                    Actions.delay(1),
                    Actions.fadeIn(0.5f)
                ));
            }
            middleTable.add(label).colspan(3).expandX();

            label.addAction(Actions.sequence(
                Actions.delay(1.5f),
                Actions.forever(
                    Actions.sequence(
                        Actions.fadeOut(0.5f),
                        Actions.fadeIn(0.5f)
                    )
                )
            ));

            label.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    PurchaseManager.getInstance().purchase(Offers.SKU.REMOVE_ADS);
                }
            });
        }
    }

    private void buildStarsLabel(boolean isFirstTime) {
        Table table = new Table();

        Image starImage = new Image(AssetManager.getInstance().getSprite("star"));
        starImage.setColor(colorScheme.mainMenuStarColor);
        float size = Gdx.graphics.getHeight() * 0.09f;
        table.add(starImage)
            .size(size)
            .padRight(size * 0.2f);

        long numStars = Persistence.getInstance().getLong("total_earned_stars", 0);
        LabelStyle labelStyle = new LabelStyle(Fonts.MAIN_MENU_NUM_STARS, Color.WHITE);
        Label numStarsLabel = new Label(String.valueOf(numStars), labelStyle);
        table.add(numStarsLabel);

        middleTable.add(table)
            .align(Align.right);

        if (isFirstTime) {
            starImage.getColor().a = 0;
            starImage.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(0.5f)
            ));

            numStarsLabel.getColor().a = 0;
            numStarsLabel.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    private void buildTitleLabel(boolean isFirstTime) {
        Sprite sprite = AssetManager.getInstance().getSprite("main_menu_title");
        Image image = new Image(sprite);
        image.setAlign(Align.center);
        image.setScaling(Scaling.fillX);
        image.setColor(ColorUtils.getShade(colorScheme.menuTitleColor, 2f));
        middleTable.add(image)
            .colspan(5)
            .fillX()
            .pad(Gdx.graphics.getHeight() * 0.05f);

        image.setOrigin(Align.center);
        image.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 1, Interpolation.smooth),
                Actions.scaleTo(1, 1, 1, Interpolation.smooth)
            )
        ));

        if (isFirstTime) {
            image.getColor().a = 0;
            image.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    private void addLevelButton(final int levelId, float width, float height, float padding, float delay) {
        int numStars = Persistence.getInstance().getInteger("level_" + levelId + "_stars", 0);
        boolean isLevelUnlocked = levelId <= lastUnlockedLevelId;
        boolean isUnlockButton = numStarsAvailable >= Globals.NUM_STARS_FOR_UNLOCK &&
                levelId == lastUnlockedLevelId + 1;
        String backgroundSpriteKey = "main_menu_level_box" + (numStars >= 1 ? numStars : "");
        if (numStars >= 1) {
            backgroundSpriteKey += "_" + ColorScheme.getColorSchemeName();
        }
        if (isUnlockButton) {
            backgroundSpriteKey = "main_menu_level_unlock_button";
        } else if (!isLevelUnlocked) {
            backgroundSpriteKey = "main_menu_level_locked";
        }
        Sprite backgroundSprite = AssetManager.getInstance().getSprite(backgroundSpriteKey);
        LabelStyle labelStyle = new LabelStyle(Fonts.MAIN_MENU_LEVEL_BUTTON, colorScheme.mainMenuLevelButtonTextColor);
        labelStyle.background = new SpriteDrawable(backgroundSprite);
        final Label button = new Label("", labelStyle);
        button.setAlignment(Align.center);
        if (isUnlockButton) {
            button.setColor(colorScheme.menuTitleColor);
        } else if (isLevelUnlocked) {
            button.setText(String.valueOf(levelId));
        } else{
            button.setColor(Color.DARK_GRAY);
        }
        middleTable.add(button)
            .pad(padding)
            .expandX()
            .size(width, height);
        if (levelId % (NUM_LEVELS_PER_PAGE / 2) == 0) {
            middleTable.row();
        }

        if (isUnlockButton) {
            button.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                    button.setColor(Color.LIGHT_GRAY);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!unlockButtonClicked) {
                        unlockButtonClicked = true;

                        AudioManager.playSound(AudioManager.getInstance().clickSound);
                        showUnlockConfirmation(levelId);
                    }
                }
            });
        } else if (isLevelUnlocked) {
            button.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                    button.setColor(Color.LIGHT_GRAY);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!levelButtonClicked) {
                        levelButtonClicked = true;

                        AudioManager.playSound(AudioManager.getInstance().clickSound);
                        transition.start();
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                EventManager.getInstance().notify(new StartLevelEvent(levelId));
                            }
                        }, ScreenTransition.DEFAULT_DURATION + 0.5f);
                    }
                }
            });
        }

        button.getColor().a = 0;
        if (isUnlockButton) {
            button.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.fadeIn(0.5f),
                Actions.forever(
                    Actions.sequence(
                        Actions.color(colorScheme.mainMenuSecondaryColor, 0.25f),
                        Actions.color(colorScheme.menuTitleColor, 0.25f)
                    )
                )
            ));
        } else {
            button.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.fadeIn(0.5f)
            ));
        }
    }

    private void onPageUpdated() {
        int lastPage = MathUtils.ceil(Globals.NUM_LEVELS / 10.0f) - 1;
        navButtonLeft.setVisible(currPage != 0);
        navButtonRight.setVisible(currPage != lastPage);
        buildMiddleTable(false);
    }

    private void showUnlockConfirmation(final int levelId) {
        Drawable background = ColorUtils.getSolidColorDrawable(Color.WHITE);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.TUTORIAL, Color.WHITE, background);
        windowStyle.stageBackground = ColorUtils.getSolidColorDrawable(new Color(0, 0, 0, 0.9f));

        final Dialog dialog = new Dialog("", windowStyle);
        dialog.getCell(dialog.getContentTable()).expand(false, false);

        TextButton.TextButtonStyle cancelButtonStyle = new TextButton.TextButtonStyle();
        cancelButtonStyle.font = Fonts.MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION;
        cancelButtonStyle.fontColor = Color.WHITE;
        cancelButtonStyle.downFontColor = Color.LIGHT_GRAY;
        String cancelText = Strings.getInstance().get("cancel");
        TextButton cancelButton = new TextButton(cancelText, cancelButtonStyle);
        cancelButton.align(Align.center);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().clickSound);
                unlockButtonClicked = false;
                dialog.hide(Actions.fadeOut(0.5f));
            }
        });

        TextButton.TextButtonStyle okButtonStyle = new TextButton.TextButtonStyle();
        okButtonStyle.font = Fonts.MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION;
        okButtonStyle.fontColor = colorScheme.modalPrimaryButtonColor;
        String unlockText = Strings.getInstance().get("unlock_level");
        TextButton okButton = new TextButton(unlockText, okButtonStyle);
        okButton.align(Align.center);
        okButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                AudioManager.playSound(AudioManager.getInstance().unlockLevelSound);
                unlockLevel(levelId);
            }
        });

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = Fonts.MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION;
        labelStyle.fontColor = Color.WHITE;
        String confirmationText = Strings.getInstance().get("unlock_confirmation");
        Label label = new Label(confirmationText, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.center);

        float paddingTop = Gdx.graphics.getHeight() * 0.1f;
        float paddingRight = Gdx.graphics.getWidth() * 0.1f;
        dialog.getContentTable().add(label).width(Gdx.graphics.getWidth() * 0.6f);
        dialog.setFillParent(true);
        dialog.background(ColorUtils.getSolidColorDrawable(Color.CLEAR));
        dialog.getButtonTable().add(cancelButton).padTop(paddingTop).padRight(paddingRight);
        dialog.getButtonTable().add(okButton).padTop(paddingTop);

        dialog.getColor().a = 0;
        dialog.show(stage, Actions.fadeIn(0.5f));
    }

    private void showDifficultyTipModal() {
        String text = Strings.getInstance().get("difficulty_tip");
        showInfoModal(text);
    }

    private void showThemeTipModal() {
        String text = Strings.getInstance().get("theme_tip");
        showInfoModal(text);
    }

    private void showInfoModal(String text) {
        Drawable background = ColorUtils.getSolidColorDrawable(Color.WHITE);
        Window.WindowStyle windowStyle = new Window.WindowStyle(Fonts.TUTORIAL, Color.WHITE, background);
        windowStyle.stageBackground = ColorUtils.getSolidColorDrawable(new Color(0, 0, 0, 0.9f));

        final Dialog dialog = new Dialog("", windowStyle);
        dialog.getCell(dialog.getContentTable()).expand(false, false);

        TextButton.TextButtonStyle okButtonStyle = new TextButton.TextButtonStyle();
        okButtonStyle.font = Fonts.MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION;
        okButtonStyle.fontColor = colorScheme.modalPrimaryButtonColor;
        String buttonText = Strings.getInstance().get("ok");
        TextButton okButton = new TextButton(buttonText, okButtonStyle);
        okButton.align(Align.center);
        okButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dialog.hide();
            }
        });

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = Fonts.MAIN_MENU_LEVEL_UNLOCK_CONFIRMATION;
        labelStyle.fontColor = Color.WHITE;
        Label label = new Label(text, labelStyle);
        label.setWrap(true);
        label.setAlignment(Align.center);

        float paddingTop = Gdx.graphics.getHeight() * 0.1f;
        dialog.getContentTable().add(label).width(Gdx.graphics.getWidth() * 0.6f);
        dialog.setFillParent(true);
        dialog.background(ColorUtils.getSolidColorDrawable(Color.CLEAR));
        dialog.getButtonTable().add(okButton).padTop(paddingTop);

        dialog.getColor().a = 0;
        dialog.show(stage, Actions.fadeIn(1.5f));
    }

    private void unlockLevel(int levelId) {
        Persistence.getInstance().putInteger("level_id", levelId);
        Persistence.getInstance().increment("total_earned_stars", -Globals.NUM_STARS_FOR_UNLOCK);
        EventManager.getInstance().notify(new ScreenChangeEvent(new MainMenuScreen()));
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

    private boolean allLevelsComplete() {
        for (int i = 1; i <= Globals.NUM_LEVELS; i++) {
            int numStars = (int) Persistence.getInstance().getLong("level_" + i + "_stars", 0);
            if (numStars != 3) {
                return false;
            }
        }

        return true;
    }

    private boolean shouldShowDifficultyTip() {
        int currLevel = Persistence.getInstance().getInteger("level_id", 1);
        if (currLevel > 10) {
            return false;
        }

        if (Persistence.getInstance().getBoolean("difficulty_tip_shown", false)) {
            return false;
        }

        int totalNumPlays = 0;
        long currLevelNumPlays = 0;
        for (int i = 1; i <= currLevel; i++) {
            long levelNumPlays = Persistence.getInstance().getLong("level_" + i + "_play_count", 0);
            totalNumPlays += levelNumPlays;
            if (i == currLevel) {
                currLevelNumPlays = levelNumPlays;
            }
        }
        float averageNumPlaysPerLevel = totalNumPlays / (float)currLevel;

        return averageNumPlaysPerLevel > 10 || currLevelNumPlays > 20;
    }

    private boolean shouldShowThemeTip() {
        if (Persistence.getInstance().getBoolean("theme_tip_shown", false)) {
            return false;
        }

        int currLevel = Persistence.getInstance().getInteger("level_id", 1);
        return currLevel > 10;
    }
}
