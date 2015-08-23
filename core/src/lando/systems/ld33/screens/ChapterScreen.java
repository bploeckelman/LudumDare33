package lando.systems.ld33.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;

public class ChapterScreen extends LDScreen  {

    private final static float CURTAIN_FADE_IN = 1;
    private final static float CURTAIN_PAUSE = 2;
    private final static float CHAPTER_FADE_IN = 1;
    private final static float CHAPTER_PAUSE = 2;
    private final static float TITLE_FADE_IN = 1;
    private final static float TITLE_PAUSE = 4;
    private final static float FADE_OUT = 2;

    private final static float CHAPTER_COVER_X = Config.width   * 0.24f;
    private final static float CHAPTER_COVER_W = Config.width   * 0.52f;
    private final static float CHAPTER_COVER_Y = Config.height  * 0.58f;
    private final static float CHAPTER_COVER_H = Config.height  * 0.10f;

    private final static float TITLES_X = Config.width  * 0.25f;
    private final static float TITLES_W = Config.width  * 0.5f;
    private final static float TITLES_Y = Config.height * 0.25f;
    private final static float TITLES_H = Config.height * 0.4166f;

//    private final static float CHAPTER_COVER_H = Config.height * 0.125f;

    private static final Texture curtain = new Texture("chapters/chapter-curtains.png");
    private Texture titles;

    private static final OrthographicCamera uiCamera = new OrthographicCamera();
    FrameBuffer sceneFrameBuffer;
    TextureRegion sceneRegion;

    private float time = 0;
    boolean isComplete = false;

    // -----------------------------------------------------------------------------------------------------------------

    public ChapterScreen(LudumDare33 game, int chapter) {

        super(game);

        uiCamera.setToOrtho(false, Config.width, Config.height);
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);

        // Show a tile portion of the map
        camera.setToOrtho(false, World.SCREEN_TILES_WIDE, World.SCREEN_TILES_HIGH);
        camera.update();

        String titlesAsset = "chapters/chapter-" + String.valueOf(chapter) + ".png";
        titles = new Texture(titlesAsset);

    }

    // -----------------------------------------------------------------------------------------------------------------

    private void renderChapter(float delta) {
        // Update the time
        time += delta;

//        if (time < 1f) {
//            // Curtain fade in
//        } else if (time < 3f) {
//            // PAUSE
//        } else if (time < 4f) {
//            // Chapter fade in
//        }

        batch.draw(curtain, 0, 0, Config.width, Config.height);
        batch.draw(titles, TITLES_X, TITLES_Y, TITLES_W, TITLES_H);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(float delta) {

        update(delta);

        sceneFrameBuffer.begin();
        {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            // Draw user interface stuff
            batch.begin();
            batch.setProjectionMatrix(uiCamera.combined);
            renderChapter(delta);
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

    // -----------------------------------------------------------------------------------------------------------------

}
