package com.dwarslooper.luetzidefense.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class ClickGUI {

    String title;

    public ClickGUI(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract Inventory open(Player player);
}
