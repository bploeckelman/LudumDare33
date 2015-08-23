package lando.systems.ld33.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld33.entities.QuestionBlock;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class Assets {

    public static SpriteBatch batch;
    public static TextureAtlas atlas;

    public static BitmapFont  font;
    public static BitmapFont  font8pt;
    public static BitmapFont  font16pt;
    public static BitmapFont  font32pt;

    public static Texture testTexture;
    public static TextureRegion testTextureRegion;
    public static Texture marioTilesetTexture;

    public static Animation questionBlockAnimation;
    public static Animation marioSmallWalkAnimation;
    public static Animation goombaNormalWalkAnimation;
    public static Animation goombaWalkAnimation;

    public static void load() {
        batch = new SpriteBatch();
        atlas = new TextureAtlas(Gdx.files.internal("sprites.atlas"));

        font8pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-8pt.fnt"));
        font16pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-16pt.fnt"));
        font32pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-32pt.fnt"));
        font8pt.getData().markupEnabled = true;
        font16pt.getData().markupEnabled = true;
        font32pt.getData().markupEnabled = true;

        // Set the default font
        font = font16pt;

        testTexture = new Texture("badlogic.jpg");
        testTextureRegion = new TextureRegion(testTexture, testTexture.getWidth(), testTexture.getHeight());
        marioTilesetTexture = new Texture("maps/mario-tileset.png");

        TextureRegion[][] tilesetRegions = TextureRegion.split(marioTilesetTexture, 16, 16);

        questionBlockAnimation = new Animation(QuestionBlock.FRAME_DURATION,
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][25],
                                               tilesetRegions[0][26]);
        questionBlockAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        marioSmallWalkAnimation = new Animation(.15f,
            atlas.findRegion("mario-small-walk1"),
            atlas.findRegion("mario-small-walk2"),
            atlas.findRegion("mario-small-walk3"));
        marioSmallWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        goombaNormalWalkAnimation = new Animation(.15f,
            atlas.findRegion("goomba-normal-walk1"),
            atlas.findRegion("goomba-normal-walk2"));
        goombaNormalWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        goombaWalkAnimation = new Animation(.15f,
            atlas.findRegion("goomba-walk1"),
            atlas.findRegion("goomba-walk2"));
        goombaWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

    }

    public static void dispose() {
        batch.dispose();
        font8pt.dispose();
        font16pt.dispose();
        font32pt.dispose();
        testTexture.dispose();
        marioTilesetTexture.dispose();
    }

    private static ShaderProgram compileShaderProgram(FileHandle vertSource, FileHandle fragSource) {
        ShaderProgram.pedantic = false;
        final ShaderProgram shader = new ShaderProgram(vertSource, fragSource);
        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Failed to compile shader program:\n" + shader.getLog());
        }
        else if (shader.getLog().length() > 0) {
            Gdx.app.debug("SHADER", "ShaderProgram compilation log:\n" + shader.getLog());
        }
        return shader;
    }

}
