package lando.systems.ld33.entities.mapobjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

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
    TextureRegion contentsKeyframe;
    TextureRegion bg = Assets.tubeBg;
    float stateTime;
    boolean hasContents;

    public TubeObject(World world, Rectangle bounds, TubeContents contents) {
        super(world, bounds);
        tubeAnimation = Assets.tubeEmptyAnimation;
        if(contents != null) {
            contentsAnimation = contents.animation;
            hasContents = true;
        } else {
            hasContents = false;
        }
        stateTime = MathUtils.random(1f);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = tubeAnimation.getKeyFrame(stateTime);
        if(hasContents) {
            contentsKeyframe = contentsAnimation.getKeyFrame(stateTime);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(bg, bounds.x, bounds.y, bounds.width, bounds.height);
        if(hasContents) {
            batch.draw(contentsKeyframe, bounds.x, bounds.y, bounds.width, bounds.height);
        }
        batch.draw(keyframe, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void hit() {

    }
}
