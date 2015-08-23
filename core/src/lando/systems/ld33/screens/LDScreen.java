package lando.systems.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.utils.Assets;

/**
 * Brian Ploeckelman created on 8/9/2015.
 */
public abstract class LDScreen extends ScreenAdapter {

    protected final LudumDare33 game;

    protected SpriteBatch        batch;
    protected Vector3            mouseScreenPos;
    protected Vector3            mouseWorldPos;
    protected OrthographicCamera camera;

    public LDScreen(LudumDare33 game) {
        this.game = game;
        batch = Assets.batch;
        mouseScreenPos = new Vector3();
        mouseWorldPos = new Vector3();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Config.width, Config.height);
    }

    public void update(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        camera.update();
        updateMouseVectors(camera);
    }

    // ------------------------------------------------------------------------
    // ScreenAdapter Methods
    // ------------------------------------------------------------------------

    @Override
    public void resize(int width, int height) {
//        camera.viewportWidth  = width;
//        camera.viewportHeight = height;
//        camera.update();
    }

    @Override
    public void pause() {
        disableInput();
    }

    @Override
    public void resume() {
        enableInput();
    }

    public abstract boolean isDone();

    public Vector3 getMouseWorldPos() {
        return mouseWorldPos;
    }

    public Vector3 getMouseScreenPos() {
        return mouseScreenPos;
    }

    // ------------------------------------------------------------------------
    // Protected Implementation
    // ------------------------------------------------------------------------

    protected void updateMouseVectors(Camera camera) {
        float mx = Gdx.input.getX();
        float my = Gdx.input.getY();
        mouseScreenPos.set(mx, my, 0);
        mouseWorldPos.set(mx, my, 0);
        camera.unproject(mouseWorldPos);
    }

    protected void enableInput() {
        final InputMultiplexer mux = new InputMultiplexer();
        // TODO: add input processor(s) here
        Gdx.input.setInputProcessor(mux);
    }

    protected void disableInput() {
        Gdx.input.setInputProcessor(null);
    }

}
