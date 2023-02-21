package com.dwarslooper.luetzidefense.arena;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Utils;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class Arena {

    String id;
    String name;
    Location center;
    ArrayList<GameAsset> assets;
    ArrayList<Location> enemySpawns;
    ArrayList<Location> signs;
    int status;

    public Arena(String id, String name, Location center, ArrayList<GameAsset> assets, ArrayList<Location> enemySpawns) {
        this.id = id;
        this.name = name;
        this.center = center;
        this.assets = assets;
        this.enemySpawns = enemySpawns;
        this.signs = new ArrayList<>();
        updateSignList();
    }

    public File getFile() {
        return new File(getFolder().getAbsolutePath() + "/arena.schem");
    }

    public ConfigurationSection getConfig() {
        return Main.config.getConfiguration().getConfigurationSection("arenas." + getId());
    }

    public void saveConfig() {
        Main.config.save();
    }

    public ArrayList<Location> getSigns() {
        return signs;
    }

    private void updateSignList() {
        if(getConfig().getList("signs") == null) return;
        signs.clear();
        getConfig().getList("signs").forEach(sign -> signs.add((Location) sign));
    }

    public boolean removeSign(Location location) {
        if(getConfig().getList("signs") == null) return false;
        if(getConfig().getList("signs").contains(location)) {
            getConfig().getList("signs").remove(location);
            saveConfig();
            return true;
        } else {
            return false;
        }
    }

    public boolean addSign(Location location) {
        ArrayList<Location> list = new ArrayList<>();
        if(getConfig().getList("signs") != null) list = (ArrayList<Location>) getConfig().getList("signs");
        if(list.contains(location)) return false;
        list.add(location);
        getConfig().set("signs", list);
        saveConfig();
        return true;
    }

    public void setStatus(int status) {
        this.status = status;
        getConfig().set("status", status);
        saveConfig();
        updateSigns();
    }

    public void updateSigns() {
        updateSignList();
        for(Location loc : getSigns()) {
            if(loc.getBlock().getState() instanceof Sign sign) {
                sign.setLine(0, getName());
                sign.setLine(1, "===============");
                sign.setLine(2, getStatusTranslation());
                sign.setLine(3, (getStatus() == 1 || getStatus() == 2) ? String.valueOf(LobbyHandler.gameUsesArena(LobbyHandler.GAMES, this).getPlayers().size()) : "");
                sign.update();
            } else {
                removeSign(loc);
            }
        }
    }

    public int getStatus() {
        return status;
    }

    public String getStatusTranslation() {
        if(status == 0) return translate("::game.status.inactive");
        if(status == 1) return translate("::game.status.waiting");
        else if(status == 2) return translate("::game.status.running");
        else if(status == 3) return translate("::game.status.resetting");
        else return translate("::game.status.unknown");
    }

    public GameAsset getAsset(String id) {
        for(GameAsset asset : getAssets()) {
            if(asset.id.equalsIgnoreCase(id)) return asset;
        }
        return null;
    }

    public File getAssetFile(String id) {
        File f = new File(getFolder().getAbsolutePath() + "/assets/" + id);
        return f.exists() ? f : null;
    }

    public File getFolder() {
        return new File(Main.getInstance().getDataFolder(), "arenas/" + getId());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return Utils.replaceLast(name.replaceFirst("'", ""), "'", "");
    }

    public Location getCenter() {
        return center;
    }

    public ArrayList<GameAsset> getAssets() {
        return assets;
    }

    public ArrayList<Location> getEnemySpawns() {
        return enemySpawns;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", center=" + center +
                ", assets=" + assets +
                ", enemySpawns=" + enemySpawns +
                '}';
    }
}
