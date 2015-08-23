package lando.systems.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.dialogue.Dialogue;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class NarrativeTestScreen extends LDScreen {

    private static final float BLUE_SKY_R        = 107f / 255f;
    private static final float BLUE_SKY_G        = 140f / 255f;
    private static final float BLUE_SKY_B        = 255f / 255f;


    OrthographicCamera         uiCamera;
    FrameBuffer                sceneFrameBuffer;
    TextureRegion              sceneRegion;
    World                      world;

    Dialogue dialogue = new Dialogue();

    public NarrativeTestScreen(LudumDare33 game) {
        super(game);

        world = new World(camera, World.Phase.First, Assets.batch);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Config.width, Config.height);
        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);


        // Show a tile portion of the map
        camera.setToOrtho(false, world.SCREEN_TILES_WIDE, world.SCREEN_TILES_HIGH);
        camera.update();


        Gdx.gl.glClearColor(BLUE_SKY_R, BLUE_SKY_G, BLUE_SKY_B, 1f);

        Array<String> messages = new Array<String>();
//        messages.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum faucibus commodo sapien id interdum. Donec justo ipsum, commodo vehicula aliquet et, semper dignissim erat. Aliquam venenatis purus et purus sodales luctus. Donec tristique lectus ut justo volutpat, at vestibulum diam vestibulum. Vestibulum elementum metus est, vitae ultrices est accumsan nec. Suspendisse volutpat, enim quis bibendum tincidunt, nulla dui porttitor nibh, sit amet iaculis purus eros non leo. Maecenas libero mauris, pharetra eget ornare quis, dictum ut velit. Pellentesque id eros posuere, iaculis ante posuere, vulputate orci. Sed feugiat interdum massa, vel pellentesque leo accumsan et. Pellentesque sed leo enim.");
        messages.add("12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345 12345");
        messages.add("A second, longer message.");
//        dialogue = ;
        dialogue.show(1, 10, 18, 4, messages);
//        dialogue.show();

        enableInput();

    }

    @Override
    public void update(float delta) {

        world.update(delta);
        super.update(delta);
    }

    @Override
    public void render(float delta) {
        update(delta);

        dialogue.update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            world.render(batch);

            // Draw user interface stuff
            batch.begin();
            batch.setProjectionMatrix(uiCamera.combined);
            // NOTE: we can fit 41 characters across the screen using the default 16pt font
//            Assets.font.draw(batch, "This... is... GOOMBA!", 0, uiCamera.viewportHeight);

            dialogue.render(batch);

            batch.end();


        }
        sceneFrameBuffer.end();

        // TODO: add default screen shader
        batch.setShader(null);
        batch.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.setProjectionMatrix(uiCamera.combined);
            batch.draw(sceneRegion, 0, 0);
            batch.end();
        }
    }

    // ------------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------------



    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    @Override
    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        // TODO: add any other input processors here as needed
        mux.addProcessor(dialogue);
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    protected void disableInput() {
        Gdx.input.setInputProcessor(null);
    }

}