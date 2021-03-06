package lando.systems.ld33;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.*;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld33.accessors.CameraAccessor;
import lando.systems.ld33.accessors.ColorAccessor;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.dialogue.Dialogue;
import lando.systems.ld33.entities.*;
import lando.systems.ld33.entities.items.CoinItem;
import lando.systems.ld33.entities.items.ItemEntity;
import lando.systems.ld33.entities.mapobjects.*;
import lando.systems.ld33.entities.mario.Mario;
import lando.systems.ld33.entities.mario.MarioAI;
import lando.systems.ld33.entities.mario.MarioDumb;
import lando.systems.ld33.entities.mario.MarioSmart;
import lando.systems.ld33.utils.*;

import java.util.Iterator;

/**
 * Created by dsgraham on 8/22/15.
 */
public class World {

    public static final float MAP_UNIT_SCALE    = 1f / 16f;
    public static final int   SCREEN_TILES_WIDE = 20;
    public static final int   SCREEN_TILES_HIGH = 15;
    public static final int   PIXELS_PER_TILE   = Config.width / SCREEN_TILES_WIDE;

    public enum Phase {
        DAY_ONE,
        HEADING_HOME,
        MEET_THE_WIFE,
        LEAVING_HOME,
        BACK_TO_WORK,
        HEADING_HOME_SAD,
        EMPTY_HOUSE,
        GET_MUSHROOM,
        OVERWORLD_FIRST,
        UNDERWORLD,
        SHROOM_LAND,
        BRIDGES_TO_FACTORY,
        INTO_THE_FACTORY,
        DEEP_FACTORY,
        CULT_ROOM
    }

    public ParticleManager            particles;
    public TiledMapTileLayer          foregroundLayer;
    public TiledMapTileLayer          backgroundLayer;
    public TiledMap                   map;
    public OrthogonalTiledMapRenderer mapRenderer;
    public Array<Rectangle>           tileRects;
    public Pool<Rectangle>            rectPool;
    public Array<ObjectBase>          mapObjects;
    public OrthographicCamera         camera;
    public Array<EntityBase>          gameEntities;
    public PlayerGoomba               player;
    public WifeGoomba                 wife;
    public GoombaKids                 kids;
    public float                      cameraLeftEdge;
    public float                      cameraRightEdge;
    public float                      gameWidth;
    public Phase                      phase;
    public int                        segment;
    public boolean                    done;
    public Dialogue                   dialogue;
    public Color                      transitionColor;
    public boolean                    cameraLock;
    public SpriteBatch                batch;
    public float                      endDelay;
    public Ganon                      ganon;
    public KingHippo                  kingHippo;
    public MotherBrain                motherBrain;
    public Dracula                    dracula;
    public Luigi                      luigi;
    public DrWily                     drWily;
    public Cape                       cape;
    public Tween                      repeatingTween;
    public Tween                      capeFloatingTween;
    public Mario fallingMario;
    public Shake                      shake;
    public Vector2                    cameraCenter;
    public Score                      score;
    public float                      fireworkDelay;
    public MarioAI                    marioAI;

    public boolean drawEndCurtain;
    public boolean endCurtainAnimationComplete;
    public MutableFloat endCurtainBottomY;
    public MutableFloat endCurtainFullY;

    public static final float END_CURTAIN_BOTTOM_HEIGHT = 40f;
    public static final int END_CURTAIN_RUFFLES = 3;
    public static final float END_CURTAIN_RUFFLE_OFFSET = 32f;
    public static final float END_CURTAIN_BOTTOM_RUFFLE_END_Y = -8f;
    public Array<MutableFloat> endCurtainPositions = new Array<MutableFloat>();
    public int endCurtainCompleteRuffleCount = 0;


    public World(OrthographicCamera cam, Phase p, SpriteBatch batch) {
        this.batch = batch;
        shake = new Shake();
        particles = new ParticleManager();
        phase = p;
        endDelay = 0;
        done = false;
        cameraLock = true;
        dialogue = new Dialogue();
        transitionColor = new Color(1,1,1,1);
        drawEndCurtain = false;

        gameEntities = new Array<EntityBase>();
        camera = cam;

        initPhase();

        tileRects = new Array<Rectangle>();
        rectPool = Pools.get(Rectangle.class);
        cameraCenter = new Vector2(camera.position.x, 7.5f);

    }

    public void loadMap(String mapName){
        final TmxMapLoader mapLoader = new TmxMapLoader();

        map = mapLoader.load(mapName);
        loadMapObjects();

        mapRenderer = new OrthogonalTiledMapRenderer(map, MAP_UNIT_SCALE, batch);

        foregroundLayer = (TiledMapTileLayer) map.getLayers().get("foreground");
        backgroundLayer = (TiledMapTileLayer) map.getLayers().get("background");

        gameWidth = backgroundLayer.getWidth();
        cameraLeftEdge = SCREEN_TILES_WIDE / 2;
        cameraRightEdge = gameWidth - cameraLeftEdge;
    }

    public void update(float dt) {
        dialogue.update(dt);
        particles.update(dt);
        if (!dialogue.isActive()) {
            if (score != null) score.update(dt);
        }
            Iterator<EntityBase> iterator = gameEntities.iterator();
            while (iterator.hasNext()) {
                EntityBase entity = iterator.next();
                if (!(dialogue.isActive() && (entity instanceof Mario)))
                    entity.update(dt);
                if (entity.dead) {
                    if (entity == player) {
                        player.respawn();
                    } else {
                        iterator.remove();
                    }
                }
            }


        for (ObjectBase object : mapObjects) {
            object.update(dt);
        }


        handlePhaseUpdate(dt);

        // keep the map in view always
        if (cameraLock) {
            float playerX = player.getBounds().x;
            if (playerX < cameraCenter.x - 3) cameraCenter.x = playerX + 3;
            if (playerX > cameraCenter.x + 2) cameraCenter.x = playerX - 2;


            cameraCenter.x = Math.min(cameraRightEdge, Math.max(cameraLeftEdge, cameraCenter.x));
            camera.position.x = cameraCenter.x;
            //camera.position.y = cameraCenter.y;
        }
        camera.update();
        shake.update(dt, camera, cameraCenter);


    }

    public void render(SpriteBatch batch){
        mapRenderer.setView(camera);

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        {
            mapRenderer.renderTileLayer(backgroundLayer);

//            player.render(batch);
            for (EntityBase entity : gameEntities) {
                entity.render(batch);
            }
            for (ObjectBase object : mapObjects) {
                object.render(batch);
            }

            particles.render(batch);

            mapRenderer.renderTileLayer(foregroundLayer);
            for (EntityBase entity : gameEntities){
                if (entity.drawOnTop)
                    entity.render(batch);
            }
        }
        batch.end();

    }

    public void renderUI(SpriteBatch batch, OrthographicCamera uiCam) {
        if (cameraLock && score != null){
            score.render(batch, uiCam);
        }

        // Speach bubbles
        for (EntityBase entity : gameEntities){
            entity.renderUI(batch, camera, uiCam);
        }

        // End game curtain
        if (drawEndCurtain) {
//            float thisCurtainY;
//            for (int i = 0; i < END_CURTAIN_RUFFLES; i++) {
//
//                // What's our target Y?
//                if (i >= endCurtainCompleteRuffleCount) {
//                    // We're still attached to the stack of ruffles.
//                    thisCurtainY = endCurtainPositions.get(i).floatValue() + (END_CURTAIN_RUFFLE_OFFSET * (i - endCurtainCompleteRuffleCount));
//                } else {
//                    // Read only our own position
//                    thisCurtainY = endCurtainPositions.get(i).floatValue();
//                }
//
//                batch.draw(Assets.endCurtainBottomTexture, 0, thisCurtainY, Config.width, END_CURTAIN_BOTTOM_HEIGHT);
//                if (i == (END_CURTAIN_RUFFLES - 1)) {
//                    // Last ruffle - draw the rest of the curtain.
//                    batch.draw(Assets.endCurtainFullTexture, 0, thisCurtainY + 40f, Config.width, Config.height);
//                }
//            }
            batch.draw(Assets.endCurtainBottomTexture, 0, endCurtainBottomY.floatValue(), Config.width, END_CURTAIN_BOTTOM_HEIGHT);
            batch.draw(Assets.endCurtainBottomTexture, 0, endCurtainFullY.floatValue(), Config.width, END_CURTAIN_BOTTOM_HEIGHT);
            batch.draw(Assets.endCurtainFullTexture, 0, endCurtainFullY.floatValue() + 40f, Config.width, Config.height);

        }

        dialogue.render(batch);

        //TODO this is debug
//        Assets.font.draw(batch, "X:" + (int)player.getBounds().x + " Y:" + (int) player.getBounds().y, 32, 32);

        batch.setColor(transitionColor);
        batch.draw(Assets.whiteTexture, 0, 0, uiCam.viewportWidth, uiCam.viewportHeight);
        batch.setColor(Color.WHITE);
    }

    public void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        if (startX > endX){
            int t = startX;
            startX = endX;
            endX = t;
        }
        if (startY > endY){
            int t = startY;
            startY = endY;
            endY = t;
        }
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = foregroundLayer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    public Array<ObjectBase> getObjects(){
        return mapObjects;
    }

    public boolean allowPolling(){
        return !dialogue.isActive();
    }

    public void addScore(int s){
        if (score != null) score.addScore(s);
    }

    public void addCoin(int c, Vector2 dist){
        if (score != null) score.addCoin(c);
        Assets.soundManager.playSound3D(SoundManager.SoundOptions.COIN_GET, dist);
    }

    public void doShake(float time){
        shake.shake(time);
    }

    // ------------------------------------------------------------------------
    // Private Implementation
    // ------------------------------------------------------------------------

    /**
     * Performs the initial setup for each phase.
     * - Load the appropriate map
     * - Spawn entities
     * - Get entities in their places
     * - Kick off any dialog
     * - ???
     * - Profit
     */
    private void initPhase() {
        segment = 0;
        fadeIn();

        switch (phase) {
            case DAY_ONE:
                score = new Score("1-1");

                loadMap("maps/level1.tmx");
                cameraLock = false;


                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y + 1f)
                        .start(LudumDare33.tween);

                float initY = camera.position.y;
                float initZoom = camera.zoom;
                camera.position.x = player.getBounds().x + .5f;
                camera.position.y = player.getBounds().y + .5f;
                camera.zoom = .1f;
                camera.update();
                Tween.to(camera, CameraAccessor.XYZ, EntityBase.PIPEDELAY)
                        .target(cameraRightEdge, initY, initZoom)
                        .ease(Linear.INOUT)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                cameraLock = true;
                            }
                        })
                        .start(LudumDare33.tween);

                Array<String> messages = new Array<String>();
                messages.add(GameText.getText("foremanLate"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case HEADING_HOME:
                Gdx.gl.glClearColor(Assets.NIGHT_SKY_R, Assets.NIGHT_SKY_G, Assets.NIGHT_SKY_B, 1f);
                loadMap("maps/enterhome.tmx");

                player = new PlayerGoomba(this, new Vector2(17, 2));
                player.canJump = false;
                player.canRight = false;
                player.setWounded();
                player.moveDelay = EntityBase.PIPEDELAY;

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("playerLate"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case MEET_THE_WIFE:
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f);

                loadMap("maps/inhome-bedroom.tmx");

                player = new PlayerGoomba(this, new Vector2(20, 2));
                player.canJump = false;
                player.canRight = false;
                player.setWounded();
                player.moveDelay = EntityBase.PIPEDELAY;
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                        .target(player.getBounds().x - 1f)
                        .ease(Linear.INOUT)
                        .start(LudumDare33.tween);


                wife = new WifeGoomba(this, new Vector2(9, 2));
                wife.moveDelay = 1f;
                Tween.to(wife.getBounds(), RectangleAccessor.Y, 0.2f)
                        .target(wife.getBounds().y + 0.5f)
                        .repeatYoyo(3, 0f)
                        .ease(Linear.INOUT)
                        .start(LudumDare33.tween);

                kids = new GoombaKids(this, new Vector2(15, 2));
                Tween.to(kids.getBounds(), RectangleAccessor.X, 2f)
                        .target(18)
                        .ease(Linear.INOUT)
                        .start(LudumDare33.tween);
                kids.addThought("Daddy! Daddy!");

                messages = new Array<String>();
                messages.add(GameText.getText("wifeBitching"));
                messages.add(GameText.getText("playerStammer"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case LEAVING_HOME:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1f);

                loadMap("maps/exithome.tmx");

                player = new PlayerGoomba(this, new Vector2(11, 2));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setSadMode();
                player.maxVelocity = 3f;
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);
                break;
            case BACK_TO_WORK:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1f);
                score = new Score("1-1");
                score.reset();

                loadMap("maps/level1.tmx");

                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setSadMode();

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y + 1f)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("impressBoss"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case HEADING_HOME_SAD:
                Gdx.gl.glClearColor(Assets.NIGHT_SKY_R, Assets.NIGHT_SKY_G, Assets.NIGHT_SKY_B, 1f);
                loadMap("maps/enterhome.tmx");

                player = new PlayerGoomba(this, new Vector2(17, 2));
                player.canJump = false;
                player.canRight = false;
                player.setWounded();
                player.moveDelay = EntityBase.PIPEDELAY;

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                           .start(LudumDare33.tween);
                break;
            case EMPTY_HOUSE:
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f);

                loadMap("maps/inhome-bedroom.tmx");

                player = new PlayerGoomba(this, new Vector2(20, 2));
                player.canJump = false;
                player.canRight = false;
                player.setWounded();
                player.moveDelay = EntityBase.PIPEDELAY;
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                        .ease(Linear.INOUT)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("ellipses"));
                messages.add(GameText.getText("notComingBack"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case GET_MUSHROOM:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1);
                loadMap("maps/level1.tmx");
                score = new Score("1-1");
                score.reset();

                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setSadMode();

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y + 1f)
                        .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("foremanLate"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case INTO_THE_FACTORY:
                Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
                score = new Score("F-B");
                loadMap("maps/level-factoryintro.tmx");
                Assets.soundManager.playMusic(SoundManager.MusicOptions.ZELDA_BK);
                player = new PlayerGoomba(this, new Vector2(97, 2));
                player.setRageMode();
                player.moveDelay = EntityBase.PIPEDELAY;
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                        .ease(Linear.INOUT)
                        .start(LudumDare33.tween);
                messages = new Array<String>();
                messages.add(GameText.getText("intoFactory"));
                messages.add(GameText.getText("factoryNoise"));
                dialogue.show(1, 10, 18, 4, messages);
                fallingMario = new Mario(this, new Vector2(0,0));
                fallingMario.dead = true;
                break;
            case DEEP_FACTORY:
                Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);
                score = new Score("F-A");
                loadMap("maps/level-factory.tmx");

                player = new PlayerGoomba(this, new Vector2(97, 2));
                player.setRageMode();
                player.moveDelay = EntityBase.PIPEDELAY;
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);


                break;
            case CULT_ROOM:
                Gdx.gl.glClearColor(220f / 255f, 20f / 255f, 60f / 255f, 1f);

                Assets.soundManager.playMusic(SoundManager.MusicOptions.METRIOD_BK);
                loadMap("maps/cadreroom.tmx");

                // Spawn cultist members
                ganon       = new Ganon(this, new Vector2(0.5f, 2));
                kingHippo   = new KingHippo(this, new Vector2(2.5f, 2));
                motherBrain = new MotherBrain(this, new Vector2(6, 2));
                dracula     = new Dracula(this, new Vector2(13, 2));
                dracula.facesRight = true;
                luigi       = new Luigi(this, new Vector2(16, 2));
                luigi.facesRight = true;
                drWily      = new DrWily(this, new Vector2(18, 2));
                drWily.facesRight = true;
                cape        = new Cape(this, new Vector2(8, 2));

                capeFloatingTween = Tween.to(cape.getBounds(), RectangleAccessor.Y, 1f)
                                        .target(4)
                                        .ease(Sine.INOUT)
                                        .repeatYoyo(-1, 0)
                                        .start(LudumDare33.tween);
                cape.moveDelay = 100000;

                ganon.addThought("LUDUM DARE");
                kingHippo.addThought("LUDUM DARE");
                motherBrain.addThought("LUDUM DARE");
                dracula.addThought("LUDUM DARE");
                luigi.addThought("LUDUM DARE");
                drWily.addThought("LUDUM DARE");

                repeatingTween = Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                ganon.addThought("LUDUM DARE");
                                kingHippo.addThought("LUDUM DARE");
                                motherBrain.addThought("LUDUM DARE");
                                dracula.addThought("LUDUM DARE");
                                luigi.addThought("LUDUM DARE");
                                drWily.addThought("LUDUM DARE");
                            }
                        })
                     .delay(3.5f)
                     .repeat(-1, 3.5f)
                     .start(LudumDare33.tween);

                player = new PlayerGoomba(this, new Vector2(17.5f, 7f));
                player.setRageMode();
                player.canJump = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y - 1f)
                     .setCallback(new TweenCallback() {
                         @Override
                         public void onEvent(int i, BaseTween<?> baseTween) {
                             Array<String> messages = new Array<String>();
                             messages.add(GameText.getText("cultFlavor"));
                             messages.add(GameText.getText("cultEnter"));
                             dialogue.show(1, 10, 18, 4, messages);
                         }
                     })
                     .start(LudumDare33.tween);
                break;
            case OVERWORLD_FIRST:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R - 0.1f, Assets.BLUE_SKY_G - 0.1f, Assets.BLUE_SKY_B - 0.1f, 1f);
                score = new Score("F-E");

                loadMap("maps/level2.tmx");
                Assets.soundManager.playMusic(SoundManager.MusicOptions.MARIO_MAJOR_BK);

                new MarioSmart(this, new Vector2(75f, 3f));
                new MarioSmart(this, new Vector2(47f, 3f));
                new MarioSmart(this, new Vector2(17f, 10f));
                new MarioSmart(this, new Vector2(22, 3));
                new MarioSmart(this, new Vector2(3f, 3f));

                player = new PlayerGoomba(this, new Vector2(97f, 2f));
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setRageMode();

                // Pipe enter
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
                     .setCallback(new TweenCallback() {
                         @Override
                         public void onEvent(int i, BaseTween<?> baseTween) {
                             Array<String> messages = new Array<String>();
                             messages.add(GameText.getText("level3Intro"));
                             dialogue.show(1, 10, 18, 4, messages);
                         }
                     })
                     .start(LudumDare33.tween);
                break;
            case UNDERWORLD:
                Gdx.gl.glClearColor(Assets.UNDERGROUND_R, Assets.UNDERGROUND_G, Assets.UNDERGROUND_B, 1f);
                score = new Score("F-F");

                loadMap("maps/level3.tmx");

                Assets.soundManager.playMusic(SoundManager.MusicOptions.DNUORGREDNU);

                player = new PlayerGoomba(this, new Vector2(97.5f, 7f));
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setRageMode();

                TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y - 1f)
                     .setCallback(new TweenCallback() {
                         @Override
                         public void onEvent(int i, BaseTween<?> baseTween) {
                             Array<String> messages = new Array<String>();
                             messages.add(GameText.getText("level2Intro"));
                             dialogue.show(1, 10, 18, 4, messages);
                         }
                     })
                     .start(LudumDare33.tween);
                break;
            case SHROOM_LAND:
                Gdx.gl.glClearColor(Assets.NIGHT_SKY_R + 0.2f, Assets.NIGHT_SKY_G + 0.2f, Assets.NIGHT_SKY_B + 0.2f, 1f);

                score = new Score("F-D");

                loadMap("maps/level4.tmx");

                Assets.soundManager.playMusic(SoundManager.MusicOptions.MARIO_MAJOR_BK);

                new MarioSmart(this, new Vector2(72, 7));
                new MarioSmart(this, new Vector2(34, 10));
                new MarioSmart(this, new Vector2(4, 5));

                new MarioDumb(this, new Vector2(67, 6));
                new MarioDumb(this, new Vector2(43, 5));


                player = new PlayerGoomba(this, new Vector2(97f, 2f));
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setRageMode();
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                           .setCallback(new TweenCallback() {
                               @Override
                               public void onEvent(int i, BaseTween<?> baseTween) {
                                   Array<String> messages = new Array<String>();
                                   messages.add(GameText.getText("level4Intro"));
                                   dialogue.show(1, 10, 18, 4, messages);
                               }
                           })
                           .start(LudumDare33.tween);
                break;
            case BRIDGES_TO_FACTORY:
                Gdx.gl.glClearColor(Assets.NIGHT_SKY_R, Assets.NIGHT_SKY_G, Assets.NIGHT_SKY_B, 1f);

                score = new Score("F-C");

                loadMap("maps/level5.tmx");

                Assets.soundManager.playMusic(SoundManager.MusicOptions.ZELDA_BK);

                new MarioDumb(this, new Vector2(56, 5));
                new MarioDumb(this, new Vector2(35, 8));
                new MarioDumb(this, new Vector2(10, 2));

                new MarioSmart(this, new Vector2(76, 5));

                fireworkDelay = 0;
                player = new PlayerGoomba(this, new Vector2(97f, 2f));
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setRageMode();
                TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                           .setCallback(new TweenCallback() {
                               @Override
                               public void onEvent(int i, BaseTween<?> baseTween) {
                                   Array<String> messages = new Array<String>();
                                   messages.add(GameText.getText("level5Intro"));
                                   dialogue.show(1, 10, 18, 4, messages);
                               }
                           })
                           .start(LudumDare33.tween);
                break;
        }
    }

    /**
     * Progress through each phase one scripted segment at a time
     * @param dt the Fucking Delta what else? Intellij Complaining
     */
    private void handlePhaseUpdate(float dt){
        switch (phase){
            case DAY_ONE:
                switch (segment){
                    case 0:
                        // Get in position, release a mario
                        if (player.getBounds().x < 27){
                            player.getBounds().x = 27;
                            segment++;
                            player.moveDelay = 6;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("hereComesMario"));
                            dialogue.show(1,10,18,4,messages);

                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()) {
                            segment++;
                            marioAI = new MarioAI(this, new Vector2(10, 2));
                            player.moveDelay = 6f;
                        }
                        break;
                    case 2:
                        // Just a bump on the head, released to go home for the day
                        if (marioAI.dead){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("headHome"));
                            dialogue.show(1, 10, 18, 4, messages);
                            player.setWounded();
                        }
                        break;
                    case 3:
                        if (!dialogue.isActive()) {
                            segment++;
                            player.addThought("Good idea");
                        }
                        break;
                    case 4:
                        // Enter home pipe
                        if (player.getBounds().x <= 5.5){
                            segment++;
                            fadeOut();
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, 3.5f)
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 5:
                        break;
                }
                break;
            case HEADING_HOME:
                switch (segment){
                    case 0:
                        // Enter, stage right
                        if (player.getBounds().x < 16){
                            player.getBounds().x = 16;
                            segment++;
                        }
                        break;
                    case 1:
                        // Walking into the house
                        if (player.getBounds().x < 10) {
                            player.getBounds().x = 10;
                            segment++;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                 .target(8f)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case MEET_THE_WIFE:
                switch (segment) {
                    case 0:
                        if (!dialogue.isActive()){
                            segment++;
                            player.moveDelay = 2f;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("wrangleKids"));
                            messages.add(GameText.getText("playerDontGo"));
                            dialogue.show(1, 10, 18, 4, messages);
                            Tween.to(kids.getBounds(), RectangleAccessor.X, 2f)
                                    .target(wife.getBounds().x + 1f)
                                    .delay(0.75f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 1:
                        // Wife storms out, takes the kids
                        if (!dialogue.isActive()) {
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("noTimeForThis"));
                            dialogue.show(1, 10, 18, 4, messages);
                            segment++;
                            //player.moveDelay = 4f;
                            Tween.to(wife.getBounds(), RectangleAccessor.X, 4f)
                                    .target(-1f)
                                    .ease(Linear.INOUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            wife.dead = true;
                                        }
                                    })
                                    .start(LudumDare33.tween);
                            Tween.to(kids.getBounds(), RectangleAccessor.X, 4f)
                                    .target(-1f)
                                    .ease(Linear.INOUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            kids.dead = true;
                                        }
                                    })
                                    .start(LudumDare33.tween);
                        }
                        break;
                    // TODO: have a drink (or three)
                    case 2:
                        // Go to the bed
                        if (player.getBounds().x < 12) {
                            player.getBounds().x = 12;
                            segment++;
                        }
                        break;
                    case 3:
                        // Get into bed
                        if (player.getBounds().x < 9.5f) {
                            player.getBounds().x = 9.5f;
                            segment++;
                            player.moveDelay = 1f;
                            Tween.to(player.getBounds(), RectangleAccessor.Y, 1f)
                                 .target(player.getBounds().y + 1f)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                        }
                        break;
                    case 4:
                        // Going to sleep
                        if (player.moveDelay <= 0){
                            player.moveDelay = 2f;
                            segment++;
                            Tween.to(transitionColor, ColorAccessor.A, 1f)
                                    .target(1)
                                    .ease(Linear.INOUT)
                                    .repeatYoyo(1, 0)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            // WAKE UP
                                            World.this.player.setSadMode();
                                            Array<String> messages = new Array<String>();
                                            messages.add(GameText.getText("tooOld"));
                                            messages.add(GameText.getText("missMisty"));
                                            dialogue.show(1, 10, 18, 4, messages);
                                        }
                                    })
                                    .start(LudumDare33.tween);
                            Tween.call(new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                    player.setSadMode();
                                }
                            })
                                 .delay(1f)
                                 .start(LudumDare33.tween);
                        }
                        break;
                    case 5:
                        if (player.getBounds().x < 1) {
                            player.moveDelay = EntityBase.PIPEDELAY;
                            segment++;
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(-1f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                            Tween.to(transitionColor, ColorAccessor.A, EntityBase.PIPEDELAY)
                                    .target(0)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case LEAVING_HOME:
                switch (segment) {
                    case 0:
                        // Enter, stage right
                        if (player.getBounds().x < 10){
                            player.getBounds().x = 10;
                            segment++;
                        }
                        break;
                    case 1:
                        // Walking into the pipe
                        if (player.getBounds().x < 3.5f) {
                            player.getBounds().x = 3;
                            segment++;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();

                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                                 .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case BACK_TO_WORK:
                switch (segment){
                    case 0:
                        // Get in position, release a mario
                        if (player.getBounds().x < 27){
                            player.getBounds().x = 27;
                            segment++;
                            player.moveDelay = 6;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("hereComesMario"));
                            dialogue.show(1,10,18,4,messages);
                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()){
                            segment++;
                            marioAI = new MarioAI(this, new Vector2(10, 2));
                            player.moveDelay = 6;
                        }
                        break;
                    case 2:
                        // Just a bump on the head, released to go home for the day
                        if (marioAI.dead){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("injuredAgain"));
                            dialogue.show(1, 10, 18, 4, messages);
                            player.setWounded();
                        }
                        break;
                    case 3:
                        if (!dialogue.isActive()) {
                            segment++;
                            player.addThought("yeah ... family ...");
                        }
                        break;
                    case 4:
                        // Enter home pipe
                        if (player.getBounds().x <= 5.5){
                            segment++;
                            fadeOut();
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, 3.5f)
                                    .start(LudumDare33.tween);
                        }
                }
                break;
            case HEADING_HOME_SAD:
                switch (segment) {
                    case 0:
                        // Enter, stage right
                        if (player.getBounds().x < 16.5f) {
                            player.getBounds().x = 16.5f;
                            segment++;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("mistyHope"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()) {
                            segment++;
                            player.addThought("Hopefully");
                        }
                        break;
                    case 2:
                        // Walking into the house
                        if (player.getBounds().x < 10) {
                            player.getBounds().x = 10;
                            segment++;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, 8f)
                                    .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case EMPTY_HOUSE:
                switch (segment) {
                    case 0:
                        // Get into bed
                        if (player.getBounds().x < 9.5f) {
                            player.getBounds().x = 9.5f;
                            segment++;
                            player.moveDelay = 1f;
                            Tween.to(player.getBounds(), RectangleAccessor.Y, 1f)
                                 .target(player.getBounds().y + 1f)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                        }
                        break;
                    case 1:
                        // Sleep... beautiful sleep
                        if (player.moveDelay <= 0) {
                            player.moveDelay = 2f;
                            segment++;
                            Tween.call(new TweenCallback() {
                                    @Override
                                    public void onEvent(int i, BaseTween<?> baseTween) {
                                        Assets.soundManager.playMusic(SoundManager.MusicOptions.MARIO_MINOR);
                                        loadMap("maps/inhome-bedroom-sad.tmx");
                                        player.setSadMode();
                                        player.addThought("* sigh *");
                                    }
                                })
                                    .delay(1.1f)
                                    .start(LudumDare33.tween);

                            Tween.to(transitionColor, ColorAccessor.A, 1f)
                                 .target(1)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                            Tween.to(transitionColor, ColorAccessor.A, 1f)
                                    .target(0)
                                    .delay(1.5f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 2:
                        // Up and at-them
                        if (player.getBounds().x < 1) {
                            player.moveDelay = EntityBase.PIPEDELAY;
                            segment++;
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                 .target(-1f)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                            Tween.to(transitionColor, ColorAccessor.A, EntityBase.PIPEDELAY)
                                 .target(0)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case GET_MUSHROOM:
                switch (segment){
                    case 0:
                        // Get in position, release a mario
                        if (player.getBounds().x < 27){
                            player.getBounds().x = 27;
                            segment++;
                            player.moveDelay = 6;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("hereComesMario"));
                            dialogue.show(1,10,18,4,messages);
                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()){
                            segment++;
                            MarioAI mario = new MarioAI(this, new Vector2(10, 2));
                            player.getBounds().x = 27;
                            player.moveDelay = 6;
                        }
                        break;
                    case 2:
                        // Just picked up the Mushroom
                        if (player.raged){
                            Assets.soundManager.playSound(SoundManager.SoundOptions.GOOMBA_MUSHROOM_GET);
                            Assets.soundManager.playMusic(SoundManager.MusicOptions.MARIO_MAJOR_BK);
                            segment++;
                            player.moveDelay = 4f;
                            cameraLock = false;
                            player.smashedAnimation = player.standingAnimation = Assets.goombaGrowAnimation;
                            player.stateTime = 0f;
                            Timeline.createSequence()
                                    .push(Tween.to(camera, CameraAccessor.XYZ, 2f)
                                        .target(player.getBounds().x + .5f, player.getBounds().y + .5f, .1f)
                                        .ease(Quad.OUT))
                                    .pushPause(.5f)
                                    .push(Tween.to(camera, CameraAccessor.XYZ, 1.5f)
                                        .target(cameraCenter.x, cameraCenter.y, 1)
                                        .ease(Quad.IN)
                                        .setCallback(new TweenCallback() {
                                            @Override
                                            public void onEvent(int type, BaseTween<?> source) {
                                                player.smashedAnimation = Assets.goombaSmashedAnimation;
                                                player.setRageMode();
                                            }
                                        }))
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 3:
                        particles.addSparkles(player.getBounds()); //sparkle all the time until you can move again
                        if (player.moveDelay <= 0){
                            cameraLock = true;
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("gotMushroom1"));
                            messages.add(GameText.getText("gotMushroom2"));
                            messages.add(GameText.getText("gotMushroom3"));
                            messages.add(GameText.getText("gotMushroom4"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 4:
                        if (player.getBounds().x < 9){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("noGoingBack1"));
                            messages.add(GameText.getText("noGoingBack2"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 5:
                        // Head to new world
                        if (player.getBounds().x <= 2.5 && player.getBounds().x > 1 && player.getBounds().y < 5.1) {
                            segment++;
                            player.getBounds().x = 1.7f;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, 3)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            World.this.done = true;
                                        }
                                    })
                                    .start(LudumDare33.tween);
                        }
                }
                break;
            case INTO_THE_FACTORY:
                if (fallingMario.dead){
                    fallingMario = new Mario(this, new Vector2(22.5f,14));
                    fallingMario.setDeadAnimations();
                    final Rectangle fallingMarioBounds = fallingMario.getBounds();
                    repeatingTween = Tween.to(fallingMarioBounds, RectangleAccessor.Y, 2f)
                                          .target(7)
                                          .repeat(-1, 2)
                                          .ease(Expo.IN)
                                          .setCallback(new TweenCallback() {
                                              @Override
                                              public void onEvent(int i, BaseTween<?> baseTween) {
                                                  shake.shake(.5f);
                                                  particles.addLargeBlood(new Vector2(22.5f, 8.5f));
                                                  Vector2 dist = new Vector2(fallingMarioBounds.x - player.getBounds().x, fallingMarioBounds.y - player.getBounds().y);
                                                  Assets.soundManager.playSound3D(
                                                          SoundManager.SoundOptions.MARIO_RECLAIMATION,
                                                          dist);
                                              }
                                          })
                                          .setCallbackTriggers(TweenCallback.END)
                                          .start(LudumDare33.tween);
                    fallingMario.moveDelay = 100000f;
                }
                switch (segment){
                    case 0:
                        if (!dialogue.isActive()) {
                            segment++;
                        }
                        break;
                    case 1:
                        if (player.getBounds().x < 84f) {
                            player.getBounds().x = 84f;
                            segment++;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("marioScreenWTF"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 2:
                        if (!dialogue.isActive()) {
                            segment++;
                        }
                        break;
                    case 3:
                        if (player.getBounds().x < 45.5f) {
                            player.getBounds().x = 45.5f;
                            segment++;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("marioTubesWTF"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 4:
                        if (!dialogue.isActive()) {
                            segment++;
                        }
                        break;
                    case 5:
                        if (player.getBounds().x < 22.5f) {
                            player.getBounds().x = 22.5f;
                            segment++;

                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("ellipses"));
                            messages.add(GameText.getText("marioGrinderWTF"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 6:
                        if (player.getBounds().x < 2.5f && player.getBounds().y < 4.1) {
                            player.getBounds().x = 2.5f;
                            player.getBounds().y = 4f;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            repeatingTween.kill();
                            fadeOut();
                            segment++;
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y - 1f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case DEEP_FACTORY:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 2.5f && player.getBounds().y < 4.1) {
                            player.getBounds().x = 2.5f;
                            player.getBounds().y = 4f;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.Y, player.getBounds().y - 1f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                            segment++;
                        }
                        break;
                }
                break;
            case CULT_ROOM:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 10){
                            kingHippo.chant(false);
                            motherBrain.chant(false);
                            dracula.chant(false);
                            luigi.chant(false);
                            drWily.chant(false);
                            repeatingTween.kill();
                            player.moveDelay = 10000;
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter1"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()){
                            ganon.chant(false);
                            kingHippo.chant(true);
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter2"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 2:
                        if (!dialogue.isActive()){
                            segment++;
                            kingHippo.chant(false);
                            motherBrain.chant(true);
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter3"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 3:
                        if (!dialogue.isActive()){
                            segment++;
                            motherBrain.chant(false);
                            dracula.chant(true);
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter4"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 4:
                        if (!dialogue.isActive()){
                            segment++;
                            dracula.chant(false);
                            luigi.chant(true);
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter5"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 5:
                        if (!dialogue.isActive()){
                            segment++;
                            luigi.chant(false);
                            drWily.chant(true);
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter6"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 6:
                        if (!dialogue.isActive()) {
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultReflection1"));
                            messages.add(GameText.getText("cultReflection2"));
                            messages.add(GameText.getText("cultReflection3"));
                            messages.add(GameText.getText("cultReflection4"));
                            messages.add(GameText.getText("cultReflection5"));
                            messages.add(GameText.getText("cultReflection6"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 7:
                        if (!dialogue.isActive()){
                            drWily.chant(false);
                            segment++;
                            endDelay = 1000;
                            cape.moveDelay = 10000;
                            transitionColor = new Color(1,1,1,0);
                            capeFloatingTween.kill();
                            Timeline.createSequence()
                                    .push(Tween.to(cape.getBounds(), RectangleAccessor.Y, 1f)
                                            .target(2))
                                    .beginParallel()
                                          .push(Tween.to(player.getBounds(), RectangleAccessor.Y, 2f)
                                                  .target(10))
                                          .push(Tween.to(cape.getBounds(), RectangleAccessor.Y, 2f)
                                                  .target(10))
                                    .end()
                                    .pushPause(.1f)
                                    .beginParallel()
                                        .push(Tween.to(player.getBounds(), RectangleAccessor.X, .5f)
                                                .target(9)
                                                .ease(Circ.INOUT)
                                                .repeatYoyo(2, 0))
                                        .push(Tween.to(cape.getBounds(), RectangleAccessor.X, .5f)
                                                .target(9)
                                                .ease(Circ.INOUT)
                                                .repeatYoyo(2, 0))

                                    .end()
                                    .beginParallel()

                                        .push(Tween.to(player.getBounds(), RectangleAccessor.X, .2f)
                                                .target(10)
                                                .ease(Circ.INOUT)
                                                .repeatYoyo(10, 0))
                                        .push(Tween.to(cape.getBounds(), RectangleAccessor.X, .2f)
                                                .target(8)
                                                .ease(Circ.INOUT)
                                                .repeatYoyo(10, 0))
                                        .push(Tween.to(transitionColor, ColorAccessor.A, 2f)
                                                .target(1))
                                    .end()
                                    .pushPause(.25f)
                                    .push(Tween.call(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            player.setCaped();
                                            cape.dead = true;
                                            player.getBounds().x = 0f;
                                            player.getBounds().y = 2;
                                            ganon.getBounds().x = 1.4f;
                                            kingHippo.getBounds().x = 2.85f;
                                            endDelay = 5;
                                            cameraLock = false;
                                            camera.position.x = player.getBounds().x + 1.5f;
                                            camera.position.y = player.getBounds().y + 1f;
                                            camera.zoom = .1f;

                                        }
                                    }))
                                    .pushPause(.5f)
                                    .push(Tween.to(transitionColor, ColorAccessor.A, 1f)
                                            .target(0)
                                            .ease(Linear.INOUT))
                                    .pushPause(.5f)
                                    .push(Tween.to(camera, CameraAccessor.XYZ, 2f)
                                            .target(cameraCenter.x, cameraCenter.y, 1)
                                            .ease(Linear.INOUT))
                                    .start(LudumDare33.tween);


                        }
                        break;
                    case 8:
                        endDelay -= dt;
                        if (endDelay < 0){
                            endDelay = 2f;
                            segment++;
                            ganon.chant(true);
                            kingHippo.chant(true);
                            motherBrain.chant(true);
                            dracula.chant(true);
                            luigi.chant(true);
                            drWily.chant(true);
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("theEnd1"));
                            dialogue.show(1, 10, 18, 4, messages);
                            Tween.call(new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {


                                    ganon.addThought("LUDUM DARE");
                                    kingHippo.addThought("LUDUM DARE");
                                    motherBrain.addThought("LUDUM DARE");
                                    dracula.addThought("LUDUM DARE");
                                    luigi.addThought("LUDUM DARE");
                                    drWily.addThought("LUDUM DARE");
                                    player.addThought("LUDUM DARE");

                                    repeatingTween = Tween.call(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            ganon.addThought("LUDUM DARE");
                                            kingHippo.addThought("LUDUM DARE");
                                            motherBrain.addThought("LUDUM DARE");
                                            dracula.addThought("LUDUM DARE");
                                            luigi.addThought("LUDUM DARE");
                                            drWily.addThought("LUDUM DARE");
                                            player.addThought("LUDUM DARE");
                                        }
                                    })
                                            .delay(3.5f)
                                            .repeat(-1, 3.5f)
                                            .start(LudumDare33.tween);
                                }
                            })
                                    .delay(1.1f)
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 9:
                        endDelay -= dt;
                        if (endDelay <= 0) {
                            segment++;
                            // Let's bring down the curtain.
                            drawEndCurtain = true;
                            endCurtainAnimationComplete = false;

                            endCurtainBottomY = new MutableFloat(Config.height);
                            endCurtainFullY = new MutableFloat(Config.height + 32f);
                            // Prep the tween to bring the full curtain just a bit past the bottom.
                            final TweenCallback fullCurtainCompleteTC = new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                endCurtainAnimationComplete = true;
                                }
                            };
                            TweenCallback fullCurtainTC = new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                    Tween.to(endCurtainFullY, -1, 0.35f)
                                            .target(8f)
                                            .ease(Sine.OUT)
                                            .setCallback(fullCurtainCompleteTC)
                                            .start(LudumDare33.tween);
                                }
                            };
                            // Tween both curtains down together.
                            // Bottom
                            Tween.to(endCurtainBottomY, -1, 3f)
                                    .target(-8f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                            Tween.to(endCurtainFullY, -1, 3f)
                                    .target(24f)
                                    .ease(Linear.INOUT)
                                    .setCallback(fullCurtainTC)
                                    .start(LudumDare33.tween);

//                        float endCurtainInitialDuration = 3f;
//                        float endCurtainRuffleDuration = 0.35f;
//                        endCurtainCompleteRuffleCount = 0;
//                        int i;
//                        float start;
//                        float target;
//
//                        // Create the timeline
//                        Timeline curtainTimeline = Timeline.createSequence();
//
//                        // Add the initial tweens to get everything to descend in a linear sequence.
//                        curtainTimeline.beginParallel();
//                        for (i = 0; i < END_CURTAIN_RUFFLES; i++) {
//                            // We have to init the Mutables this time around
//                            start = Config.height + (END_CURTAIN_RUFFLE_OFFSET * i);
//                            endCurtainPositions.add(new MutableFloat(start));
//                            target = END_CURTAIN_BOTTOM_RUFFLE_END_Y + (END_CURTAIN_RUFFLE_OFFSET * i);
//                            curtainTimeline.push(
//                                    Tween.to(endCurtainPositions.get(i), -1, endCurtainInitialDuration)
//                                            .target(target)
//                                            .ease(Linear.INOUT)
//                            );
//                        }
//
//                        // End that initial parallel
//                        curtainTimeline.end();
//
//                        // When that parallel ends, we want to update our finished ruffle index
//                        curtainTimeline.setCallback(new TweenCallback() {
//                            @Override
//                            public void onEvent(int i, BaseTween<?> baseTween) {
//                                endCurtainCompleteRuffleCount = 1;
//                            }
//                        });
//
//                        // For each extra ruffle, add a new easing tween to bring down and stop the ruffle.
//                        for (i = 1; i < END_CURTAIN_RUFFLES; i++) {
//                            curtainTimeline.push(
//                                    Tween.to(endCurtainPositions.get(i), -1, endCurtainRuffleDuration)
//                                            .target(endCurtainPositions.get(i).floatValue() - 16f)
//                                            .ease(Sine.OUT)
//                                            .setCallback(new TweenCallback() {
//                                                @Override
//                                                public void onEvent(int i, BaseTween<?> baseTween) {
//                                                    endCurtainCompleteRuffleCount++;
//                                                }
//                                            })
//                            );
//                        }
//
//                        // When it's all over, flip the complete flag
//                        curtainTimeline.setCallback(new TweenCallback() {
//                            @Override
//                            public void onEvent(int i, BaseTween<?> baseTween) {
//                                endCurtainAnimationComplete = true;
//                            }
//                        });
//
//                        // Start it up
//                        curtainTimeline.start(LudumDare33.tween);
                        }
                        break;
                    case 10:
                        // Wait for the curtain to be fully done.
                        if (endCurtainAnimationComplete) {
                            segment++;
                        }
                        break;
                    case 11:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("theEnd2"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 12:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("gameOver"));
                            dialogue.show(8, 7, 4, 3, messages, false, 100, false);
                        }
                        break;
                }
                break;
            case OVERWORLD_FIRST:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 95.5f) {
                            player.getBounds().x = 95.5f;
                            segment++;
                        }
                        break;
                    case 1:
                        if (player.getBounds().x < 2.1f && player.getBounds().y < 2.5f) {
                            segment++;
                            // Pipe exit
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                                    .ease(Linear.INOUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            World.this.fadeOut();
                                        }
                                    })
                                    .start(LudumDare33.tween);
                        }
                }
                break;
            case UNDERWORLD:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 5.5f && player.getBounds().y < 10.5f) {

                            segment++;
                            // Pipe exit
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1f)
                                    .ease(Linear.INOUT)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            World.this.fadeOut();
                                        }
                                    })
                                    .start(LudumDare33.tween);
                        }
                }
                break;
            case SHROOM_LAND:
                switch (segment) {
                    case 0:
                        if (!dialogue.isActive()) {
                            segment++;
                        }
                        break;
                    case 1:
                        if (player.getBounds().x < 2.5f && player.getBounds().y < 2.1f) {
                            segment++;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            TweenHelper.tweenPipeTravel(player, RectangleAccessor.X, player.getBounds().x - 1)
                                 .ease(Linear.INOUT)
                                 .start(LudumDare33.tween);
                        }
                }
                break;
            case BRIDGES_TO_FACTORY:
                // Always be shooting fireworks
                fireworkDelay-= dt;
                if (fireworkDelay <= 0) {
                    shootCastleFireworks();
                }

                switch (segment) {
                    case 0:
                        if (!dialogue.isActive()) {
                            segment++;
                            // TODO: script all the things! (or at least an exit handler)
                        }
                        break;
                    case 1:
                        if (player.getBounds().x < 8){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("atCastle1"));
                            messages.add(GameText.getText("atCastle2"));
                            messages.add(GameText.getText("atCastle3"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 2:
                        if (player.getBounds().x < 3.1 && player.getBounds().y < 3.1){
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(1)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
        }
    }

    private void fadeIn(){
        transitionColor = new Color(0, 0, 0, 1);
        Tween.to(transitionColor, ColorAccessor.A, EntityBase.PIPEDELAY)
                .target(0)
                .ease(Linear.INOUT)
                .start(LudumDare33.tween);
    }

    private void fadeOut(){
        transitionColor = new Color(0, 0, 0, 0);
        Tween.to(transitionColor, ColorAccessor.A, EntityBase.PIPEDELAY)
                .target(1)
                .ease(Linear.INOUT)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        World.this.done = true;
                    }
                })
                .start(LudumDare33.tween);
    }



    private void loadMapObjects() {
        if (map == null) return;

        mapObjects = new Array<ObjectBase>();

        MapProperties props;
        MapLayer objectLayer = map.getLayers().get("objects");
        for (MapObject object : objectLayer.getObjects()) {
            props = object.getProperties();
            float w = (Float) props.get("width");
            float h = (Float) props.get("height");
            float x = (Float) props.get("x");
            float y = (Float) props.get("y"); // NOTE: god dammit... off by 1
            String type = (String) props.get("type");

            // Instantiate based on type
            if (type.equals("qblock")) {
                String dropTypeName = (String) props.get("drops");
                ItemEntity.ItemType dropType = ItemEntity.ItemType.getType(dropTypeName);
                mapObjects.add(new QuestionBlock(this, new Rectangle(x / w, (y / h) + 1, 1, 1), dropType));
            }
            else if (type.equals("spike")) {
                mapObjects.add(new SpikeObject(this, new Rectangle(x / w, (y / h) + 1, 1, 1)));
            }
            else if(type.equals("tube")) {
                mapObjects.add(new TubeObject(
                    this, new Rectangle(x / w, (y / 16) + 2, 1, 2),
                    TubeObject.TubeContents.valueOf((String) props.get("contains"))));
            }
            else if(type.equals("marioscreen")) {
                mapObjects.add(new MarioScreenObject(
                    this, new Rectangle(x / 16, (y / 16) + 2, 2, 2)));
            }
            else if (type.equals("coin")) {
                new CoinItem(this, x / 16, (y / 16) + 1, false);
            }
//            else if (type.equals("...")) {
//
//            }
        }
    }

    private void shootCastleFireworks(){
        fireworkDelay = .5f;

        int rand = MathUtils.random(4);
        switch(rand) {
            case 0:
                particles.addFirework(new Vector2(4.1f, 11), 90);
                break;
            case 1:
                particles.addFirework(new Vector2(2.1f, 11), 90);
                break;
            case 2:
                particles.addFirework(new Vector2(5.1f, 9), 45);
                break;
            case 3:
                particles.addFirework(new Vector2(6.1f, 7), 30);
                break;
            case 4:
                particles.addFirework(new Vector2(4.1f, 7), 45);
                break;
        }


    }

}
