package lando.systems.ld33.entities.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.entities.PlayerGoomba;

/**
 * Created by dsgraham on 8/24/15.
 */
public class MarioSmart extends Mario {
    public MarioSmart(World w, Vector2 pos) {
        this(w, pos, false);
    }

    public MarioSmart(World w, Vector2 pos, boolean large) {
        super(w, pos);
        if(large) {
            growBig();
        }
        dir = .7f;
    }

    public void update(float dt){
        super.update(dt);

        PlayerGoomba player = world.player;
        Rectangle pRect = player.getBounds();


        //Lets keep it simple, only do smart things on the ground
        if (!grounded || moveDelay > 0) return;

        // Kill the PLAYER!!
        if (pRect.contains(new Vector2(bounds.x + .5f + (dir * 4) , bounds.y + .5f))){
            jump();
        }

        // It's a pit
        int startX = (int)(bounds.x +.5f);
        int endX = (int)(bounds.x + .5f + dir);
        int startY = (int)bounds.y -1;
        int endY = startY + 2;
        world.getTiles(startX, startY, endX, endY, tiles);
        if (tiles.size == 0) {
            if(MathUtils.random() < .7f){
                dir *= -1;
            } else {
                jump();
            }
        }


        // It's a wall
        startX = endX = (int)(bounds.x + .5f + (dir));
        startY = (int)bounds.y;
        endY = startY + 2;
        world.getTiles(startX, startY, endX, endY, tiles);
        if (tiles.size >= 1){
            if(MathUtils.random() < .7f){
                dir *= -1;
            } else {
                jump();
            }
        }

    }
}
