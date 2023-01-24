package com.dwarslooper.luetzidefense.characters;

import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

public abstract class Character {

    String display;
    int id;
    int level;
    public Character(String displayName, int id, int level) {
        this.display = displayName;
        this.id = id;
        this.level = level;
    }

    public String getDisplay() {
        return display;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public abstract Villager manageEntity(Villager e);

    public abstract boolean tick(Villager e, GameLobby l);
}
