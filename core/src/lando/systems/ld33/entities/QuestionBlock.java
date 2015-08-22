package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class QuestionBlock extends ObjectBase {

    public static final float FRAME_DURATION = 0.105f;

    Animation           animation;
    float               stateTime;
    ItemEntity.ItemType spawnType;

    public QuestionBlock(Rectangle bounds, ItemEntity.ItemType spawnType) {
        super(bounds);
        animation = Assets.questionBlockAnimation;
        stateTime = 0f;
        this.spawnType = spawnType;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = animation.getKeyFrame(stateTime);
    }

}
