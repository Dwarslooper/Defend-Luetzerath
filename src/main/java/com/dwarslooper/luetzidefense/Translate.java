package com.dwarslooper.luetzidefense;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Translate {

    public static Config language;
    private static Set<String> keys;
    private static final HashMap<String, String> translatable = new HashMap<>();
    public static File folder;
    public static ArrayList<String> enemyMessages;
    public static ArrayList<String> fellowMessages;

    public static String translate(String identifier) {
        String translated;
        translated = identifier;

        for(String s : translatable.keySet()) {
            translated = translated.replace(s, translatable.get(s));
        }

        return translated;

    }

    public static String translate(String identifier, String... args) {

        String string = translate(identifier);

        for(String s : args) {
            string = string.replaceFirst("%s%", s);
        }

        return string;
    }

    public static void reload() {

        folder = new File(Main.getInstance().getDataFolder().getAbsolutePath() + "/lang");
        if(!folder.exists()) folder.mkdirs();
        String currentLang = Main.getInstance().getConfig().getString("language");
        File test = new File(folder, currentLang + ".yml");
        if(!test.exists()) return;
        language = new Config(currentLang + ".yml", folder);
        language.reload();

        translatable.clear();
        keys = language.getConfiguration().getKeys(true);
        for(String s : keys) {
            String value = language.getConfiguration().getString(s);
            if(value.contains("root='YamlConfiguration'")) {
                continue;
            }
            translatable.put("::" + s, value);
        }
    }

    public static void debug() {
        Main.LOGGER.info("Started LANG debug. isEmpty?: " + translatable.isEmpty());
        for(String s : translatable.keySet()) {
            Main.LOGGER.info(s + " -> " + translatable.get(s));
        }
    }

    public static void init() {

        // Add default translations here. If you plan on committing your own language to the plugin, you
        // dont have to change anything in here.

        Main.getInstance().saveResource("lang/en_us.yml", false);
        Main.getInstance().saveResource("lang/de_de.yml", false);
        Main.getInstance().saveResource("lang/la_ng.yml", false);
        reload();

        enemyMessages = (ArrayList<String>) language.getConfiguration().getList("ingame.talk.enemy");
        fellowMessages = (ArrayList<String>) language.getConfiguration().getList("ingame.talk.fellow");

    }

    public static ArrayList<String> getEnemyMessages() {
        return enemyMessages;
    }

    public static ArrayList<String> getFellowMessages() {
        return fellowMessages;
    }
}
