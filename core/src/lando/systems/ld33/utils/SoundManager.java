package lando.systems.ld33.utils;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import lando.systems.ld33.LudumDare33;

import java.util.HashMap;

/**
 * Author: Ian McNamara <ian.mcnamara@wisc.edu>
 * Teaching and Research Application Development
 * Copyright 2015 Board of Regents of the University of Wisconsin System
 */
public class SoundManager{

    public enum SoundOptions {
        COIN_GET,
        GLASS_JAR_BREAK,
        GOOMBA_JUMP,
        GOOMBA_MUSHROOM_GET,
        GOOMBA_SQUASH,
        MARIO_DEATH,
        MARIO_JUMP,
        MUSHROOM_GET,
        MUSHROOM_REVEAL,
        PIPE_TRAVEL,
        SPIKE_STAB
    }
    public enum MusicOptions {
        MARIO_MAJOR,
        MARIO_MINOR
    }
    private enum MusicPieces {
        MARIO_MAJOR_INTRO,
        MARIO_MAJOR_LOOP,
        MARIO_MINOR_INTRO,
        MARIO_MINOR_LOOP
    }

    private HashMap<SoundOptions, Sound> soundMap = new HashMap<SoundOptions, Sound>();
    private HashMap<MusicPieces, Sound> musicMap = new HashMap<MusicPieces, Sound>();

    // -----------------------------------------------------------------------------------------------------------------

    public SoundManager() {
        load();
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void load() {
        soundMap.put(SoundOptions.COIN_GET, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/coin-get.wav")));
        soundMap.put(SoundOptions.GLASS_JAR_BREAK, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/glass-jar-break.wav")));
        soundMap.put(SoundOptions.GOOMBA_JUMP, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/goomba-jump.wav")));
        soundMap.put(SoundOptions.GOOMBA_MUSHROOM_GET, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/goomba-mushroom-get.wav")));
        soundMap.put(SoundOptions.GOOMBA_SQUASH, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/goomba-squash.wav")));
        soundMap.put(SoundOptions.MARIO_DEATH, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mario-death.wav")));
        soundMap.put(SoundOptions.MARIO_JUMP, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mario-jump.wav")));
        soundMap.put(SoundOptions.MUSHROOM_GET, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mushroom-get.wav")));
        soundMap.put(SoundOptions.MUSHROOM_REVEAL, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mushroom-reveal.wav")));
        soundMap.put(SoundOptions.PIPE_TRAVEL, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/pipe-travel.wav")));
        soundMap.put(SoundOptions.SPIKE_STAB, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/spike-stab.wav")));

        musicMap.put(MusicPieces.MARIO_MAJOR_INTRO, Gdx.audio.newSound(Gdx.files.internal("sounds/music/mario-major-intro.mp3")));
        musicMap.put(MusicPieces.MARIO_MAJOR_LOOP, Gdx.audio.newSound(Gdx.files.internal("sounds/music/mario-major-loop.mp3")));
        musicMap.put(MusicPieces.MARIO_MINOR_INTRO, Gdx.audio.newSound(Gdx.files.internal("sounds/music/mario-minor-intro.mp3")));
        musicMap.put(MusicPieces.MARIO_MINOR_LOOP, Gdx.audio.newSound(Gdx.files.internal("sounds/music/mario-minor-loop.mp3")));
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void dispose() {
        SoundOptions[] allSounds = SoundOptions.values();
        for (SoundOptions allSound : allSounds) {
            soundMap.get(allSound).dispose();
        }
        MusicPieces[] allMusicPieces = MusicPieces.values();
        for (MusicPieces musicPiece : allMusicPieces) {
            musicMap.get(musicPiece).dispose();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void playSound(SoundOptions soundOption) {
        soundMap.get(soundOption).play();
    }

    private MusicOptions currentOption;
    private long currentLoopID;
    private Sound currentLoopSound;

    public void playMusic(MusicOptions musicOption) {

        currentOption = musicOption;
        // Kill any currently play loop
        if (currentLoopSound != null) {
            currentLoopSound.stop(currentLoopID);
        }

        switch (musicOption) {
            case MARIO_MAJOR:
                musicMap.get(MusicPieces.MARIO_MAJOR_INTRO).play();
                Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        // Are we still in this case?
                        if (currentOption == MusicOptions.MARIO_MAJOR) {
                            currentLoopID = musicMap.get(MusicPieces.MARIO_MAJOR_LOOP).loop();
                            currentLoopSound = musicMap.get(MusicPieces.MARIO_MAJOR_LOOP);
                        }
                    }
                })
                        .delay(2.7f)
                        .start(LudumDare33.tween);
                break;
            case MARIO_MINOR:
                musicMap.get(MusicPieces.MARIO_MINOR_INTRO).play();
                Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        // Are we still in this case?
                        if (currentOption == MusicOptions.MARIO_MINOR) {
                            currentLoopID = musicMap.get(MusicPieces.MARIO_MINOR_LOOP).loop();
                            currentLoopSound = musicMap.get(MusicPieces.MARIO_MINOR_LOOP);
                        }
                    }
                })
                        .delay(3.2f)
                        .start(LudumDare33.tween);
                break;
            default:
                Gdx.app.log("ERROR", "SoundManager.playMusic | Unrecognized music option.");

        }
    }

}
