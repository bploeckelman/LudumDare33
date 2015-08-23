package lando.systems.ld33.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class QuestionBlock extends ObjectBase {

    public static final float FRAME_DURATION = 0.105f;
    public static final float BOUNCE_OFFSET  = 0.4f;

    Animation           animation;
    float               stateTime;
    boolean             isUsed;
    ItemEntity.ItemType spawnType;

    public QuestionBlock(World world, Rectangle bounds, ItemEntity.ItemType spawnType) {
        super(world, bounds);
        animation = Assets.questionBlockAnimation;
        stateTime = 0f;
        isUsed = false;
        this.spawnType = spawnType;
    }

    @Override
    public void update(float delta) {
        if (!isUsed) {
            stateTime += delta;
            keyframe = animation.getKeyFrame(stateTime);
        }
    }

    @Override
    public void hit() {
        if (isUsed) {
            return;
        } else {
            isUsed = true;
        }

        // Kick out an item of type spawnType
        ItemEntity item;
        switch (spawnType) {
            default:
            case COIN:       item = new CoinItem(world, bounds.x, bounds.y); break;
            case MUSHROOM:   item = new MushroomItem(world, bounds.x, bounds.y); break;
            case FIREFLOWER: item = new FireflowerItem(world, bounds.x, bounds.y); break;
            case STAR:       item = new MushroomItem(world, bounds.x, bounds.y); break;
        }
        world.gameEntities.add(item);

        // Bounce this question block
        Tween.to(bounds, RectangleAccessor.XY, 0.175f)
                 .target(bounds.x, bounds.y + BOUNCE_OFFSET)
                .repeatYoyo(1, 0f)
                .ease(Linear.INOUT)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        // Change the keyframe to 'dead block' after tween finishes
                        keyframe = Assets.deadQuestionBlockRegion;
                    }
                })
                .start(LudumDare33.tween);
    }

}
