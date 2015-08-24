package lando.systems.ld33.entities.mapobjects;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.entities.MarioDumb;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.SoundManager;

public class TubeObject extends ObjectBase {

    public enum TubeContents {
        large (Assets.tubeMarioLargeAnimation),
        small (Assets.tubeMarioSmallAnimation),
        embryo (Assets.tubeMarioEmbryoAnimation);

        Animation animation;
        TubeContents(Animation animation) {
            this.animation = animation;
        }
    }

    Animation tubeAnimation;
    Animation contentsAnimation;
    Animation explodingAnimation = Assets.tubeExplosionAnimation;
    TextureRegion contentsKeyframe;
    TextureRegion bg = Assets.tubeBg;
    TextureRegion broken = Assets.tubeBroken;
    float stateTime;
    float touchDebounceTime = 3f;
    float touchDebounce;
    float explodingTime = .65f;
    TubeContents contents;
    boolean hasContents;
    boolean isBroken = false;
    boolean isExploding = false;

    public TubeObject(World world, Rectangle bounds, TubeContents contents) {
        super(world, bounds);
        tubeAnimation = Assets.tubeEmptyAnimation;
        if(contents != null) {
            contentsAnimation = contents.animation;
            this.contents = contents;
            hasContents = true;
        } else {
            hasContents = false;
        }
        stateTime = MathUtils.random(1f);
        touchDebounce = 0f;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        if(touchDebounce > 0f) {
            touchDebounce -= delta;
        }
        else if(touchDebounce < 0f) {
            touchDebounce = 0f;
        }

        if(isExploding) {
            keyframe = explodingAnimation.getKeyFrame(stateTime);
            return;
        }

        keyframe = tubeAnimation.getKeyFrame(stateTime);
        if(hasContents) {
            contentsKeyframe = contentsAnimation.getKeyFrame(stateTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(isBroken) {
            batch.draw(broken, bounds.x, bounds.y, bounds.width, bounds.height);

            if(isExploding) {
                batch.draw(keyframe, bounds.x - 2, bounds.y - 1, bounds.width + 4, bounds.height + 2);
            }

            return;
        }

        batch.draw(bg, bounds.x, bounds.y, bounds.width, bounds.height);
        if(hasContents) {
            batch.draw(contentsKeyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void hit(int addScore) {

    }

    @Override
    public void touch() {
        if(touchDebounce > 0) {
            return;
        }

        touchDebounce = touchDebounceTime;
        if(
            !isBroken &&
            (contents == TubeContents.small || contents == TubeContents.large) &&
            MathUtils.random() >= .5f)
        {
            Assets.soundManager.playSound(SoundManager.SoundOptions.GLASS_JAR_BREAK);
            isExploding = true;
            stateTime = 0f;
            isBroken = true;
            Tween.call(new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    isExploding = false;

                    new MarioDumb(world, new Vector2(bounds.x + 1, bounds.y),
                         contents == TubeContents.large);
                }
            })
            .delay(.65f)
            .start(LudumDare33.tween);
        }
    }
}
