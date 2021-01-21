package com.tendersaucer.asquareastray.screen.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.tendersaucer.asquareastray.utils.ColorUtils;
import com.tendersaucer.asquareastray.utils.RandomUtils;

public class TileScreenTransition extends ScreenTransition {

    private static final int GRID_WIDTH = 10;

    public TileScreenTransition(Stage stage, float duration) {
        super(stage, duration);
    }

    @Override
    public void start() {
        super.start();

        int numSquares = TileScreenTransition.GRID_WIDTH * TileScreenTransition.GRID_WIDTH;

        float width = Gdx.graphics.getWidth() / (float)GRID_WIDTH;
        float height = Gdx.graphics.getHeight() / (float)GRID_WIDTH;
        for (int i = 0; i < TileScreenTransition.GRID_WIDTH; i++) {
            float y = i * height;

            for (int j = 0; j < TileScreenTransition.GRID_WIDTH; j++) {
                float x = j * width;

                Image image = new Image(ColorUtils.getSolidColorDrawable(new Color(0x010101ff)));
                image.setPosition(x, y);

                float delay = (duration / numSquares) * RandomUtils.pickFromRange(0, numSquares);
                image.getColor().a = 0;
                image.addAction(Actions.sequence(
                    Actions.delay(delay),
                    Actions.fadeIn(0.5f)
                ));
                image.setSize(width, height);

                stage.addActor(image);
            }
        }
    }
}
