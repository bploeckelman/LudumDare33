package lando.systems.ld33.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld33.entities.mapobjects.QuestionBlock;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class Assets {

    public static SoundManager soundManager;
    public static SpriteBatch  batch;
    public static TextureAtlas atlas;

    public static BitmapFont font;
    public static BitmapFont font8pt;
    public static BitmapFont font16pt;
    public static BitmapFont font32pt;

    public static Texture testTexture;
    public static Texture blackTexture;
    public static Texture whiteTexture;
    public static Texture marioTilesetTexture;
    public static Texture titleScreenTexture;

    public static TextureRegion     testTextureRegion;
    public static TextureRegion     deadQuestionBlockRegion;
    public static TextureRegion[][] mushrooms;
    public static TextureRegion     bigMushroom;
    public static TextureRegion     tubeBg;
    public static TextureRegion     tubeBroken;
    public static NinePatch         thoughtBubble;
    public static TextureRegion     staticCoin;

    public static Animation questionBlockAnimation;
    public static Animation spikesDownAnimation;
    public static Animation spikesDownBloodyAnimation;
    public static Animation marioSmallWalkAnimation;
    public static Animation marioSmallStandingAnimation;
    public static Animation marioSmallJumpingAnimation;
    public static Animation marioBigWalkAnimation;
    public static Animation marioBigStandingAnimation;
    public static Animation marioBigJumpingAnimation;
    public static Animation marioSmallDieAnimation;
    public static Animation tubeEmptyAnimation;
    public static Animation tubeMarioSmallAnimation;
    public static Animation tubeMarioEmbryoAnimation;
    public static Animation tubeMarioLargeAnimation;
    public static Animation tubeExplosionAnimation;
    public static Animation marioScreenAnimation;
    public static Animation goombaNormalWalkAnimation;
    public static Animation goombaNormalStandingAnimation;
    public static Animation goombaSadWalkAnimation;
    public static Animation goombaSadStandingAnimation;
    public static Animation goombaWalkAnimation;
    public static Animation goombaStandingAnimation;
    public static Animation goombaSmashedAnimation;
    public static Animation goombaGrowAnimation;
    public static Animation goombaWifeAnimation;
    public static Animation goombaKidsAnimation;
    public static Animation goombaHurtAnimation;
    public static Animation goombaHurtStandingAnimation;
    public static Animation goombaCloakEmpty;
    public static Animation goombaCloak;
    public static Animation coinAnimation;
    public static Animation fireFlowerAnimation;
    public static Animation starAnimation;
    public static Animation mushroomAnimation;
    public static Animation motherBrainAnimation;
    public static Animation kingHippoAnimation;
    public static Animation ganonAnimation;
    public static Animation draculaAnimation;
    public static Animation luigiAnimation;
    public static Animation drWilyAnimation;

    public static final float BLUE_SKY_R = 107f / 255f;
    public static final float BLUE_SKY_G = 140f / 255f;
    public static final float BLUE_SKY_B = 255f / 255f;

    public static final float NIGHT_SKY_R = BLUE_SKY_R / 2;
    public static final float NIGHT_SKY_G = BLUE_SKY_G / 2;
    public static final float NIGHT_SKY_B = BLUE_SKY_B / 2;

    public static final float UNDERGROUND_R = 32f / 255f;
    public static final float UNDERGROUND_G = 104f / 255f;
    public static final float UNDERGROUND_B = 120f / 255f;

    public static void load() {
        batch = new SpriteBatch();
        GameText.load();
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
        titleScreenTexture = new Texture("title-screen.png");

        thoughtBubble = new NinePatch(atlas.findRegion("thought"), 4, 5, 4, 9);
        //thoughtBubble.scale(1/16f, 1/16f);

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

        spikesDownAnimation = new Animation(.15f,
            atlas.findRegion("spikes-down").split(16, 16)[0]);
        spikesDownAnimation.setPlayMode(Animation.PlayMode.LOOP);
        spikesDownBloodyAnimation = new Animation(.15f,
            atlas.findRegion("spikes-down").split(16, 16)[1]);
        spikesDownBloodyAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marioSmallWalkAnimation = new Animation(.15f,
            atlas.findRegion("mario-small-walk1"),
            atlas.findRegion("mario-small-walk2"),
            atlas.findRegion("mario-small-walk3"));
        marioSmallWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marioSmallStandingAnimation = new Animation(.15f, atlas.findRegion("mario-small-straight-on"));
        marioSmallJumpingAnimation = new Animation(.15f, atlas.findRegion("mario-small-jump"));
        marioSmallDieAnimation = new Animation(.15f, atlas.findRegion("mario-small-die"));

        marioBigWalkAnimation = new Animation(.15f,
                atlas.findRegion("mario-big-walk1"),
                atlas.findRegion("mario-big-walk2"),
                atlas.findRegion("mario-big-walk3"));
        marioBigWalkAnimation.setPlayMode(Animation.PlayMode.LOOP);

        marioBigStandingAnimation = new Animation(.15f, atlas.findRegion("mario-big-straight-on"));
        marioBigJumpingAnimation = new Animation(.15f, atlas.findRegion("mario-big-jump"));

        tubeEmptyAnimation = new Animation(.15f,
            atlas.findRegion("tube-empty").split(16, 32)[0]);
        tubeEmptyAnimation.setPlayMode(Animation.PlayMode.LOOP);
        tubeMarioSmallAnimation = new Animation(.15f,
            atlas.findRegion("tube-mario-small").split(16, 32)[0]);
        tubeMarioSmallAnimation.setPlayMode(Animation.PlayMode.LOOP);
        tubeMarioEmbryoAnimation = new Animation(.15f,
            atlas.findRegion("tube-mario-embryo").split(16, 32)[0]);
        tubeMarioEmbryoAnimation.setPlayMode(Animation.PlayMode.LOOP);
        tubeMarioLargeAnimation = new Animation(.15f,
            atlas.findRegion("tube-mario-large").split(16, 32)[0]);
        tubeMarioLargeAnimation.setPlayMode(Animation.PlayMode.LOOP);
        TextureRegion[][] scifiFx = atlas.findRegion("scifi-fx").split(32, 32);
        tubeExplosionAnimation = new Animation(.05f,
            scifiFx[0][3],
            scifiFx[0][2],
            scifiFx[0][5],
            scifiFx[0][4],
            scifiFx[9][3],
            scifiFx[9][2],
            scifiFx[1][0],
            scifiFx[1][1],
            scifiFx[1][6],
            scifiFx[1][7],
            scifiFx[2][5],
            scifiFx[10][6],
            scifiFx[10][7]);
        tubeExplosionAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        tubeBg = atlas.findRegion("tube-bg");
        tubeBroken = atlas.findRegion("tube-broken");

        marioScreenAnimation = new Animation(.5f,
            atlas.findRegion("mario-screen").split(32, 32)[0]);
        marioScreenAnimation.setPlayMode(Animation.PlayMode.LOOP);

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
        goombaStandingAnimation = new Animation(.15f, atlas.findRegion("goomba-straight-rage"));

        goombaSmashedAnimation  = new Animation(.15f,
                atlas.findRegion("goomba-squished"));
        goombaGrowAnimation = new Animation(.8f,
            atlas.findRegion("goomba-sad-straight-on"),
            atlas.findRegion("goomba-straight-high"),
            atlas.findRegion("goomba-straight-rage"));
        goombaGrowAnimation.setPlayMode(Animation.PlayMode.NORMAL);
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

        goombaCloakEmpty = new Animation(.15f, atlas.findRegion("goomba-cloak-empty"));
        goombaCloak = new Animation(.15f, atlas.findRegion("goomba-cloak"));

        TextureRegion[] coinFrames = atlas.findRegion("coin").split(16, 16)[0];

        coinAnimation = new Animation(.2f,
            coinFrames[0], coinFrames[1], coinFrames[2], coinFrames[3], coinFrames[4]);
        coinAnimation.setPlayMode(Animation.PlayMode.LOOP);

        staticCoin = coinFrames[0];

        mushroomAnimation = new Animation(.15f, bigMushroom);

        fireFlowerAnimation = new Animation(.15f,
            atlas.findRegion("fire-flower").split(16, 16)[0]);
        fireFlowerAnimation.setPlayMode(Animation.PlayMode.LOOP);
        starAnimation = new Animation(.15f,
            atlas.findRegion("star").split(16, 16)[0]);
        starAnimation.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion motherBrainRegion = atlas.findRegion("mother-brain");
        TextureRegion kingHippoRegion   = atlas.findRegion("king-hippo");
        TextureRegion ganonRegion       = atlas.findRegion("ganon");
        TextureRegion draculaRegion     = atlas.findRegion("dracula");
        TextureRegion luigiRegion       = atlas.findRegion("luigi");
        TextureRegion drWilyRegion      = atlas.findRegion("dr-wily");
        draculaRegion.flip(true, false);
        luigiRegion.flip(true, false);
        drWilyRegion.flip(true, false);

        motherBrainAnimation = new Animation(0.15f, motherBrainRegion);
        kingHippoAnimation   = new Animation(0.15f, kingHippoRegion);
        ganonAnimation       = new Animation(0.15f, ganonRegion);
        draculaAnimation     = new Animation(0.15f, draculaRegion);
        luigiAnimation       = new Animation(0.15f, luigiRegion);
        drWilyAnimation      = new Animation(0.15f, drWilyRegion);

        soundManager = new SoundManager();

    }

    public static void dispose() {
        batch.dispose();
        font8pt.dispose();
        font16pt.dispose();
        font32pt.dispose();
        testTexture.dispose();
        marioTilesetTexture.dispose();
        titleScreenTexture.dispose();
        atlas.dispose();
        soundManager.dispose();
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
