package lando.systems.ld33.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Sine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.accessors.ColorAccessor;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.SoundManager;

public class ChapterScreen extends LDScreen  {

    private final static float CURTAIN_FADE_IN = 1;
    private final static float CURTAIN_PAUSE = 2;
    private final static float CHAPTER_FADE_IN = 1;
    private final static float CHAPTER_PAUSE = 2;
    private final static float TITLE_FADE_IN = 1;
    private final static float TITLE_PAUSE = 4;
    private final static float FADE_OUT = 2;

    private final static float TITLES_X = Config.width  * 0.25f;
    private final static float TITLES_W = Config.width  * 0.5f;
    private final static float TITLES_Y = Config.height * 0.25f;
    private final static float TITLES_H = Config.height * 0.4166f;

    private final static float CHAPTER_COVER_X = Config.width   * 0.24f;
    private final static float CHAPTER_COVER_W = Config.width   * 0.52f;
    private final static float CHAPTER_COVER_Y = Config.height  * 0.58f;
    private final static float CHAPTER_COVER_H = Config.height  * 0.10f;

    private final static float TITLE_COVER_X = Config.width   * 0.24f;
    private final static float TITLE_COVER_W = Config.width   * 0.52f;
    private final static float TITLE_COVER_Y = TITLES_Y;
    private final static float TITLE_COVER_H = Config.height  * 0.30f;

//    private final static float CHAPTER_COVER_H = Config.height * 0.125f;

    private static final Texture curtain = new Texture("chapters/chapter-curtains.png");
    private Texture titles;

    private static final OrthographicCamera uiCamera = new OrthographicCamera();
    FrameBuffer sceneFrameBuffer;
    TextureRegion sceneRegion;

    private int chapter;
    private float time = 0;
    boolean isComplete = false;

    private float curtainAlpha;
    private float chapterAlpha;
    private float titleAlpha;

    private Color curtainColor;
    private Color chapterCoverColor;
    private Color titleCoverColor;
    private Color coverAllColor;
    private Color promptPulse;

    // -----------------------------------------------------------------------------------------------------------------

    public ChapterScreen(LudumDare33 game, int chapter) {
        super(game);

        this.chapter = chapter;

        uiCamera.setToOrtho(false, Config.width, Config.height);
        sceneFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Config.width, Config.height, false);
        sceneRegion = new TextureRegion(sceneFrameBuffer.getColorBufferTexture());
        sceneRegion.flip(false, true);

        // Show a tile portion of the map
        camera.setToOrtho(false, World.SCREEN_TILES_WIDE, World.SCREEN_TILES_HIGH);
        camera.update();

        if (chapter == 0) {
            Assets.soundManager.playMusic(SoundManager.MusicOptions.MARIO_MAJOR);
            titles = Assets.titleScreenTexture;
            promptPulse = new Color(1f, 0.6f, 0f, 1f);
            Tween.to(promptPulse, ColorAccessor.RGB, 0.33f)
                    .target(1f, 1f, 0f)
                    .repeatYoyo(-1, 0f)
                    .start(LudumDare33.tween);
        } else {
            String titlesAsset = "chapters/chapter-" + String.valueOf(chapter) + ".png";
            titles = new Texture(titlesAsset);
        }

        curtainAlpha = 0;
        chapterAlpha = 0;
        titleAlpha = 0;

        curtainColor = new Color(1, 1, 1, 0);
        Tween.to(curtainColor, ColorAccessor.A, 1.5f)
                .target(1f)
                .ease(Linear.INOUT)
                .delay(0f)
                .start(LudumDare33.tween);

        chapterCoverColor = new Color(1, 1, 1, 1);
        Tween.to(chapterCoverColor, ColorAccessor.A, 1f)
                .target(0f)
                .ease(Sine.INOUT)
                .delay(2f)
                .start(LudumDare33.tween);

        titleCoverColor = new Color(1, 1, 1, 1);
        Tween.to(titleCoverColor, ColorAccessor.A, 2f)
                .target(0f)
                .ease(Sine.INOUT)
                .delay(3f)
                .start(LudumDare33.tween);

        if (chapter != 0) {
            coverAllColor = new Color(1, 1, 1, 0);
            Tween.to(coverAllColor, ColorAccessor.A, 3f)
                 .target(1f)
                 .ease(Sine.INOUT)
                 .delay(6f)
                 .setCallback(new TweenCallback() {
                     @Override
                     public void onEvent(int i, BaseTween<?> baseTween) {
                         isComplete = true;
                     }
                 })
                 .start(LudumDare33.tween);
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void renderChapter(float delta) {
        // Update the time
        time += delta;

        if (chapter == 0) {
            batch.draw(titles, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1f, 1f, 1f, 0.3f);
            batch.draw(Assets.blackTexture, 80f, 0, Gdx.graphics.getWidth() - 160f, 30f);
            batch.setColor(1f, 1f, 1f, 1f);
            Assets.font.setColor(promptPulse);
            Assets.font.draw(batch, "[WHITE]Press[] ENTER", 110f, 150f);
            Assets.font.setColor(0f, 0f, 0f, 1f);
            Assets.font.draw(batch, "Ludum Dare 33 - Jam Entry", 118f, 23f);
            Assets.font.setColor(1f, 1f, 0f, 1f);
            Assets.font.draw(batch, "Ludum Dare 33 - Jam Entry", 115f, 25f);
            Assets.font.setColor(1f, 1f, 1f, 1f);
            return;
        }

        // Draw the curtain
        batch.setColor(curtainColor);
        batch.draw(curtain, 0, 0, Config.width, Config.height);

        // Draw the titles
        batch.setColor(1, 1, 1, 1);
        batch.draw(titles, TITLES_X, TITLES_Y, TITLES_W, TITLES_H);

        // Draw the chapter cover
        batch.setColor(chapterCoverColor);
        batch.draw(Assets.blackTexture, CHAPTER_COVER_X, CHAPTER_COVER_Y, CHAPTER_COVER_W, CHAPTER_COVER_H);

        // Draw the title cover
        batch.setColor(titleCoverColor);
        batch.draw(Assets.blackTexture, TITLE_COVER_X, TITLE_COVER_Y, TITLE_COVER_W, TITLE_COVER_H);

        // Draw the cover all
        batch.setColor(coverAllColor);
        batch.draw(Assets.blackTexture, 0, 0, Config.width, Config.height);

        // Reset
        batch.setColor(1, 1, 1, 1);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void update(float delta) {
        super.update(delta);
        if (chapter == 0 &&
            (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE))) {
            isComplete = true;
        }
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

    @Override
    public boolean isDone() {
        return isComplete;
    }

    // -----------------------------------------------------------------------------------------------------------------

}
