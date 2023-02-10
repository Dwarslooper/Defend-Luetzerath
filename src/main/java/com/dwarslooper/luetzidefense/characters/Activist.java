package com.dwarslooper.luetzidefense.characters;

import org.bukkit.entity.Entity;

public abstract class Activist extends Character {

    int cost;

    public Activist(String displayName, int id, int level, int cost) {
        super(displayName, id, level);
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}
