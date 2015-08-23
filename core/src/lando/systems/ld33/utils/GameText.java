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
        strings.put("foremanLate", "\"You're late! Move left and get into position!\"");
        strings.put("hereComesMario", "\"All right everyone! Here he comes, look sharp;!\"");
        strings.put("headHome", "\"Looks like you took a nasty knock there... "
                + "Why don't you head on home to your family.\"");

        // Getting Home
        strings.put("playerLate", ":\"Damn it I'm late getting home again.\"");

        // See the Wife
        strings.put("wifeBitching", ":\"What the hell, injured on the job again?! "
                + "That's it, I'm taking the kids and going to my mother's house!\"");
        strings.put("playerDontGo", "\"... wait, but... I ... don't go!\"");
        strings.put("noTimeForThis", ":\"I don't have time for this. I need to get up early tomorrow for work again.\"");

        // Leave home
        strings.put("tooOld", ":\"6 am already?! God dammit... I'm getting too old for this shit.\"");

        // Day 2
        strings.put("impressBoss",  ":\"Here we go again. Maybe I can impress the boss today!\"");

        // Empty Nest
        strings.put("notComingBack", ":\"... ... I guess she's really not coming back. *sigh*\"");
    }

    public static String getText(String key){
        if (strings.containsKey(key)){
            return strings.get(key);
        }

        return "Uninitialized String!!!! (" + key + ")";
    }
}
