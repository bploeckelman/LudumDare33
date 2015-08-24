package lando.systems.ld33.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by dsgraham on 8/24/15.
 */
public class Score {
    public static int score;
    public static int coins;
    public static float time;

    private String scoreStr;
    private String coinStr;
    private String world;

    public Score(String world){
        buildScoreString();
        buildCoinString();
        this.world = world;
    }

    public void reset(){
        score = 0;
        coins = 0;
        time = 0;
        buildScoreString();
        buildCoinString();
    }

    public void update(float dt){
        time += dt;
    }


    public void render(SpriteBatch batch, OrthographicCamera cam){
        drawStringShaded(batch, "MARIO", 2 * 32, cam.viewportHeight - 32);
        drawStringShaded(batch, scoreStr, 2 * 32, cam.viewportHeight - 48);

        batch.draw(Assets.staticCoin, 7.5f*32, cam.viewportHeight - 64, 16, 16);
        drawStringShaded(batch, coinStr, 8 * 32, cam.viewportHeight - 48);

        drawStringShaded(batch, "WORLD", 11 * 32, cam.viewportHeight - 32);
        drawStringShaded(batch, world, 11.5f * 32, cam.viewportHeight - 48);

        drawStringShaded(batch, "TIME", 15 * 32, cam.viewportHeight - 32);
        final GlyphLayout layout = new GlyphLayout();
        String timeStr = "" + (int)time;
        layout.setText(Assets.font, timeStr);
        drawStringShaded(batch, timeStr, 17 * 32 - layout.width, cam.viewportHeight - 48);
    }

    private void drawStringShaded(SpriteBatch batch, String text, float x, float y){
        Assets.font.setColor(Color.BLACK);
        Assets.font.draw(batch, text, x+1, y - 1);
        Assets.font.setColor(Color.WHITE);
        Assets.font.draw(batch, text, x, y);

    }

    public void addScore(int s){
        score += s;
        if(score > 999999) score -= 1000000;
        if (score < 0) score += 1000000;
        buildScoreString();
    }

    public void addCoin(int c){
        coins += c;
        if (coins > 99) coins -= 100;
        if (coins < 0) coins += 100;
        addScore(c * 100);
        buildCoinString();
    }

    private void buildCoinString(){
        if (coins < 10) coinStr = "x0"+coins;
        else coinStr = "x" + coins;
    }

    private void buildScoreString(){
        int places = (int)MathUtils.log(10, score);
        if (score == 0) places = 0;
        scoreStr = "";
        for (int i = places; i < 5; i ++){
            scoreStr += "0";
        }
        scoreStr += score;
    }
}
