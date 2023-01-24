package com.dwarslooper.luetzidefense.gui;


import org.bukkit.inventory.ItemStack;

public class ClickableItem extends ItemStack {

    private ItemStack itemStack;
    private String identifier;
    private int button;

    public ClickableItem(ItemStack itemStack, String identifier) {
        super(itemStack);
        this.itemStack = itemStack;
        this.identifier = identifier;
    }

    public ClickableItem(ItemStack itemStack, String identifier, int button) {
        super(itemStack);
        this.itemStack = itemStack;
        this.identifier = identifier;
        this.button = button;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getButton() {
        return button;
    }
}
