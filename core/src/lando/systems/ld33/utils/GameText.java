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
        strings.put("foremanLate", "Foreman:\"Late again Billy! Move left and get into position!\"");
        strings.put("hereComesMario", "Foreman:\"All right everyone! Here he comes, look sharp\"");
        strings.put("headHome", "Foreman:\"Looks like you took a nasty knock there... "
                + "Why don't you head home to your family.\"");

        // Getting Home
        strings.put("playerLate", strings.get("playerName") + ": \"Damn it! I'm late getting home again. Misty is going to be furious!\"");

        // See the Wife
        strings.put("wifeBitching", strings.get("wifeName") + ":\"What the hell! Injured on the job and late again?! "
                + "That's it, I'm leaving with the kids!\"");
        strings.put("playerStammer", strings.get("playerName") + ":\"but... but I...\"");
        strings.put("playerDontGo", strings.get("playerName") + ":\"Wait! Don't leave!\"");
        strings.put("wrangleKids", strings.get("wifeName") + ":\"Come here kids!\"");
        strings.put("noTimeForThis", strings.get("playerName") + ":\"Fine, I don't have time for this anyway. I need to get up early tomorrow for work again.\"");

        // Leave home
        strings.put("tooOld", strings.get("playerName") + ":\"6 am already?! God dammit... I'm getting too old for this shit.\"");
        strings.put("missMisty", strings.get("playerName") + ":\"... ... I miss Misty.\"");

        // Day 2
        strings.put("impressBoss", strings.get("playerName") + ":\"If I impress the boss today and get a raise, Misty is sure to come home then!\"");
        strings.put("injuredAgain", "Foreman:\"Injured again? Alright, head home and see your family.\"");

        // Heading to empty house
        strings.put("mistyHope", strings.get("playerName") + ":\"Cheer up Billy, she'll be back. She always comes back.\"");

        // Empty Nest
        strings.put("ellipses", strings.get("playerName") + ":\"... ... ...\"");
        strings.put("notComingBack", strings.get("playerName") + ":\"I guess she's really not coming back this time.\"");

        // Get Mushroom Day
        strings.put("gotMushroom1", strings.get("playerName") + ":\"What's happening?!?\"");
        strings.put("gotMushroom2", strings.get("playerName") + ":\"I feel strange, but also good.\"");
        strings.put("gotMushroom3", strings.get("playerName") + ":\"The world seems so different. My feet feel so much stronger!\"");
        strings.put("gotMushroom4", strings.get("playerName") + ":\"I wonder if I can move to the right now? Maybe I can even jump!\"");
        strings.put("noGoingBack1", strings.get("playerName") + ":\"There is nothing left for me to go home to.  Maybe I should use my new powers to explore.\"");
        strings.put("noGoingBack2", strings.get("playerName") + ":\"I wonder where this other restricted tube goes?\"");

        //BridgeLevel
        strings.put("atCastle1", strings.get("playerName") + ": \"So this is the end of the road.");
        strings.put("atCastle2", strings.get("playerName") + ": \"I wonder what's in this place.\"");
        strings.put("atCastle3", strings.get("playerName") + ": \"Only one way to find out I suppose... Onward!\"");

        //Factory
        strings.put("intoFactory", strings.get("playerName") + ":\"What... what is this place?\"");
        strings.put("factoryNoise", strings.get("playerName") + ":\"...and what is that shaking?\"");
        strings.put("marioScreenWTF", strings.get("playerName") + ":\"Now that is damn strange.\"");
        strings.put("marioTubesWTF", strings.get("playerName") + ":\"Just when I thought I'd seen everything.\"");
        strings.put("marioGrinderWTF", strings.get("playerName") + ":\"I think I'm going to be sick.\"");

        // Respawn
        strings.put("respawn", "Please be more careful, this is Ludum Dare so we won't make you start again.");

        // Level Intros
        strings.put("level2Intro", strings.get("playerName") + ":\"Well now, this is... different.\"");
        strings.put("level3Intro", strings.get("playerName") + ":\"Where am I?\"");
        strings.put("level4Intro", strings.get("playerName") + ":\"What a strange place.\"");
        strings.put("level5Intro", strings.get("playerName") + ":\"I must be getting close to something important by now.\"");

        // Cult room
        strings.put("cultFlavor", "  [Sounds of Chanting in Latin]");
        strings.put("cultEnter", strings.get("playerName") + ":\"This is rather creepy...\"");
        strings.put("cultChant", "Cultists (chanting): \"LUDUM DARE... LUDUM DARE... LUDUM DARE... LUDUM DARE...\"");

        strings.put("cultCenter1", "Gannon: \"You made it to our inner sanctum and now you know our secrets.\"");
        strings.put("cultCenter2", "King Hippo: \"Now you can never return to your family.\"");
        strings.put("cultCenter3", "Mother Brain:\"[bubble noise]\"");
        strings.put("cultCenter4", "Dracula: \"Blood tastes good!\"");
        strings.put("cultCenter5", "Luigi: \"Yes Dracula, we know...\"");
        strings.put("cultCenter6", "Dr. Wily: \"Here, take this cape and join us.\"");

        strings.put("cultReflection1", strings.get("playerName") + ": Is this what I've been waiting for my whole life?");
        strings.put("cultReflection2", strings.get("playerName") + ": It could be just the thing to provide a sense of meaning... of purpose...");
        strings.put("cultReflection3", strings.get("playerName") + ": Finally... a place to belong! A group of friends!     A family!");
        strings.put("cultReflection4", strings.get("playerName") + ":\"... ... ...\"");
        strings.put("cultReflection5", strings.get("playerName") + ":\"I'll do it!\"");
        strings.put("cultReflection6", strings.get("playerName") + ":\"Hand over that cloak, let's do this thing!\"");

        // The End
        strings.put("theEnd1", "Developers: \"Congratulations on becoming the ultimate monster, and thanks for playing!\"");
        strings.put("theEnd2", "Created for LD33 by:  Brian Ploeckelman, Doug Graham, Colin Kennedy and Ian McNamara ");
        strings.put("gameOver", "GAME OVER");
    }

    public static String getText(String key){
        if (strings.containsKey(key)){
            return strings.get(key);
        }

        return "Uninitialized String!!!! (" + key + ")";
    }
}
