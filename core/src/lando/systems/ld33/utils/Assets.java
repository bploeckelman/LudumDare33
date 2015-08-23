package lando.systems.ld33.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld33.entities.QuestionBlock;

import java.util.Arrays;

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
    public static Texture blackTexture;
    public static Texture whiteTexture;
    public static Texture marioTilesetTexture;

    public static TextureRegion testTextureRegion;
    public static TextureRegion deadQuestionBlockRegion;
    public static TextureRegion[][] mushrooms;
    public static TextureRegion bigMushroom;

    public static Animation questionBlockAnimation;
    public static Animation marioSmallWalkAnimation;
    public static Animation marioSmallStandingAnimation;
    public static Animation marioSmallJumpingAnimation;
    public static Animation marioBigWalkAnimation;
    public static Animation marioBigStandingAnimation;
    public static Animation marioBigJumpingAnimation;
    public static Animation marioTubeAnimation;
    public static Animation goombaNormalWalkAnimation;
    public static Animation goombaNormalStandingAnimation;
    public static Animation goombaSadWalkAnimation;
    public static Animation goombaSadStandingAnimation;
    public static Animation goombaWalkAnimation;
    public static Animation goombaSmashedAnimation;
    public static Animation goombaWifeAnimation;
    public static Animation goombaKidsAnimation;
    public static Animation goombaHurtAnimation;
    public static Animation goombaHurtStandingAnimation;
    public static Animation coinAnimation;
    public static Animation fireFlowerAnimation;
    public static Animation starAnimation;
    public static Animation mushroomAnimation;

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
        blackTexture = new Texture("black.png");
        whiteTexture = new Texture("white.png");
        testTextureRegion = new TextureRegion(testTexture, testTexture.getWidth(), testTexture.getHeight());
        marioTilesetTexture = new Texture("maps/mario-tileset.png");

        TextureRegion[][] tilesetRegions = TextureRegion.split(marioTilesetTexture, 16, 16);

        mushrooms = atlas.findRegion("mushrooms").split(16, 16);
        bigMushroom = mushrooms[0][0];

        questionBlockAnimation = new Animation(QuestionBlock.FRAME_DURATION,
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][24],
                                               tilesetRegions[0][25],
                                               tilesetRegions[0][26]);
        questionBlockAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        deadQuestionBlockRegion = new TextureRegion(tilesetRegions[0][27]);

        marioSmallWalkAnimation = new Animation(.15f,
            atlas.findRegion("mario-small-walk1"),
            atlas.findRegion("mario-small-walk2"),
            atlas.findRegion("mario-small-walk3"));
        marioSmallWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marioSmallStandingAnimation = new Animation(.15f, atlas.findRegion("mario-small-straight-on"));
        marioSmallJumpingAnimation = new Animation(.15f, atlas.findRegion("mario-small-jump"));

        marioBigWalkAnimation = new Animation(.15f,
                atlas.findRegion("mario-big-walk1"),
                atlas.findRegion("mario-big-walk2"),
                atlas.findRegion("mario-big-walk3"));
        marioBigWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marioBigStandingAnimation = new Animation(.15f, atlas.findRegion("mario-big-straight-on"));
        marioBigJumpingAnimation = new Animation(.15f, atlas.findRegion("mario-big-jump"));

        marioTubeAnimation = new Animation(.15f,
            atlas.findRegion("mario-tube").split(16, 32)[0]);
        marioTubeAnimation.setPlayMode(Animation.PlayMode.LOOP);

        goombaNormalWalkAnimation = new Animation(.15f,
            atlas.findRegion("goomba-normal-walk1"),
            atlas.findRegion("goomba-normal-walk2"));
        goombaNormalWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        goombaNormalStandingAnimation = new Animation(.15f, atlas.findRegion("goomba-straight-on"));

        goombaSadWalkAnimation = new Animation(.15f,
            atlas.findRegion("goomba-sad-walk").split(16, 16)[0]);
        goombaSadWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        goombaSadStandingAnimation = new Animation(.15f, atlas.findRegion("goomba-sad-straight-on"));

        goombaWalkAnimation = new Animation(.15f,
            atlas.findRegion("goomba-walk1"),
            atlas.findRegion("goomba-walk2"));
        goombaWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        goombaSmashedAnimation  = new Animation(.15f,
                atlas.findRegion("goomba-squished"));
        goombaWifeAnimation = new Animation(.15f,
            atlas.findRegion("goomba-wife").split(16, 16)[0]);
        goombaWifeAnimation.setPlayMode(Animation.PlayMode.LOOP);
        goombaKidsAnimation = new Animation(.15f,
            atlas.findRegion("goomba-kids").split(32, 32)[0]);
        goombaKidsAnimation.setPlayMode(Animation.PlayMode.LOOP);

        goombaHurtAnimation = new Animation(.15f,
            atlas.findRegion("goomba-hurt").split(16, 16)[0]);
        goombaHurtAnimation.setPlayMode(Animation.PlayMode.LOOP);
        goombaHurtStandingAnimation = new Animation(.15f,
            atlas.findRegion("goomba-hurt-straight-on"));

        coinAnimation = new Animation(.03f,
            Arrays.copyOfRange(atlas.findRegion("coin").split(16, 16)[0], 1, 4));
        coinAnimation.setPlayMode(Animation.PlayMode.LOOP);

        mushroomAnimation = new Animation(.15f, bigMushroom);

        fireFlowerAnimation = new Animation(.15f,
            atlas.findRegion("fire-flower").split(16, 16)[0]);
        fireFlowerAnimation.setPlayMode(Animation.PlayMode.LOOP);
        starAnimation = new Animation(.15f,
            atlas.findRegion("star").split(16, 16)[0]);
        starAnimation.setPlayMode(Animation.PlayMode.LOOP);

    }

    public static void dispose() {
        batch.dispose();
        font8pt.dispose();
        font16pt.dispose();
        font32pt.dispose();
        testTexture.dispose();
        marioTilesetTexture.dispose();
        atlas.dispose();
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
