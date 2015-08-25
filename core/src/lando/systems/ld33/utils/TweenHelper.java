package lando.systems.ld33.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;
import lando.systems.ld33.entities.EntityBase;

/**
 * Author: Ian McNamara <ian.mcnamara@wisc.edu>
 * Teaching and Research Application Development
 * Copyright 2015 Board of Regents of the University of Wisconsin System
 */
public class TweenHelper {

    /**
     * Convenience method to create the pipe transition tween & play the pipe noise.
     * Does NOT start the tween.
     * @param entity The entity to 'move' into/out of the pipe
     * @param rectangleAccessor The axis to move on
     * @param target Where to go
     * @return The tween for chaining.
     */
    public static Tween tweenPipeTravel(EntityBase entity, int rectangleAccessor, float target) {
        Assets.soundManager.playSound(SoundManager.SoundOptions.PIPE_TRAVEL);
        return Tween.to(entity.getBounds(), rectangleAccessor, EntityBase.PIPEDELAY)
                .target(target)
                .ease(Linear.INOUT);
    }

}
