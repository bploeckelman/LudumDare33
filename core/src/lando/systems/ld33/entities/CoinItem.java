package lando.systems.ld33.entities;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class CoinItem extends ItemEntity {

    private static final float BOUNCE_HEIGHT = 4.0f;

    Animation animation;
    float     stateTime;
    boolean   isUsed;

    public CoinItem(final World world, float px, float py) {
        super(world, px, py);
        animation = Assets.coinAnimation;
        stateTime = 0f;
        isUsed = false;
        type = ItemType.COIN;

        Tween.to(bounds, RectangleAccessor.Y, 0.3f)
                .target(py + BOUNCE_HEIGHT)
                .repeatYoyo(1, 0f)
                .ease(Linear.INOUT)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        isUsed = true;
                        world.gameEntities.removeValue(CoinItem.this, true);
                    }
                })
                .start(LudumDare33.tween);
    }

    @Override
    public void update(float delta) {
        if (!isUsed) {
            stateTime += delta;
            keyframe = animation.getKeyFrame(stateTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (isUsed) return;
        super.render(batch);
    }

}
