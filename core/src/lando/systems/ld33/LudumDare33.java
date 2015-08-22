package lando.systems.ld33;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld33.accessors.ColorAccessor;
import lando.systems.ld33.accessors.RectangleAccessor;
import lando.systems.ld33.accessors.Vector2Accessor;
import lando.systems.ld33.accessors.Vector3Accessor;
import lando.systems.ld33.screens.PrototypeScreen;
import lando.systems.ld33.utils.Assets;

public class LudumDare33 extends Game {

	public static TweenManager tween;

	@Override
	public void create () {
		if (tween == null) {
			tween = new TweenManager();
			Tween.registerAccessor(Color.class, new ColorAccessor());
			Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
			Tween.registerAccessor(Vector2.class, new Vector2Accessor());
			Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		}
		Assets.load();
		setScreen(new PrototypeScreen(this));
	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		tween.update(delta);
		super.render();
	}

	@Override
	public void dispose() {
		Assets.dispose();
	}

}
