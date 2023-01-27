package com.dwarslooper.luetzidefense.setup;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.arena.ArenaManager;

import java.io.File;
import java.util.Locale;

public class Setup {


    public static int createNew(String input) {

        String name = input.replaceAll("[^a-zA-Z0-9]", "");
        if(name.equalsIgnoreCase("")) return 2;

        String line = "arenas." + name.toLowerCase(Locale.ROOT);

        if(Main.config.get(line) != null) {
            return 1;
        }

        Main.config.set(line + ".id", name);
        Main.config.set(line + ".name", "'" + input + "'");
        Main.config.set(line + ".isfinished", false);
        Main.config.getConfiguration().createSection(line + ".assets");
        Main.config.getConfiguration().createSection(line + ".spawns");
        Main.config.getConfiguration().createSection(line + ".signs");
        new File(Main.getInstance().getDataFolder(), "/arenas/" + name + "/assets").mkdirs();

        Main.config.save();
        ArenaManager.reload();

        return 0;

    }

}
