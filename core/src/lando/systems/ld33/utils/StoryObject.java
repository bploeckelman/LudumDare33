package lando.systems.ld33.utils;

import lando.systems.ld33.LudumDare33;
import lando.systems.ld33.World;
import lando.systems.ld33.screens.ChapterScreen;
import lando.systems.ld33.screens.PrototypeScreen;

/**
 * Brian Ploeckelman created on 8/23/2015.
 */
public class StoryObject {

    private final StoryManager storyManager;
    private final LudumDare33  game;

    public Integer chapterNumber;
    public World.Phase worldPhase;

    public StoryObject(StoryManager storyManager, Integer chapterNumber, World.Phase worldPhase) {
        this.storyManager = storyManager;
        this.game = storyManager.game;
        this.chapterNumber = chapterNumber;
        this.worldPhase = worldPhase;
    }

    public void doTransition() {
        if (chapterNumber != null) {
            // TODO: do chapter transition
            System.out.println("launching chapter " + chapterNumber);
            storyManager.currentChapter = chapterNumber;
            storyManager.currentScreen = new ChapterScreen(game, chapterNumber);
            game.setScreen(storyManager.currentScreen);
        }
        else if (worldPhase != null) {
            // TODO: do world phase transition
            System.out.println("launching game phase " + worldPhase.name());
            storyManager.currentPhase = worldPhase;
            storyManager.currentScreen = new PrototypeScreen(game, worldPhase);
            game.setScreen(storyManager.currentScreen);
        }
    }

}
