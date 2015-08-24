package lando.systems.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld33.World;
import lando.systems.ld33.entities.items.MushroomItem;
import lando.systems.ld33.entities.mapobjects.ObjectBase;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.GameText;
import lando.systems.ld33.utils.SoundManager;

/**
 * Created by dsgraham on 8/22/15.
 */
public class PlayerGoomba extends EntityBase {

    public boolean canJump;
    public boolean canRight;


    public boolean raged = false;
    private Array<Vector2> lastSafePos;

    public PlayerGoomba(World w, Vector2 p) {
        super(w);
        lastSafePos = new Array<Vector2>();
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

    public void setCaped(){
        walkingAnimation = standingAnimation = jumpingAnimation = smashedAnimation = Assets.goombaCloak;
        bounds.height = 2;
        bounds.width = 2;
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
        standingAnimation = Assets.goombaStandingAnimation;
        smashedAnimation = Assets.goombaSmashedAnimation;
        jumpingAnimation = Assets.goombaWalkAnimation;
    }

    public void stomped() {
        state = State.Smashed;
        moveDelay = 3;
        addThought("OOF");
        Assets.soundManager.playSound(SoundManager.SoundOptions.GOOMBA_SQUASH);
    }

    public void respawn(){
        Vector2 safe = lastSafePos.first();
        bounds.x = safe.x;
        bounds.y = safe.y;
        velocity.x = 0;
        velocity.y = 0;
        dead = false;
        Array<String> messages = new Array<String>();
        messages.add(GameText.getText("respawn"));
        world.dialogue.show(1,10,18,4,messages,true,600);
    }

    @Override
    public void update(float dt){
        if (grounded) {
            lastSafePos.add(new Vector2(bounds.x, bounds.y));
            if (lastSafePos.size > 10){
                lastSafePos.removeIndex(0);
            }
        }
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
