package lando.systems.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.entities.items.MushroomItem;
import lando.systems.ld33.entities.mapobjects.ObjectBase;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/22/15.
 */
public class PlayerGoomba extends EntityBase {

    public boolean canJump;
    public boolean canRight;


    public boolean raged = false;

    public PlayerGoomba(World w, Vector2 p) {
        super(w);

        bounds = new Rectangle(p.x, p.y, 1, 1);
        setNormalMode();
    }

    protected void hitBlockFromBelow(ObjectBase obj){
        obj.hit();
    }

    public void setNormalMode() {
        walkingAnimation = Assets.goombaNormalWalkAnimation;
        smashedAnimation = Assets.goombaSmashedAnimation;
        standingAnimation = Assets.goombaNormalStandingAnimation;
        jumpingAnimation = Assets.goombaNormalWalkAnimation;
    }

    public void setWounded(){
        walkingAnimation = Assets.goombaHurtAnimation;
        standingAnimation = Assets.goombaHurtStandingAnimation;
    }

    public void setSadMode(){
        walkingAnimation = Assets.goombaSadWalkAnimation;
        standingAnimation = jumpingAnimation = Assets.goombaSadStandingAnimation;
    }

    public void setRageMode() {
        raged = true;
        canJump = true;
        canRight = true;
        walkingAnimation = Assets.goombaWalkAnimation;
        standingAnimation = Assets.goombaWalkAnimation;
        smashedAnimation = Assets.goombaWalkAnimation;
        jumpingAnimation = Assets.goombaWalkAnimation;
    }

    public void stomped() {
        state = State.Smashed;
        moveDelay = 3;
    }



    @Override
    public void update(float dt){

        super.update(dt);


        if (moveDelay <= 0 && world.allowPolling()) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && canJump && grounded) {
                velocity.y += jumpVelocity;
                state = State.Jumping;
                grounded = false;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocity.x = -maxVelocity;
                if (grounded) state = State.Walking;
                facesRight = false;
            }

            if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && canRight) {
                velocity.x = maxVelocity;
                if (grounded) state = State.Walking;
                facesRight = true;
            }
        } else if (state != State.Smashed) {
            state = State.Standing;
        }

        Rectangle intersectRect = new Rectangle();
        for (int i = 0; i < world.gameEntities.size; i ++){
            EntityBase entity = world.gameEntities.get(i);
            if (entity == this) continue;
            if (Intersector.intersectRectangles(bounds, entity.getBounds(), intersectRect)){
                if (entity instanceof MushroomItem) {
                    setRageMode();
                    entity.dead = true;
                }
            }
        }

        velocity.x *= damping;

        // Keep in bounds
        if (moveDelay < 0)
            bounds.x = Math.max(0, Math.min(world.gameWidth - bounds.width, bounds.x));


    }
}
