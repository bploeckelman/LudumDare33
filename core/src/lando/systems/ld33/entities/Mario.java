package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/22/15.
 */
public class Mario extends EntityBase {

    protected int dir;


    public Mario(World w, Vector2 pos) {
        super(w);
        bounds = new Rectangle(pos.x, pos.y, 1, 1);
        makeSmall();
        dir = 1;
    }

    public void jump(){
        if (!grounded) return;
        velocity.y = jumpVelocity;
    }

    public void growBig(){
        walkingAnimation = Assets.marioBigWalkAnimation;
        bounds.height = 2;
    }

    public void makeSmall(){
        walkingAnimation = Assets.marioSmallWalkAnimation;
        jumpingAnimation = Assets.marioSmallWalkAnimation;
        standingAnimation = Assets.marioSmallWalkAnimation;
        bounds.height = 1;
    }

    protected void hitBlockFromBelow(ObjectBase obj){
        obj.hit();
    }

    public void update(float dt){
        super.update(dt);
        velocity.x = 8 * dir;

        velocity.x = 8 * dir;

        facesRight = dir > 0;

        Rectangle intersectRect = new Rectangle();
        for (int i = 0; i < world.gameEntities.size; i ++){
            EntityBase entity = world.gameEntities.get(i);
            if (entity == this) continue;
            if (Intersector.intersectRectangles(bounds, entity.getBounds(), intersectRect)){
                if (entity instanceof MushroomItem) {
                    growBig();
                    entity.dead = true;
                }
                if (entity instanceof PlayerGoomba){
                    if (intersectRect.width > intersectRect.height){
                        velocity.y = jumpVelocity;
                        entity.stomped();
                    }
                }
            }
        }
    }
}
