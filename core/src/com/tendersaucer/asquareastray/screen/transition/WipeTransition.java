package com.tendersaucer.asquareastray.screen.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tendersaucer.asquareastray.AssetManager;

public class WipeTransition extends ScreenTransition {

    private static final int NUM_STRIPS = 10;

    public enum Direction {
        LEFT_UP, RIGHT_UP, UP_RIGHT, DOWN_RIGHT
    }

    private final Direction direction;
    private final Interpolation interpolation;

    public WipeTransition(Stage stage, float duration, Direction direction, Interpolation interpolation) {
        super(stage, duration);

        this.direction = direction;
        this.interpolation = interpolation;
    }

    @Override
    public void start() {
        super.start();

        Sprite sprite = AssetManager.getInstance().getSprite("default");

        float screenWidth = (float)Gdx.graphics.getWidth();
        float screenHeight = (float)Gdx.graphics.getHeight();

        float time = 0.1f;
        float x = 0, y = 0;
        for (int i = 0; i < NUM_STRIPS; i++) {
            Image image = new Image(sprite);
            image.setColor(new Color(0x050505ff));

            switch(direction) {
                case LEFT_UP:
                    image.setSize(screenWidth, screenHeight / NUM_STRIPS);
                    image.setPosition(screenWidth, y);
                    image.addAction(Actions.moveBy(-screenWidth, 0, time, interpolation));
                    y += screenHeight / NUM_STRIPS;
                    break;
                case RIGHT_UP:
                    image.setSize(screenWidth, screenHeight / NUM_STRIPS);
                    image.setPosition(-screenWidth, y);
                    image.addAction(Actions.moveBy(screenWidth, 0, time, interpolation));
                    y += screenHeight / NUM_STRIPS;
                    break;
                case UP_RIGHT:
                    image.setSize(screenWidth / NUM_STRIPS, screenHeight);
                    image.setPosition(x, -screenHeight);
                    image.addAction(Actions.moveBy(0, screenHeight, time, interpolation));
                    x += screenWidth / NUM_STRIPS;
                    break;
                case DOWN_RIGHT:
                    image.setSize(screenWidth / NUM_STRIPS, screenHeight);
                    image.setPosition(x, screenHeight);
                    image.addAction(Actions.moveBy(0, -screenHeight, time, interpolation));
                    x += screenWidth / NUM_STRIPS;
                    break;
            }
            stage.addActor(image);

            time += 0.1f;
        }
    }
}
