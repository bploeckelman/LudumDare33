package lando.systems.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class PrototypeScreen extends LDScreen {

    OrthographicCamera         uiCamera;
    FrameBuffer                sceneFrameBuffer;
    TextureRegion              sceneRegion;
    World                      world;


    public PrototypeScreen(LudumDare33 game, World.Phase worldPhase) {
        super(game);
        Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1f);

        world = new World(camera, worldPhase, batch);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Config.width, Config.height);
        sceneFrameBuffer = new FrameBuffer(Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);


        // Show a tile portion of the map
        camera.setToOrtho(false, world.SCREEN_TILES_WIDE, world.SCREEN_TILES_HIGH);
        camera.update();


    }

    @Override
    public void update(float delta) {
        world.update(delta);
        super.update(delta);
        enableInput();
    }

    @Override
    public void render(float delta) {

        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            world.render(batch);

            // Draw user interface stuff
            batch.begin();
            batch.setProjectionMatrix(uiCamera.combined);
            world.renderUI(batch);
            // NOTE: we can fit 41 characters across the screen using the default 16pt font
//            Assets.font.draw(batch, "This... is... GOOMBA!", 0, uiCamera.viewportHeight);
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

    @Override
    public boolean isDone() {
        return world.done;
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    @Override
    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        // TODO: add any other input processors here as needed
        mux.addProcessor(world.dialogue);
        Gdx.input.setInputProcessor(mux);
    }

    @Override
    protected void disableInput() {
        Gdx.input.setInputProcessor(null);
    }

}
