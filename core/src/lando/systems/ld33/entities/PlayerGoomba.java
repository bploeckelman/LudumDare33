package lando.systems.ld33.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import lando.systems.ld33.World;

/**
 * Created by dsgraham on 8/22/15.
 */
public class PlayerGoomba extends EntityBase {
    public PlayerGoomba(World w, Texture tex, Rectangle r) {
        super(w, tex, r);
    }

    @Override
    public void update(float dt){
        super.update(dt);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && grounded) {
            velocity.y += jumpVelocity;
            state = State.Jumping;
            grounded = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = - maxVelocity;
            if (grounded) state = State.Walking;
            facesRight = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = maxVelocity;
            if (grounded) state = State.Walking;
            facesRight = true;
        }
    }
}
