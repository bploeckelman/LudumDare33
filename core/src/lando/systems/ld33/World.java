package lando.systems.ld33;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Game;
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
import lando.systems.ld33.entities.items.ItemEntity;
import lando.systems.ld33.entities.mapobjects.*;
import lando.systems.ld33.utils.Assets;
import lando.systems.ld33.utils.GameText;
import org.w3c.dom.css.Rect;

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
        DAY_ONE, HEADING_HOME, MEET_THE_WIFE, LEAVING_HOME, BACK_TO_WORK, INTO_THE_FACTORY, EMPTY_HOUSE,
        CULT_ROOM,
        GET_MUSHROOM,
        LEVEL2
    }

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
    public Tween                      chant;
    public Ganon                      ganon;
    public KingHippo                  kingHippo;
    public MotherBrain                motherBrain;
    public Dracula                    dracula;
    public Luigi                      luigi;
    public DrWily                     drWily;

    public World(OrthographicCamera cam, Phase p, SpriteBatch batch) {
        this.batch = batch;
        phase = p;
        endDelay = 0;
        done = false;
        cameraLock = true;
        dialogue = new Dialogue();
        transitionColor = new Color(1,1,1,1);

        gameEntities = new Array<EntityBase>();
        camera = cam;

        initPhase();

        tileRects = new Array<Rectangle>();
        rectPool = Pools.get(Rectangle.class);

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

        Iterator<EntityBase> iterator = gameEntities.iterator();
        while(iterator.hasNext()) {
            EntityBase entity = iterator.next();
            entity.update(dt);
            if (entity.dead) {
                if (entity == player){
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
            if (playerX < camera.position.x - 3) camera.position.x = playerX + 3;
            if (playerX > camera.position.x + 2) camera.position.x = playerX - 2;


            camera.position.x = Math.min(cameraRightEdge, Math.max(cameraLeftEdge, camera.position.x));
        }
        camera.update();

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

            mapRenderer.renderTileLayer(foregroundLayer);
            for (EntityBase entity : gameEntities){
                if (entity instanceof Mario)
                    entity.render(batch);
            }
        }
        batch.end();

    }

    public void renderUI(SpriteBatch batch, OrthographicCamera uiCam) {
        dialogue.render(batch);
        for (EntityBase entity : gameEntities){
            entity.renderUI(batch, camera, uiCam);
        }
        batch.setColor(transitionColor);
        batch.draw(Assets.blackTexture, 0,0, uiCam.viewportWidth, uiCam.viewportHeight);
        batch.setColor(Color.WHITE);
    }

    public void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
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
                loadMap("maps/level1.tmx");
                cameraLock = false;


                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                Tween.to(player.getBounds(), RectangleAccessor.Y, EntityBase.PIPEDELAY)
                     .target(player.getBounds().y + 1f)
                        .ease(Linear.INOUT)
                     .start(LudumDare33.tween);

                float initY = camera.position.y;
                float initZoom = camera.zoom;
                camera.position.x = player.getBounds().x + .5f;
                camera.position.y = player.getBounds().y + .5f;
                camera.zoom = .1f;
                camera.update();
                Tween.to(camera, CameraAccessor.XYZ, 1f)
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
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
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
                messages.add(GameText.getText("playerDontGo"));
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
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);
                break;
            case BACK_TO_WORK:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1f);

                loadMap("maps/level1.tmx");

                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setSadMode();
                Tween.to(player.getBounds(), RectangleAccessor.Y, EntityBase.PIPEDELAY)
                     .target(player.getBounds().y + 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("impressBoss"));
                dialogue.show(1, 10, 18, 4, messages);
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
                messages.add(GameText.getText("notComingBack"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case GET_MUSHROOM:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1);
                loadMap("maps/level1.tmx");

                player = new PlayerGoomba(this, new Vector2(33.5f, 3f));
                player.canJump = false;
                player.canRight = false;
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setSadMode();
                Tween.to(player.getBounds(), RectangleAccessor.Y, EntityBase.PIPEDELAY)
                        .target(player.getBounds().y + 1f)
                        .ease(Linear.INOUT)
                        .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("foremanLate"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case INTO_THE_FACTORY:
                Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);

                loadMap("maps/level-factory.tmx");

                player = new PlayerGoomba(this, new Vector2(97, 2));
                player.setRageMode();
                player.moveDelay = EntityBase.PIPEDELAY;
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("intoFactory"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case CULT_ROOM:
                Gdx.gl.glClearColor(220f / 255f, 20f / 255f, 60f / 255f, 1f);

                loadMap("maps/cadreroom.tmx");

                // Spawn cultist members
                ganon       = new Ganon(this, new Vector2(0.5f, 2));
                kingHippo   = new KingHippo(this, new Vector2(2.5f, 2));
                motherBrain = new MotherBrain(this, new Vector2(6, 2));
                dracula     = new Dracula(this, new Vector2(13, 2));
                luigi       = new Luigi(this, new Vector2(16, 2));
                drWily      = new DrWily(this, new Vector2(18, 2));

                ganon.addThought("LUDUM DARE");
                kingHippo.addThought("LUDUM DARE");
                motherBrain.addThought("LUDUM DARE");
                dracula.addThought("LUDUM DARE");
                luigi.addThought("LUDUM DARE");
                drWily.addThought("LUDUM DARE");

                chant = Tween.call(new TweenCallback() {
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
                player.canRight = true;
                player.canJump = true;
                player.setNormalMode();
                player.moveDelay = EntityBase.PIPEDELAY;
                Tween.to(player.getBounds(), RectangleAccessor.Y, EntityBase.PIPEDELAY)
                     .target(player.getBounds().y - 1f)
                     .ease(Linear.INOUT)
                     .start(LudumDare33.tween);

                messages = new Array<String>();
                messages.add(GameText.getText("cultEnter"));
                dialogue.show(1, 10, 18, 4, messages);
                break;
            case LEVEL2:
                Gdx.gl.glClearColor(Assets.BLUE_SKY_R, Assets.BLUE_SKY_G, Assets.BLUE_SKY_B, 1f);

                loadMap("maps/level2.tmx");

                new MarioDumb(this, new Vector2(75f, 3f));
                new MarioDumb(this, new Vector2(47f, 3f));
                new MarioDumb(this, new Vector2(17f, 10f));
                new MarioDumb(this, new Vector2(3f, 3f));

                player = new PlayerGoomba(this, new Vector2(97f, 2f));
                player.moveDelay = EntityBase.PIPEDELAY;
                player.setRageMode();
                Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                     .target(player.getBounds().x - 1f)
                     .ease(Linear.INOUT)
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
        }
    }

    /**
     * Progress through each phase one scripted segment at a time
     * @param dt
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
                            new MarioAI(this, new Vector2(10, 2));
                            player.moveDelay = 4f;
                        }
                        break;
                    case 2:
                        // Just a bump on the head, released to go home for the day
                        if (player.moveDelay <= 0){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("headHome"));
                            dialogue.show(1, 10, 18, 4, messages);
                            player.setWounded();
                        }
                        break;
                    case 3:
                        // Enter home pipe
                        if (player.getBounds().x <= 5.5){
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(3.5f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
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
                            dialogue.show(1,10,18,4,messages);
                            Tween.to(kids.getBounds(), RectangleAccessor.X, 2f)
                                    .target(wife.getBounds().x + 1f)
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
                                            World.this.player.setSadMode();

                                            Array<String> messages = new Array<String>();
                                            messages.add(GameText.getText("tooOld"));
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
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                 .target(player.getBounds().x - 1f)
                                 .ease(Linear.INOUT)

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
                            MarioAI mario = new MarioAI(this, new Vector2(10, 2));

                        }
                        break;
                    case 2:
                        // Just a bump on the head, released to go home for the day
                        if (player.moveDelay <= 0){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("injuredAgain"));
                            dialogue.show(1, 10, 18, 4, messages);
                            player.setWounded();
                            Tween.call(new TweenCallback() {
                                    @Override
                                    public void onEvent(int i, BaseTween<?> baseTween) {
                                        player.addThought("yeah ... family ...");
                                    }
                                })
                                 .delay(2f)
                                 .start(LudumDare33.tween);
                        }
                        break;
                    case 3:
                        // Enter home pipe
                        if (player.getBounds().x <= 5.5){
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(3.5f)
                                    .ease(Linear.INOUT)

                                    .start(LudumDare33.tween);
                        }
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
                                        loadMap("maps/inhome-bedroom-sad.tmx");
                                        player.setSadMode();
                                        player.addThought("* sigh *");
                                    }
                                })
                                    .delay(1f)
                                    .start(LudumDare33.tween);

                            Tween.to(transitionColor, ColorAccessor.A, 1f)
                                 .target(1)
                                 .ease(Linear.INOUT)
                                 .repeatYoyo(1, 0)
                                 .setCallback(new TweenCallback() {
                                     @Override
                                     public void onEvent(int i, BaseTween<?> baseTween) {
                                         World.this.player.setSadMode();
                                     }
                                 })
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

                        }
                        break;
                    case 2:
                        // Just picked up the Mushroom
                        if (player.raged){
                            segment++;
                            player.moveDelay = 3f;
                            cameraLock = false;
                            player.smashedAnimation = Assets.goombaGrowAnimation;
                            player.stateTime = 0f;
                            Tween.to(camera, CameraAccessor.XYZ, 1.5f)
                                    .target(player.getBounds().x + .5f, player.getBounds().y + .5f, .1f)
                                    .ease(Quad.INOUT)
                                    .repeatYoyo(1, 0)
                                    .setCallback(new TweenCallback() {
                                        @Override
                                        public void onEvent(int type, BaseTween<?> source) {
                                            player.smashedAnimation = Assets.goombaSmashedAnimation;
                                        }
                                    })
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 3:
                        if (player.moveDelay <= 0){
                            cameraLock = true;
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("gotMushroom"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 4:
                        // Head to Factory
                        if (player.getBounds().x <= .5){
                            player.moveDelay = EntityBase.PIPEDELAY;
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(-2f)
                                    .ease(Linear.INOUT)
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
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 2.5f && player.getBounds().y < 4.5) {
                            player.getBounds().x = 2.5f;
                            player.getBounds().y = 4f;
                            player.moveDelay = EntityBase.PIPEDELAY;
                            fadeOut();
                            Tween.to(player.getBounds(), RectangleAccessor.Y, EntityBase.PIPEDELAY)
                                    .target(player.getBounds().y - 1f)
                                    .ease(Linear.INOUT)
                                    .start(LudumDare33.tween);
                        }
                        break;
                }
                break;
            case CULT_ROOM:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 10){
                            chant.kill();
                            player.moveDelay = 10000;
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter1"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 1:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter2"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 2:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("cultCenter3"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 3:
                        if (!dialogue.isActive()){
                            segment++;
                            endDelay = 5;
                            Timeline.createSequence()
                                    .push( Tween.to(transitionColor, ColorAccessor.A, 1f)
                                        .target(1)
                                        .ease(Linear.INOUT))
                                    .pushPause(.5f)
                                    .push(Tween.to(transitionColor, ColorAccessor.A, 1f)
                                        .target(0)
                                        .ease(Linear.INOUT))
                                    .start(LudumDare33.tween);

                            Tween.call(new TweenCallback() {
                                        @Override
                                        public void onEvent(int i, BaseTween<?> baseTween) {
                                            player.setCaped();
                                            player.getBounds().x = -0.4f;
                                            ganon.getBounds().x = 1.4f;
                                            kingHippo.getBounds().x = 2.85f;
                                        }
                                    })
                                    .delay(1.1f)
                                    .start(LudumDare33.tween);
                        }
                        break;
                    case 4:
                        endDelay -= dt;
                        if (endDelay < 0){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("theEnd1"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 5:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("theEnd2"));
                            dialogue.show(1, 10, 18, 4, messages);
                        }
                        break;
                    case 6:
                        if (!dialogue.isActive()){
                            segment++;
                            Array<String> messages = new Array<String>();
                            messages.add(GameText.getText("gameOver"));
                            dialogue.show(1, 10, 18, 4, messages, false, 100, false);
                        }
                        break;
                }
                break;
            case LEVEL2:
                switch (segment) {
                    case 0:
                        if (player.getBounds().x < 95.5f) {
                            player.getBounds().x = 95.5f;
                            segment++;
                        }
                        break;
                    case 1:
                        if (player.getBounds().x < 2.1f && player.getBounds().y < 2.5f) {
                            Tween.to(player.getBounds(), RectangleAccessor.X, EntityBase.PIPEDELAY)
                                    .target(player.getBounds().x - 1f)
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
//            else if (type.equals("...")) {
//
//            }
        }
    }

}
