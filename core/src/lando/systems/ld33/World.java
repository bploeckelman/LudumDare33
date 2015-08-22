package lando.systems.ld33;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld33.entities.EntityBase;
import lando.systems.ld33.entities.PlayerGoomba;
import lando.systems.ld33.utils.Assets;

import java.util.ArrayList;

/**
 * Created by dsgraham on 8/22/15.
 */
public class World {

    public static final float MAP_UNIT_SCALE    = 1f / 16f;
    public static final int   SCREEN_TILES_WIDE = 20;
    public static final int   SCREEN_TILES_HIGH = 15;

    TiledMap                   mapLevel1;
    OrthogonalTiledMapRenderer mapRenderer;
    Array<Rectangle>            tileRects;
    public Pool<Rectangle>             rectPool;
    OrthographicCamera          camera;
    ArrayList<EntityBase>       gameEntities;
    EntityBase                  player;
    float cameraLeftEdge;
    float cameraRightEdge;

    public World(OrthographicCamera cam){
        gameEntities = new ArrayList<EntityBase>();
        camera = cam;

        final TmxMapLoader mapLoader = new TmxMapLoader();

        //TODO pass this in?
//        testMap = mapLoader.load("maps/mario-test.tmx");
        mapLevel1 = mapLoader.load("maps/level1.tmx");
//        mapRenderer = new OrthogonalTiledMapRenderer(testMap, MAP_UNIT_SCALE);
        mapRenderer = new OrthogonalTiledMapRenderer(mapLevel1, MAP_UNIT_SCALE);

        TiledMapTileLayer mapLayer = (TiledMapTileLayer) mapLevel1.getLayers().get("foreground");
        cameraLeftEdge = SCREEN_TILES_WIDE / 2;
        cameraRightEdge = mapLayer.getWidth() - cameraLeftEdge;

        tileRects = new Array<Rectangle>();
        rectPool = Pools.get(Rectangle.class);

        player = new PlayerGoomba(this, Assets.testTexture, new Rectangle(5, 2, 1,1 ));
        gameEntities.add(player);

    }

    public void update(float dt){
        for (EntityBase entity: gameEntities){
            entity.update(dt);
        }

        float playerX = player.getBounds().x;
        if (playerX < camera.position.x - 3) camera.position.x = playerX + 3;
        if (playerX > camera.position.x + 2) camera.position.x = playerX - 2;

        // keep the map in view always
        camera.position.x = Math.min(cameraRightEdge, Math.max(cameraLeftEdge, camera.position.x));
        camera.update();
    }

    public void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        TiledMapTileLayer layer = (TiledMapTileLayer)mapLevel1.getLayers().get("foreground");
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    public void render(SpriteBatch batch){

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        for (EntityBase entity : gameEntities){
            entity.render(batch);
        }
        batch.end();
    }


}
