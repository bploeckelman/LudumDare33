package lando.systems.ld33.entities.mapobjects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

public class MarioScreenObject extends ObjectBase {
    Animation animation;
    float stateTime;

    public MarioScreenObject(World world, Rectangle bounds) {
        super(world, bounds);

        animation = Assets.marioScreenAnimation;
        stateTime = 0f;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        keyframe = animation.getKeyFrame(stateTime);
    }

    @Override
    public void hit() {

    }
}
