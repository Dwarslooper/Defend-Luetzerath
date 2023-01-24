package com.dwarslooper.luetzidefense.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class ClickGUI {

    String title;
    int id;

    public ClickGUI(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("deprecation")
    public abstract Inventory open(Player player);
}
