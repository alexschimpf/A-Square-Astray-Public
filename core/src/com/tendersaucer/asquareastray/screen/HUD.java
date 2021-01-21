package com.tendersaucer.asquareastray.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.asquareastray.AssetManager;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.Fonts;
import com.tendersaucer.asquareastray.Persistence;
import com.tendersaucer.asquareastray.Strings;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.component.ability.AbilityType;
import com.tendersaucer.asquareastray.event.AbilityActivatedEvent;
import com.tendersaucer.asquareastray.event.AbilityReadyEvent;
import com.tendersaucer.asquareastray.event.LevelStateChangeEvent;
import com.tendersaucer.asquareastray.event.ScreenChangeEvent;
import com.tendersaucer.asquareastray.event.StartLevelEvent;
import com.tendersaucer.asquareastray.event.listener.IAbilityReadyListener;
import com.tendersaucer.asquareastray.event.listener.ILevelStateChangeListener;
import com.tendersaucer.asquareastray.level.LevelState;
import com.tendersaucer.asquareastray.screen.transition.TileScreenTransition;
import com.tendersaucer.asquareastray.screen.transition.ScreenTransition;
import com.tendersaucer.asquareastray.utils.ColorUtils;


public class HUD extends Stage implements IAbilityReadyListener, ILevelStateChangeListener {

    private static final int COUNTDOWN_IMAGE_HEIGHT = (int)(Gdx.graphics.getHeight() * 0.4f);
    private static final int CONTROL_BAR_PADDING = (int)(Gdx.graphics.getHeight() * 0.02f);
    private static final int TOP_BAR_HEIGHT = (int)(Gdx.graphics.getHeight() * 0.12f);
    private static final int BOTTOM_BAR_HEIGHT = (int)(Gdx.graphics.getHeight() * 0.2f);

    private int countdownTime;
    private float origMusicVolume;
    private boolean isAbilityAvailable;
    private LevelState levelState;
    private Label timeLabel;
    private Image countdownImage;
    private Container countdownContainer;
    private Image dashButton;
    private Image restartButton;
    private Image backButton;
    private AnimatedImage clockImage;
    private Timer.Task countdownTask;

    private final Skin skin;
    private final Level level;
    private final ScreenTransition transition;

    public HUD(Level level) {
        super(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        countdownTime = 2;
        isAbilityAvailable = false;
        this.level = level;
        skin = new Skin();
        origMusicVolume = AudioManager.getInstance().musicVolume;

        transition = new TileScreenTransition(this, ScreenTransition.DEFAULT_DURATION);

        setSkinResources();
        buildUI();
    }

    @Override
    public void dispose() {
        super.dispose();

        EventManager.getInstance().mute(LevelStateChangeEvent.class, this);
        EventManager.getInstance().mute(AbilityReadyEvent.class, this);
    }

    @Override
    public void onLevelStateChange(LevelState oldState, LevelState newState) {
        if (newState.equals(LevelState.RUNNING)) {
            if (!oldState.equals(LevelState.COUNTDOWN)) {
                countdownContainer.setVisible(false);
            }

            dashButton.addAction(Actions.fadeIn(0.5f));
            backButton.addAction(Actions.fadeIn(0.5f));
            restartButton.addAction(Actions.fadeIn(0.5f));
            clockImage.setState(com.tendersaucer.asquareastray.animation.AnimatedImage.State.PLAYING);
            isAbilityAvailable = true;
        } else if (newState.equals(LevelState.DONE_FAILURE) || newState.equals(LevelState.DONE_SUCCESS)) {
            levelState = newState;
            dashButton.addAction(Actions.fadeOut(0.5f));
            backButton.addAction(Actions.fadeOut(0.5f));
            restartButton.addAction(Actions.fadeOut(0.5f));
            clockImage.setState(com.tendersaucer.asquareastray.animation.AnimatedImage.State.STOPPED);
            isAbilityAvailable = false;
            if (countdownTask != null) {
                countdownTask.cancel();
            }

            resetMusicVolume();
            startTransition();
        } else if (newState.equals(LevelState.COUNTDOWN)) {
            countdownImage.addAction(Actions.fadeIn(0.3f));
            scheduleCountdown();
        } else if (newState.equals(LevelState.TUTORIAL)) {
            Tutorial tutorial = new Tutorial(level, this);
            tutorial.start();
        }
    }

    @Override
    public void onAbilityReady(AbilityType abilityType) {
        isAbilityAvailable = true;
        if (abilityType.equals(AbilityType.DASH)) {
            dashButton.addAction(Actions.fadeIn(0.5f));
        }
    }

    public void update() {
        act();

        handleTimeLabel();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            startDash();
        }
    }

    public void draw() {
        super.draw();
    }

    public void resizeViewport(float width, float height) {
        getViewport().update((int)width, (int)height);
        getViewport().apply();
    }

    public Image getDashButton() {
        return dashButton;
    }

    public Image getRestartButton() {
        return restartButton;
    }

    public Image getBackButton() {
        return backButton;
    }

    private void setSkinResources() {
        skin.add("dash_right", AssetManager.getInstance().getTextureRegion("dash_right"));
        skin.add("dash_left", AssetManager.getInstance().getTextureRegion("dash_left"));
        skin.add("replay", AssetManager.getInstance().getTextureRegion("replay"));
        skin.add("clock", AssetManager.getInstance().getTextureRegion("clock"));
        skin.add("home", AssetManager.getInstance().getTextureRegion("home"));
    }

    private void buildUI() {
        Table mainTable = new Table();
        mainTable.top().setFillParent(true);

        Table topTable = new Table();
        topTable.setBackground(ColorUtils.getSolidColorDrawable(new Color(0, 0, 0, 0.7f)));
        buildBackButton(topTable);
        buildTimeLabel(topTable);
        buildRestartButton(topTable);
        mainTable.add(topTable).fillX();

        mainTable.row();
        buildCountdownTimer(mainTable);
        mainTable.row();

        Table bottomTable = new Table();
        bottomTable.setBackground(ColorUtils.getSolidColorDrawable(new Color(0, 0, 0, 0.7f)));
        buildDashButton(bottomTable);
        buildLevelIDLabel(bottomTable);
        mainTable.add(bottomTable).fillX();

        addActor(mainTable);
    }

    private void buildBackButton(Table table) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
                skin.get("home", TextureAtlas.AtlasRegion.class));
        backButton = new Image(drawable);
        backButton.setColor(level.getColorScheme().otherButtonColor);
        backButton.getColor().a = Tutorial.showTutorial() ? 1 : 0;

        float size = HUD.TOP_BAR_HEIGHT * 0.75f;
        float leftPadding = Gdx.graphics.getHeight() * 0.03f;
        table.add(backButton).align(Align.left).
                pad(HUD.CONTROL_BAR_PADDING).padLeft(leftPadding).size(size).expandX();

        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (level.isRunning() || level.getState() == LevelState.TUTORIAL) {
                    if (countdownTask != null) {
                        countdownTask.cancel();
                        level.cancelStartLevelStart();
                    }

                    resetMusicVolume();
                    AudioManager.playSound(AudioManager.getInstance().clickSound);
                    EventManager.getInstance().notify(new ScreenChangeEvent(new MainMenuScreen()));
                }

                return true;
            }
        });
    }

    private void buildTimeLabel(Table table) {
        clockImage = new AnimatedImage("clock", 1000, null);
        float size = HUD.TOP_BAR_HEIGHT * 0.6f;
        table.add(clockImage).pad(HUD.CONTROL_BAR_PADDING).size(size);

        LabelStyle style = new LabelStyle(Fonts.HUD_CLOCK_TIME, Color.WHITE);
        timeLabel = new Label("0", style);
        table.add(timeLabel).pad(HUD.CONTROL_BAR_PADDING);
    }

    private void buildRestartButton(Table table) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
                skin.get("replay", TextureAtlas.AtlasRegion.class));
        restartButton = new Image(drawable);
        restartButton.setColor(level.getColorScheme().otherButtonColor);
        restartButton.getColor().a = Tutorial.showTutorial() ? 1 : 0;

        restartButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (level.isRunning()) {
                    resetMusicVolume();
                    AudioManager.playSound(AudioManager.getInstance().clickSound);
                    countdownImage.addAction(Actions.fadeOut(0.5f));
                    EventManager.getInstance().notify(
                            new LevelStateChangeEvent(level.getState(), LevelState.DONE_FAILURE));
                }
                return true;
            }
        });

        float size = HUD.TOP_BAR_HEIGHT * 0.75f;
        float rightPadding = Gdx.graphics.getHeight() * 0.03f;
        table.add(restartButton).align(Align.right).expandX()
                .pad(HUD.CONTROL_BAR_PADDING).padRight(rightPadding).size(size);
    }

    private void buildDashButton(Table table) {
        boolean isLeftHandedMode = Persistence.getInstance().getBoolean("settings_enable_left_handed_mode", false);

        String resourceName = isLeftHandedMode ? "dash_left" : "dash_right";
        TextureRegionDrawable drawable = new TextureRegionDrawable(
                skin.get(resourceName, TextureAtlas.AtlasRegion.class));
        dashButton = new Image(drawable);
        dashButton.setColor(level.getColorScheme().dashButtonColor);
        dashButton.getColor().a = Tutorial.showTutorial() ? 1 : 0;

        dashButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startDash();
                return true;
            }
        });

        float size = HUD.BOTTOM_BAR_HEIGHT * 0.75f;
        float leftPadding = Gdx.graphics.getHeight() * 0.03f;
        table.add(dashButton).align(Align.left).pad(HUD.CONTROL_BAR_PADDING).padLeft(leftPadding)
                .size(size * 1.5f, size).colspan(3).expandX();
    }

    private void buildLevelIDLabel(Table table) {
        LabelStyle style = new LabelStyle(Fonts.HUD_LEVEL_ID, Color.WHITE);
        String text = Strings.getInstance().get("level_num", level.getId());
        Label label = new Label(text, style);
        label.setAlignment(Align.right);
        float rightPadding = Gdx.graphics.getHeight() * 0.03f;
        table.add(label).align(Align.right).pad(HUD.CONTROL_BAR_PADDING)
                .padRight(rightPadding).colspan(1).expandX();
    }

    private void buildCountdownTimer(Table table) {
        countdownContainer = new Container();
        Sprite sprite = AssetManager.getInstance().getSprite("countdown3");
        countdownImage = new Image(sprite);
        countdownImage.getColor().a = 0;
        float width = (sprite.getWidth() / sprite.getHeight()) * COUNTDOWN_IMAGE_HEIGHT;
        countdownImage.getDrawable().setMinWidth(width);
        countdownImage.getDrawable().setMinHeight(COUNTDOWN_IMAGE_HEIGHT);
        countdownContainer.setBackground(ColorUtils.getSolidColorDrawable(new Color(0, 0, 0, 0.7f)));
        countdownContainer.setActor(countdownImage);
        table.add(countdownContainer).fill().expand();
    }

    private void scheduleCountdown() {
        AudioManager.playSound(AudioManager.getInstance().countdown3Sound);
        countdownTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (countdownTime > 0) {
                    Sound sound = countdownTime == 2 ? AudioManager.getInstance().countdown2Sound :
                            AudioManager.getInstance().countdown1Sound;
                    AudioManager.playSound(sound);

                    Sprite sprite = AssetManager.getInstance().getSprite("countdown" + countdownTime);
                    Drawable drawable = new SpriteDrawable(sprite);
                    float width = (sprite.getWidth() / sprite.getHeight()) * COUNTDOWN_IMAGE_HEIGHT;
                    drawable.setMinWidth(width);
                    drawable.setMinHeight(COUNTDOWN_IMAGE_HEIGHT);
                    countdownImage.setDrawable(drawable);

                    countdownTime--;
                } else {
                    countdownImage.setVisible(false);
                    countdownContainer.addAction(Actions.fadeOut(0.5f));
                }
            }
        }, 1, 1, 2);
    }

    private void handleTimeLabel() {
        if (!level.isDone()) {
            int elapsedTime = (int)Math.ceil(level.getElapsedTime()) / 1000;
            timeLabel.setText(String.valueOf(elapsedTime));
        }
    }

    private void startTransition() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (levelState.equals(LevelState.DONE_SUCCESS)) {
                    Screen screen = new SuccessScreen(level);
                    EventManager.getInstance().notify(new ScreenChangeEvent(screen));
                } else {
                    EventManager.getInstance().notify(new StartLevelEvent(level.getId(), true));
                }
            }
        }, ScreenTransition.DEFAULT_DURATION + 0.5f);

        transition.start();
    }

    public void startDash() {
        if (level.isRunning() && isAbilityAvailable) {
            isAbilityAvailable = false;
            dashButton.addAction(Actions.fadeOut(0.5f));
            EventManager.getInstance().notify(new AbilityActivatedEvent());
        }
    }

    private void resetMusicVolume() {
        AudioManager.getInstance().music.setVolume(origMusicVolume);
        AudioManager.getInstance().musicSlow.setVolume(0);
    }
}
