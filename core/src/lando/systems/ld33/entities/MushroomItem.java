package lando.systems.ld33.entities;

import com.badlogic.gdx.math.Vector2;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class MushroomItem extends ItemEntity {

    public MushroomItem(World w, Vector2 p) {
        super(w, p);
        setKeyframe();
    }

    public MushroomItem(World w, float px, float py) {
        super(w, px, py);
        setKeyframe();
    }

    private void setKeyframe() {
        keyframe = Assets.bigMushroom;
    }

}
