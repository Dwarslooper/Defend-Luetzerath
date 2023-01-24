package com.dwarslooper.luetzidefense.arena;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GameAssetManager {

    public static HashMap<String, GameAsset> GAME_ASSETS = new HashMap<>();
    public static HashMap<Player, GameAsset> editing_asset = new HashMap<>();

    public static GameAsset parseFromYML(String arena, ConfigurationSection cs) {

        if(cs.isSet("id") && cs.isSet("file") && cs.isSet("cost") && cs.isSet("ignoreair") && cs.isSet("location") && cs.isSet("name")) {
            String id = cs.getString("id");
            String fileName = cs.getString("file");
            int cost = cs.getInt("cost");
            boolean ignoreAir = cs.getBoolean("ignoreair");
            Location location = cs.getLocation("location");
            String name = cs.getString("name");
            return new GameAsset(arena, id, fileName, cost, ignoreAir, location, name);
        } else {
            return null;
        }

    }

    public static void register() {

    }

}
