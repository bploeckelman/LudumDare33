package lando.systems.ld33.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Author: Ian McNamara <ian.mcnamara@wisc.edu>
 * Teaching and Research Application Development
 * Copyright 2015 Board of Regents of the University of Wisconsin System
 */
public class SoundManager{

    public enum SoundOptions {
        COIN_GET,
        COIN_REVEAL,
        GOOMBA_SQUASH,
        MARIO_DEATH,
        MARIO_JUMP,
        MUSHROOM_GET,
        MUSHROOM_REVEAL
    }

    private HashMap<SoundOptions, Sound> soundMap = new HashMap<SoundOptions, Sound>();

    // -----------------------------------------------------------------------------------------------------------------

    public SoundManager() {
        load();
    }

    // -----------------------------------------------------------------------------------------------------------------

    private void load() {
        soundMap.put(SoundOptions.COIN_GET, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/coin-get.wav")));
        soundMap.put(SoundOptions.COIN_REVEAL, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/coin-reveal.wav")));
        soundMap.put(SoundOptions.GOOMBA_SQUASH, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/goomba-squash.wav")));
        soundMap.put(SoundOptions.MARIO_DEATH, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mario-death-1.wav")));
        soundMap.put(SoundOptions.MARIO_JUMP, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mario-jump-1.wav")));
        soundMap.put(SoundOptions.MUSHROOM_GET, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mushroom-get.wav")));
        soundMap.put(SoundOptions.MUSHROOM_REVEAL, Gdx.audio.newSound(Gdx.files.internal("sounds/effects/mushroom-reveal-1.wav")));
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void dispose() {
        SoundOptions[] allSounds = SoundOptions.values();
        for (SoundOptions allSound : allSounds) {
            soundMap.get(allSound).dispose();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void playSound(SoundOptions soundOption) {
        soundMap.get(soundOption).play();
    }

}
