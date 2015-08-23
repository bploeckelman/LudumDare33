package lando.systems.ld33.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dsgraham on 8/23/15.
 */
public class GameText {
    private static Map<String, String> strings;

    public static void load(){
        strings = new HashMap<String, String>();
        strings.put("playerName", "Billy");
        strings.put("wifeName", "Misty");
        // First Day
        strings.put("foremanLate", "Foreman:\"You're late again! Move left and get into position!\"");
        strings.put("hereComesMario", "Foreman:\"All right everyone! Here he comes, look sharp\"");
        strings.put("headHome", "Foreman:\"Looks like you took a nasty knock there... "
                + "Why don't you head on home to your family.\"");

        // Getting Home
        strings.put("playerLate", strings.get("playerName") + ": \"Damn it I'm late getting home again.\"");

        // See the Wife
        strings.put("wifeBitching", strings.get("wifeName") + ":\"What the hell, injured on the job again?! "
                + "That's it, I'm taking the kids and going to my mother's house!\"");
        strings.put("playerDontGo", strings.get("playerName") + ":\"... wait, but... I ... don't go!\"");
        strings.put("wrangleKids", strings.get("wifeName") + ":\"Come here kids!\"");
        strings.put("noTimeForThis", strings.get("playerName") + ":\"I don't have time for this. I need to get up early tomorrow for work again.\"");

        // Leave home
        strings.put("tooOld", strings.get("playerName") + ":\"6 am already?! God dammit... I'm getting too old for this shit.\"");

        // Day 2
        strings.put("impressBoss", strings.get("playerName") + ":\"Here we go again. Maybe I can impress the boss today!\"");
        strings.put("injuredAgain", "Foreman:\"Injured again? Alright, head home and see your family.\"");

        // Empty Nest
        strings.put("notComingBack", strings.get("playerName") + ":\"... ... I guess she's really not coming back.\"");

        // Get Mushroom Day
        strings.put("gotMushroom", "What is this?!? There is more to life than just moving left!");

        //Factory
        strings.put("intoFactory", strings.get("playerName") + ":\"What... what [RED]is[] this place?\"");

        // Respawn
        strings.put("respawn", "Please be more careful, this is Ludum Dare so we won't make you start again.");

        // Cult room
        strings.put("cultEnter", strings.get("playerName") + ":\"This is creepy...\"");
        strings.put("cultChant", "Cultists (chanting): \"LUDUM DARE... LUDUM DARE... LUDUM DARE... LUDUM DARE...\"");
    }

    public static String getText(String key){
        if (strings.containsKey(key)){
            return strings.get(key);
        }

        return "Uninitialized String!!!! (" + key + ")";
    }
}
