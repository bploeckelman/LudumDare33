package lando.systems.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/22/15.
 */
public class PlayerGoomba extends EntityBase {

    public boolean canJump;
    public boolean canRight;

    public PlayerGoomba(World w, Vector2 p) {
        super(w);
        bounds = new Rectangle(p.x, p.y, 1, 1);
        canJump = true;
        canRight = true;
    }

    @Override
    public void update(float dt){
        super.update(dt);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && canJump && grounded) {
            velocity.y += jumpVelocity;
            state = State.Jumping;
            grounded = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = - maxVelocity;
            if (grounded) state = State.Walking;
            facesRight = false;
        }

        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) && canRight) {
            velocity.x = maxVelocity;
            if (grounded) state = State.Walking;
            facesRight = true;
        }

        velocity.x *= damping;

        // Keep in bounds
        bounds.x = Math.max(0, Math.min(world.gameWidth - bounds.width, bounds.x));
    }
}
