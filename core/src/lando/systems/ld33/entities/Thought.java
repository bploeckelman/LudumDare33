package lando.systems.ld33.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld33.utils.Assets;

/**
 * Created by dsgraham on 8/23/15.
 */
public class Thought {

    private String text;
    private Rectangle entityBounds;
    private Rectangle bounds;
    public float timeToLive;

    public Thought(String t, Rectangle e){
        text = t;
        entityBounds = e;
        timeToLive = 3f;


    }

    public void update(float dt){
        timeToLive -= dt;
    }

    public void render(SpriteBatch batch, OrthographicCamera gameCam, OrthographicCamera uiCam){
        final GlyphLayout layout = new GlyphLayout();
        layout.setText(Assets.font8pt, text);
        Vector3 screenCoords = gameCam.project(new Vector3(entityBounds.x + entityBounds.width / 2 , entityBounds.y + (entityBounds.height), 0));
        Vector3 worldCoords = uiCam.unproject(screenCoords);
        bounds = new Rectangle(worldCoords.x - ((layout.width + 10 )/2), uiCam.viewportHeight - worldCoords.y, (layout.width) + 20, (layout.height) + 20);

        float alpha = Math.min(1, timeToLive * 2);
        batch.setColor(1,1,1,alpha);
        Assets.thoughtBubble.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
        Assets.font8pt.setColor(0,0,0,alpha);

        //Assets.font8pt.getData().setScale(1);
        Assets.font8pt.draw(batch, text, bounds.x + 10, bounds.y + layout.height + 15);
        Assets.font8pt.setColor(Color.WHITE);
        batch.setColor(Color.WHITE);
    }
}
