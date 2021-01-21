package com.tendersaucer.asquareastray.object.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class Actions {

    public static ColorAction color (Color color) {
        return color(color, 0, null);
    }

    public static ColorAction color (Color color, float duration) {
        return color(color, duration, null);
    }

    public static ColorAction color (Color color, float duration, Interpolation interpolation) {
        ColorAction action = new ColorAction();
        action.setEndColor(color);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction alpha(float a) {
        return alpha(a, 0, null);
    }

    public static AlphaAction alpha(float a, float duration) {
        return alpha(a, duration, null);
    }

    public static AlphaAction alpha(float a, float duration, Interpolation interpolation) {
        AlphaAction action = new AlphaAction();
        action.setAlpha(a);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction fadeOut(float duration) {
        return alpha(0, duration, null);
    }

    public static AlphaAction fadeOut(float duration, Interpolation interpolation) {
        AlphaAction action = new AlphaAction();
        action.setAlpha(0);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static AlphaAction fadeIn(float duration) {
        return alpha(1, duration, null);
    }

    public static AlphaAction fadeIn(float duration, Interpolation interpolation) {
        AlphaAction action = new AlphaAction();
        action.setAlpha(1);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static ScaleToAction scaleTo(float x, float y) {
        return scaleTo(x, y, 0, null);
    }

    public static ScaleToAction scaleTo(float x, float y, float duration) {
        return scaleTo(x, y, duration, null);
    }

    public static ScaleToAction scaleTo(float x, float y, float duration, Interpolation interpolation) {
        ScaleToAction action = new ScaleToAction();
        action.setScale(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    static public MoveToAction moveTo(float x, float y) {
        return moveTo(x, y, 0, null);
    }

    static public MoveToAction moveTo(float x, float y, float duration) {
        return moveTo(x, y, duration, null);
    }

    static public MoveToAction moveTo(float x, float y, float duration, Interpolation interpolation) {
        MoveToAction action = new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    static public MoveByAction moveBy(float amountX, float amountY) {
        return moveBy(amountX, amountY, 0, null);
    }

    static public MoveByAction moveBy(float amountX, float amountY, float duration) {
        return moveBy(amountX, amountY, duration, null);
    }

    static public MoveByAction moveBy(float amountX, float amountY, float duration, Interpolation interpolation) {
        MoveByAction action = new MoveByAction();
        action.setAmount(amountX, amountY);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    public static DelayAction delay(float duration) {
        DelayAction action = new DelayAction();
        action.setDuration(duration);
        return action;
    }

    public static DelayAction delay(float duration, Action delayedAction) {
        DelayAction action = new DelayAction();
        action.setDuration(duration);
        action.setAction(delayedAction);
        return action;
    }

    public static SequenceAction sequence(Action... actions) {
        return new SequenceAction(actions);
    }

    public static ParallelAction parallel(Action... actions) {
        return new ParallelAction(actions);
    }

    public static RepeatAction repeat(int count, Action repeatedAction) {
        RepeatAction action = new RepeatAction();
        action.setCount(count);
        action.setAction(repeatedAction);
        return action;
    }

    public static RepeatAction forever(Action repeatedAction) {
        RepeatAction action = new RepeatAction();
        action.setCount(RepeatAction.FOREVER);
        action.setAction(repeatedAction);
        return action;
    }

    public static RunnableAction run(Runnable runnable) {
        RunnableAction action = new RunnableAction();
        action.setRunnable(runnable);
        return action;
    }
}
