package lando.systems.ld33.entities.mario;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.entities.EntityBase;
import lando.systems.ld33.entities.PlayerGoomba;
import lando.systems.ld33.entities.items.CoinItem;
import lando.systems.ld33.entities.items.MushroomItem;
import lando.systems.ld33.entities.mapobjects.ObjectBase;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.SoundManager;

/**
 * Created by dsgraham on 8/22/15.
 */
public class Mario extends EntityBase {

    protected float dir;


    public Mario(World w, Vector2 pos) {
        super(w);
        bounds = new Rectangle(pos.x, pos.y, 1, 1);
        makeSmall();
        dir = 1;
    }

    public void jump(){
        if (!grounded) return;
        velocity.y = jumpVelocity;
        Vector2 dist = new Vector2(bounds.x - world.player.getBounds().x, bounds.y - world.player.getBounds().y);
        Assets.soundManager.playSound3D(SoundManager.SoundOptions.MARIO_JUMP, dist);
    }

    public void setDeadAnimations(){
        walkingAnimation = jumpingAnimation = standingAnimation = smashedAnimation = Assets.marioSmallDieAnimation;
    }

    public void growBig(){
        Assets.soundManager.playSound(SoundManager.SoundOptions.MUSHROOM_GET);
        walkingAnimation = Assets.marioBigWalkAnimation;
        jumpingAnimation = Assets.marioBigJumpingAnimation;
        standingAnimation = Assets.marioBigStandingAnimation;
        bounds.height = 2;
    }

    public void makeSmall(){
        walkingAnimation = Assets.marioSmallWalkAnimation;
        jumpingAnimation = Assets.marioSmallJumpingAnimation;
        standingAnimation = Assets.marioSmallStandingAnimation;
        bounds.height = 1;
    }

    protected void hitBlockFromBelow(ObjectBase obj){
        obj.hit(1);
    }

    public void update(float dt){
        super.update(dt);
        if (moveDelay > 0 ) return;
        velocity.x = 8 * dir;

        velocity.x = 8 * dir;

        facesRight = dir > 0;

        Rectangle intersectRect = new Rectangle();
        for (int i = 0; i < world.gameEntities.size; i ++){
            EntityBase entity = world.gameEntities.get(i);
            if (entity == this) continue;
            if (entity.immuneTime > 0) continue;
            if (Intersector.intersectRectangles(bounds, entity.getBounds(), intersectRect)){
                if (entity instanceof MushroomItem) {
                    world.addScore(250);
                    growBig();
                    entity.dead = true;
                }
                else if (entity instanceof CoinItem) {
                    if (!((CoinItem) entity).bouncer) {
                        Vector2 dist = new Vector2(bounds.x - world.player.getBounds().x, bounds.y - world.player.getBounds().y);
                        world.addCoin(1, dist);
                        entity.dead = true;
                    }
                }
                else if (entity instanceof PlayerGoomba){
                    if (velocity.y < 0 && intersectRect.width > intersectRect.height){
                        velocity.y = jumpVelocity;
                        entity.stomped();
                    } else {
                        stomped();
                    }
                }
            }
        }
    }

    public void stomped(){
        drawOnTop = true;
        world.addScore(-300);
        standingAnimation = jumpingAnimation = walkingAnimation = Assets.marioSmallDieAnimation;
        moveDelay = 2f;
        Assets.soundManager.playSound(SoundManager.SoundOptions.MARIO_DEATH);
        Tween.to(bounds, RectangleAccessor.Y, 1f)
                .target(-2)
                .ease(Back.IN)
                .start(LudumDare33.tween);
    }
}
