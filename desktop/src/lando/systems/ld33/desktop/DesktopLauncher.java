package lando.systems.ld33.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lando.systems.ld33.Config;
import lando.systems.ld33.LudumDare33;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = Config.title;
        config.width = Config.width;
        config.height = Config.height;
		new LwjglApplication(new LudumDare33(), config);
	}
}
