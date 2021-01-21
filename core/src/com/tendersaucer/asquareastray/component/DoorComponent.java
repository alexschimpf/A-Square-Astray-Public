package com.tendersaucer.asquareastray.component;

import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.asquareastray.AudioManager;
import com.tendersaucer.asquareastray.animation.AnimatedImage;
import com.tendersaucer.asquareastray.event.EventManager;
import com.tendersaucer.asquareastray.level.CameraHandler;
import com.tendersaucer.asquareastray.level.Level;
import com.tendersaucer.asquareastray.object.GameObject;
import com.tendersaucer.asquareastray.event.FinalUnlockEvent;
import com.tendersaucer.asquareastray.event.TimeScaleEvent;
import com.tendersaucer.asquareastray.event.UnlockEvent;
import com.tendersaucer.asquareastray.event.listener.IUnlockListener;
import com.tendersaucer.asquareastray.object.Properties;
import com.tendersaucer.asquareastray.utils.Tween;

public class DoorComponent extends Component implements IUnlockListener {

    private int numUnlocksLeft;

    public DoorComponent(Level level, GameObject parent, Properties properties) {
        super(level, parent, properties);

        numUnlocksLeft = properties.getInt("num_unlocks_required");

        parent.setLayer(GameObject.DEFAULT_LAYER - 1);
    }

    @Override
    public void init() {
        EventManager.getInstance().listen(UnlockEvent.class, this);

        Properties properties = new Properties();
        properties.put("colors", "1,1,1,1 0.35,0.35,0.35,1");
        properties.put("durations", "750,750");
        parent.addComponent(new ColorTweenComponent(level, parent, properties));

        AnimationComponent animationComponent =
                (AnimationComponent)parent.getComponentByType(AnimationComponent.class);
        animationComponent.getAnimation().setIgnoreTimeScale(true);
    }

    @Override
    public void destroy() {
        super.destroy();

        EventManager.getInstance().mute(UnlockEvent.class, this);
    }

    @Override
    public void onUnlock(String doorName) {
        if (parent.getName().equals(doorName)) {
            if (--numUnlocksLeft <= 0) {
                // TODO: Play sound
                final AnimatedImage animation =
                        ((AnimationComponent)parent.getComponentByType(AnimationComponent.class)).getAnimation();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        animation.setState(AnimatedImage.State.PLAYING);
                    }
                }, CameraHandler.MOVE_TO_TARGET_DURATION / 1000.0f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        parent.setDone();
                    }
                }, (CameraHandler.MOVE_TO_TARGET_DURATION + animation.getTotalDuration()) / 1000.0f);

                AudioManager.playSound(AudioManager.getInstance().unlockFinalSound);
                EventManager.getInstance().postNotify(new TimeScaleEvent(new Tween(0, 0, 0.01f)));
                EventManager.getInstance().postNotify(new FinalUnlockEvent(doorName));
            } else {
                AudioManager.playSound(AudioManager.getInstance().unlockSound);
            }
        }
    }
}
