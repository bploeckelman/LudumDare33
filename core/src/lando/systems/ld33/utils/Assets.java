package lando.systems.ld33.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld33.entities.QuestionBlock;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class Assets {

    public static SpriteBatch batch;

    public static BitmapFont  font;
    public static BitmapFont  font8pt;
    public static BitmapFont  font16pt;
    public static BitmapFont  font32pt;

    public static Texture testTexture;
    public static Texture marioTilesetTexture;

    public static Animation questionBlockAnimation;

    public static void load() {
        batch = new SpriteBatch();

        font8pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-8pt.fnt"));
        font16pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-16pt.fnt"));
        font32pt = new BitmapFont(Gdx.files.internal("fonts/emulogic-32pt.fnt"));
        font8pt.getData().markupEnabled = true;
        font16pt.getData().markupEnabled = true;
        font32pt.getData().markupEnabled = true;

        // Set the default font
        font = font16pt;

        testTexture = new Texture("badlogic.jpg");
        marioTilesetTexture = new Texture("maps/mario-tileset.png");

        TextureRegion[][] tilesetRegions = TextureRegion.split(marioTilesetTexture, 16, 16);

        questionBlockAnimation = new Animation(QuestionBlock.FRAME_DURATION,
                                               tilesetRegions[23][0],
                                               tilesetRegions[24][0],
                                               tilesetRegions[25][0]);
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
