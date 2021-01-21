package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.ColorScheme;
import com.tendersaucer.asquareastray.Fonts;
import com.tendersaucer.asquareastray.Globals;
import com.tendersaucer.asquareastray.Strings;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.particle.ParticleEffectEmitter;
import com.tendersaucer.asquareastray.screen.transition.RandomWipeTransition;
import com.tendersaucer.asquareastray.screen.transition.ScreenTransition;
import com.tendersaucer.asquareastray.utils.ColorUtils;
import com.tendersaucer.asquareastray.utils.RandomUtils;
import com.tendersaucer.asquareastray.event.StartLevelEvent;
import com.tendersaucer.asquareastray.particle.ParticleEffects;


public class SuccessScreen implements Screen {

    private Table middleTable;
    private final int levelId;
    private final int numStarsEarned;
    private final int elapsedTime;
    private boolean buttonClicked;
    private final Stage stage;
    private final ColorScheme colorScheme;
    private final ScreenTransition transition;

    public SuccessScreen(Level level) {
        levelId = level.getId();
        elapsedTime = (int)(level.getElapsedTime() / 1000);
        numStarsEarned = level.getEarnedStars();

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
        Gdx.input.setInputProcessor(stage);
        setKeyListener();

        AssetManager.getInstance().load();

        buildUI();
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

        mainTable.add().expand();

        middleTable = new Table();
        buildMiddleTable();
        mainTable.add(middleTable)
            .expand()
            .padTop(Gdx.graphics.getHeight() * 0.02f)
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
                ParticleEffects.LEVEL_COMPLETE, position, 0);
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
        buildReplayButton();
        middleTable.row();

        buildTitleLabel();
        middleTable.row();

        buildNextLevelButton();
        middleTable.row();

        buildStars();
        middleTable.row();

        buildElapsedTimeLabel();
    }

    private void buildBackButton() {
        float size = Gdx.graphics.getHeight() * 0.09f;
        Sprite sprite = AssetManager.getInstance().getSprite("home");
        final Image button = new Image(sprite);
        middleTable.add(button)
            .align(Align.left)
            .size(size)
            .expandX();

        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                button.setColor(Color.LIGHT_GRAY);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!buttonClicked) {
                    buttonClicked = true;
                    AudioManager.playSound(AudioManager.getInstance().clickSound);
                    Screen mainMenuScreen = new MainMenuScreen();
                    EventManager.getInstance().notify(new ScreenChangeEvent(mainMenuScreen));
                }
            }
        });

        float delay = (2.5f + ((numStarsEarned - 1) * 0.5f));
        button.getColor().a = 0;
        button.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildReplayButton() {
        float size = Gdx.graphics.getHeight() * 0.09f;
        Sprite sprite = AssetManager.getInstance().getSprite("replay");
        final Image button = new Image(sprite);
        middleTable.add(button)
            .align(Align.right)
            .size(size)
            .expandX();

        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                button.setColor(Color.LIGHT_GRAY);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!buttonClicked) {
                    buttonClicked = true;

                    AudioManager.playSound(AudioManager.getInstance().clickSound);

                    transition.start();
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            EventManager.getInstance().postNotify(new StartLevelEvent(levelId));
                        }
                    }, ScreenTransition.DEFAULT_DURATION + 0.5f);
                }
            }
        });

        float delay = (2.5f + ((numStarsEarned - 1) * 0.5f));
        button.getColor().a = 0;
        button.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildTitleLabel() {
        LabelStyle titleLabelStyle = new LabelStyle(Fonts.SUCCESS_TITLE, Color.WHITE);
        String text = Strings.getInstance().get("level_num", levelId);
        Label titleLabel = new Label(text, titleLabelStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(ColorUtils.getShade(colorScheme.menuTitleColor, 2f));
        middleTable.add(titleLabel)
            .colspan(2)
            .fillX()
            .padTop(Gdx.graphics.getHeight() * 0.05f)
            .row();

        float delay = (2f + ((numStarsEarned - 1) * 0.5f));
        titleLabel.getColor().a = 0;
        titleLabel.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));

        text = Strings.getInstance().get("complete");
        titleLabel = new Label(text, titleLabelStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(ColorUtils.getShade(colorScheme.menuTitleColor, 2f));
        middleTable.add(titleLabel)
            .colspan(2)
            .fillX()
            .padBottom(Gdx.graphics.getHeight() * 0.05f);

        titleLabel.getColor().a = 0;
        titleLabel.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));
    }

    private void buildNextLevelButton() {
        LabelStyle style = new LabelStyle(Fonts.SUCCESS_NEXT_BUTTON, Color.WHITE);
        String text = Strings.getInstance().get("start_next_level");
        final Label button = new Label(text, style);
        button.setAlignment(Align.center);

        float delay = (2.5f + ((numStarsEarned - 1) * 0.5f));
        button.getColor().a = 0;
        button.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    button.addListener(new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int b) {
                            button.setColor(Color.LIGHT_GRAY);
                            return true;
                        }

                        @Override
                        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                            if (!buttonClicked) {
                                buttonClicked = true;
                                AudioManager.playSound(AudioManager.getInstance().clickSound);
                                if (levelId >= Globals.NUM_LEVELS) {
                                    MainMenuScreen mainMenuScreen = new MainMenuScreen();
                                    EventManager.getInstance().postNotify(new ScreenChangeEvent(mainMenuScreen));
                                } else {
                                    transition.start();
                                    Timer.schedule(new Timer.Task() {
                                        @Override
                                        public void run() {
                                            EventManager.getInstance().postNotify(new StartLevelEvent(levelId + 1));
                                        }
                                    }, ScreenTransition.DEFAULT_DURATION + 0.5f);
                                }
                            }
                        }
                    });
                }
            }),
            Actions.forever(
                Actions.sequence(
                    Actions.fadeIn(0.5f),
                    Actions.fadeOut(0.5f)
                )
            )
        ));

        middleTable.add(button).colspan(2).fill().expand().padBottom(Gdx.graphics.getHeight() * 0.05f);
    }

    private void buildStars() {
        Table table = new Table();
        for (int i = 0; i < numStarsEarned; i++) {
            final Sound sound = i == 0 ? AudioManager.getInstance().star1Sound : i == 1 ?
                    AudioManager.getInstance().star2Sound : AudioManager.getInstance().star3Sound;

            Image starImage = getStarImage();
            starImage.getColor().a = 0;
            starImage.addAction(Actions.sequence(
                Actions.delay(1 + (i * 0.5f)),
                Actions.parallel(
                    Actions.fadeIn(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            AudioManager.playSound(sound);
                        }
                    })
                )
            ));
            float padding = Gdx.graphics.getHeight() * 0.02f;
            table.add(starImage).size(starImage.getWidth(), starImage.getHeight()).pad(padding);
        }

        middleTable.add(table).colspan(2).fill().expand();
    }

    private Image getStarImage() {
        Image starImage = new Image(AssetManager.getInstance().getSprite("star"));
        starImage.setColor(colorScheme.successMenuStarColor);
        float size = Gdx.graphics.getHeight() * 0.18f;
        starImage.setSize(size, size);
        return starImage;
    }

    private void buildElapsedTimeLabel() {
        LabelStyle style = new LabelStyle(Fonts.SUCCESS_ELAPSED_TIME, Color.WHITE);
        String text = Strings.getInstance().get("elapsed_time", elapsedTime);
        Label label = new Label(text, style);
        label.setAlignment(Align.center);

        float delay = (1.5f + ((numStarsEarned - 1) * 0.5f));
        label.getColor().a = 0;
        label.addAction(Actions.sequence(
            Actions.delay(delay),
            Actions.fadeIn(0.5f)
        ));

        middleTable.add(label).colspan(2).fill().expand();
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
}
