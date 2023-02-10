package com.dwarslooper.luetzidefense;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class SettingManager {

    public static boolean notifyDeath = false;
    public static boolean shortCommands = false;
    public static boolean inventoryManager = true;
    public static int pointsOnKill = 2;
    public static int pointsOnDeath = 4;
    public static boolean pickupMud = true;
    public static int pointsFromAssets = 1;
    public static int spawnDelay = 80;
    public static int assetGeneratePointsDelay = 40;
    public static List<String> blockedCommands = new ArrayList<>();

    private final Map<Option, Object> options = new HashMap<>();

    private void loadOptions() {
        for(Option option : Option.values()) {
            options.put(option, Main.getInstance().getConfig().get(option.getPath(), option.getDefault()));
        }
    }

    public enum Option {
        NOTIFY_DEATH("notify-death", true),


        ;
        private final String path;
        private final boolean def;

        Option(String path, boolean def) {
            this.path = path;
            this.def = def;
        }

        public String getPath() {
            return path;
        }

        /**
         * @return default value of option if absent in config
         */
        public boolean getDefault() {
            return def;
        }
    }

    public static boolean checkConfig() {
        FileConfiguration config = Main.getInstance().getConfig();

        try {
            config.getString("language");
            config.getInt("min_enemy_spawns");
            config.getInt("difficulty");
            config.getInt("ratelimit");
            config.getBoolean("notify-death");
            config.getBoolean("short-commands");
            config.getBoolean("inventory-manager");
            config.get("blocked-commands");
            config.getBoolean("has-init");
            config.getInt("version");
            Main.LOGGER.info("all config things set!");
            return true;

        } catch (Exception e) {
            Main.LOGGER.info("nah something's broken");
            e.printStackTrace();
            Main.getInstance().getServer().getConsoleSender().sendMessage(Main.PREFIX + "§cThere was an error in the config! The config file has been reset to default!");
            Main.getInstance().saveResource("config.yml", true);
            Main.getInstance().saveConfig();
            return false;
        }

    }

    public static void loadSettings() {
        SettingManager.notifyDeath = Main.getInstance().getConfig().getBoolean("notify-death", true);
        SettingManager.shortCommands = Main.getInstance().getConfig().getBoolean("short-commands", false);
        SettingManager.inventoryManager = Main.getInstance().getConfig().getBoolean("inventory-manager", true);
        SettingManager.pointsOnKill = Main.getInstance().getConfig().getInt("points-on-kill", 2);
        SettingManager.pointsOnDeath = Main.getInstance().getConfig().getInt("points-on-death", 4);
        SettingManager.pickupMud = Main.getInstance().getConfig().getBoolean("pickup-mud", true);
        SettingManager.pointsFromAssets = Main.getInstance().getConfig().getInt("points-from-assets", 1);
        SettingManager.spawnDelay = Main.getInstance().getConfig().getInt("spawn-delay", 80);
        SettingManager.assetGeneratePointsDelay = Main.getInstance().getConfig().getInt("asset-generate-points-delay", 40);

        SettingManager.blockedCommands.clear();

        if(Main.getInstance().getConfig().isList("blocked-commands")) {
            for(Object s : Objects.requireNonNull(Main.getInstance().getConfig().getList("blocked-commands"))) {
                if(s instanceof String string) {
                    SettingManager.blockedCommands.add(string);
                }
            }
        }
    }

}
