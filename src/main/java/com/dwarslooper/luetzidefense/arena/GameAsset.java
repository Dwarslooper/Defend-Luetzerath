package com.dwarslooper.luetzidefense.arena;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SchematicManager;
import com.dwarslooper.luetzidefense.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.io.File;

public class GameAsset {

    String arena;
    String id;
    String fileName;
    int cost;
    boolean ignoreAir;
    Location displayAt;
    String displayName;
    boolean isPlaced;
    int removeProgress;
    ArmorStand remProgBar;

    public GameAsset(String arena, String id, String fileName, int cost, boolean ignoreAir, Location displayAt, String displayName) {
        this.arena = arena;
        this.id = id;
        this.fileName = fileName;
        this.cost = cost;
        this.ignoreAir = ignoreAir;
        this.displayAt = displayAt;
        this.displayName = displayName;
        this.removeProgress = 0;
    }

    public String getArena() {
        return arena;
    }

    public String getId() {
        return id;
    }

    public boolean verifyFiles() {
        return (getFile() != null && getBrokenFile() != null);
    }

    public File getFile() {
        return ArenaManager.getByName(arena).getAssetFile(fileName);
    }

    public File getBrokenFile() {
        return ArenaManager.getByName(arena).getAssetFile(fileName.replace(".schem", "_broken.schem"));
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setRemoveProgress(int removeProgress) {
        this.removeProgress = removeProgress;
    }

    public int getRemoveProgress() {
        return removeProgress;
    }

    public ArmorStand getRemProgBar() {
        return remProgBar;
    }

    public void setRemProgBar(ArmorStand remProgBar) {
        this.remProgBar = remProgBar;
    }

    public void updateRemoveProgress(int multiplier) {
        if(remProgBar == null) {
            if((removeProgress == 1)) {
                remProgBar = (ArmorStand) getDisplayAt().getWorld().spawnEntity(getDisplayAt().clone().add(0, 0.4, 0), EntityType.ARMOR_STAND);

                remProgBar.setInvulnerable(true);
                remProgBar.setInvisible(true);
                remProgBar.setGravity(false);
                remProgBar.setSmall(true);
                remProgBar.setBasePlate(false);
            }
        } else {
            remProgBar.setCustomName(Utils.getProgressBar(removeProgress, 100, 20, "|", ChatColor.YELLOW, ChatColor.GRAY));
            remProgBar.setCustomNameVisible(true);
        }
        if(removeProgress >= 100) {
            destroy();
            removeProgress = 0;
            if(remProgBar != null) remProgBar.remove();
            remProgBar = null;
        } else if(!((removeProgress + multiplier) < 0)) {
            removeProgress += multiplier;
        } else {
            if(remProgBar != null) remProgBar.remove();
            remProgBar = null;
            removeProgress = 0;
        }
    }

    public void destroy() {
        setPlaced(false);
        SchematicManager.paste(this.getDisplayAt(), getBrokenFile(), false);
    }

    public String getFileName() {
        return fileName;
    }

    public int getCost() {
        return cost;
    }

    public boolean getIgnoreAir() {
        return ignoreAir;
    }

    public Location getDisplayAt() {
        return displayAt;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "GameAsset{" +
                "arena='" + arena + '\'' +
                ", id='" + id + '\'' +
                ", fileName='" + fileName + '\'' +
                ", cost=" + cost +
                ", ignoreAir=" + ignoreAir +
                ", displayAt=" + displayAt +
                ", displayName='" + displayName + '\'' +
                '}';
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setDisplayAt(Location displayAt) {
        this.displayAt = displayAt;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIgnoreAir(boolean ignoreAir) {
        this.ignoreAir = ignoreAir;
    }
}
