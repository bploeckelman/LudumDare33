package lando.systems.ld33.utils;

import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.screens.ChapterScreen;
import lando.systems.ld33.screens.LDScreen;
import lando.systems.ld33.screens.PrototypeScreen;

/**
 * Brian Ploeckelman created on 8/22/2015.
 */
public class StoryManager {

    final LudumDare33 game;

    int         currentChapter;
    World.Phase currentPhase;
    LDScreen    currentScreen;

    public StoryManager(LudumDare33 game) {
        this.game = game;
        this.currentChapter = 1;
        this.currentPhase = World.Phase.First;
        this.currentScreen = new PrototypeScreen(game, currentPhase);

        game.setScreen(currentScreen);
    }

    public void update(float delta) {
        if (!currentScreen.isDone()) return;

        if (currentScreen instanceof PrototypeScreen) {
            System.out.println("launching chapter " + currentChapter);
            currentScreen = new ChapterScreen(game, currentChapter);
            currentPhase = currentPhase.nextPhase();
            game.setScreen(currentScreen);
        }
        else if (currentScreen instanceof ChapterScreen) {
            System.out.println("launching game phase " + currentPhase.getValue());
            currentScreen = new PrototypeScreen(game, currentPhase);
            currentChapter++;
            // TODO: temporary until we have more chapters in place
            if (currentChapter > 1) currentChapter = 1;
            game.setScreen(currentScreen);
        }
    }

}
