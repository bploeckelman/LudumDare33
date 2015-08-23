package lando.systems.ld33.entities.mapobjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/23/2015.
 */
public class SpikeObject extends ObjectBase {

    Animation cleanAnimation;
    Animation usedAnimation;
    float     stateTime;
    boolean   isUsed;

    public SpikeObject(World world, Rectangle bounds) {
        super(world, bounds);
        cleanAnimation = Assets.questionBlockAnimation;
        usedAnimation = Assets.questionBlockAnimation;
        stateTime = 0f;
        isUsed = false;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = (isUsed) ? Assets.deadQuestionBlockRegion //usedAnimation.getKeyFrame(stateTime)
                            : cleanAnimation.getKeyFrame(stateTime);
    }

    @Override
    public void hit() {
        isUsed = true;
        // TODO: launch blood spurt particle effect and kill the player?
    }

}
