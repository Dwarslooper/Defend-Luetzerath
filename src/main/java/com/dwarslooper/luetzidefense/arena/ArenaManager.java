package com.dwarslooper.luetzidefense.arena;

import com.dwarslooper.luetzidefense.Main;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaManager {

    public static HashMap<String, Arena> ARENAS = new HashMap<>();
    public static HashMap<Player, String> currently_editing = new HashMap<>();
    public static List<String> arena_list = new ArrayList<>();

    public static void reload() {
        ARENAS.clear();

        arena_list = Main.config.getConfiguration().getConfigurationSection("arenas").getKeys(false).stream().toList();

        Set<String> arenas = Main.config.getConfiguration().getConfigurationSection("arenas").getKeys(false);

        for(String id_ : arenas) {
            ConfigurationSection arena = Main.config.getConfiguration().getConfigurationSection("arenas").getConfigurationSection(id_);

            String id = arena.getString("id");
            String name = arena.getString("name");
            Location center = arena.getLocation("center");

            ArrayList<GameAsset> gameAssets = new ArrayList<>();
            ArrayList<Location> enemySpawns = new ArrayList<>();

            ConfigurationSection assets_ = arena.getConfigurationSection("assets");
            List<Location> spawns_ = (List<Location>) arena.getList("spawns");

            if(!validate(id, name, center, assets_, spawns_)) return;

            for(String s : assets_.getKeys(false)) {
                GameAsset asset = GameAssetManager.parseFromYML(id_, Objects.requireNonNull(assets_.getConfigurationSection(s)));
                if(asset == null) continue;
                gameAssets.add(asset);
            }

            if(spawns_ != null) enemySpawns.addAll(spawns_);
            int minSpawns = Main.getInstance().getConfig().getInt("min_enemy_spawns");
            if(enemySpawns.size() < minSpawns) return;
            if(!arena.getBoolean("isfinished")) {
                Main.getInstance().getServer().getConsoleSender().sendMessage("§cArena §6" + id_ + " §chad all settings set but is not registered! To use arena you have to register it in the edit GUI!");
                return;
            }

            ARENAS.put(id_, new Arena(id, name, center, gameAssets, enemySpawns));
        }

        for(Arena a : ARENAS.values()) {
            Main.getInstance().getServer().getConsoleSender().sendMessage("Arena registered: §6" + a.getName());
        }

    }

    public static Arena getByName(String name) {
        for(Arena a : ARENAS.values()) {
            if(a.getId().equalsIgnoreCase(name)) return a;
        }
        return null;
    }

    public static ArrayList<String> getAssetList(String arena) {
        ArrayList<String> list = new ArrayList<>();
        getByName(arena).getAssets().forEach(value -> {
            list.add(value.id);
        });
        return list;
    }

    private static boolean validate(Object... values) {
        for(Object o : values) {
            if(o instanceof Collection list) {
                if(list.isEmpty()) return false;
            }
            if(o == null) return false;
        }
        return true;
    }

}
