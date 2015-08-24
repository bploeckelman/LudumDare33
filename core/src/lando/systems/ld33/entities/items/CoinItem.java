package lando.systems.ld33.entities.items;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.SoundManager;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class CoinItem extends ItemEntity {

    private static final float BOUNCE_HEIGHT = 4.0f;

    public boolean bouncer;

    public CoinItem(final World world, float px, float py) {
        this(world, px, py, true);
    }

    public CoinItem(final World world, float px, float py, boolean bouncer) {
        super(world, px, py);
        walkingAnimation = smashedAnimation = jumpingAnimation = standingAnimation = Assets.coinAnimation;
        this.type = ItemType.COIN;
        this.bouncer = bouncer;
        if (bouncer) {
            drawOnTop = true;
            bounds.y += 1f;
            Tween.to(bounds, RectangleAccessor.Y, 0.3f)
                 .target(bounds.y + BOUNCE_HEIGHT)
                 .repeatYoyo(1, 0f)
                 .ease(Linear.INOUT)
                 .setCallback(new TweenCallback() {
                     @Override
                     public void onEvent(int i, BaseTween<?> baseTween) {
                         CoinItem.this.dead = true;
                     }
                 })
                 .start(LudumDare33.tween);
        }
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

}
