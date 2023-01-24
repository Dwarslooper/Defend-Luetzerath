package com.dwarslooper.luetzidefense;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class StackCreator {

    public static ItemStack createItem(Material mtl, int amount, String name, String lore) {
        ItemStack result = new ItemStack(mtl, amount);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> list = new ArrayList<String>();
        list.add(lore);
        meta.setLore(list);
        result.setItemMeta(meta);
        return result;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ItemStack createItem(Material mtl, int amount, String name, List lore) {
        ItemStack result = new ItemStack(mtl, amount);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        result.setItemMeta(meta);
        return result;
    }
    public static ItemStack createItem(Material mtl, int amount, String name) {
        ItemStack result = new ItemStack(mtl, amount);
        ItemMeta meta = result.getItemMeta();
        meta.setDisplayName(name);
        result.setItemMeta(meta);
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation"})
    public static ItemStack createHead(int amount, String name, ArrayList lore, String owner) {
        ItemStack result = new ItemStack(Material.PLAYER_HEAD, amount, (byte) 3);
        SkullMeta meta = (SkullMeta) result.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.setOwner(owner);
        result.setItemMeta(meta);
        return result;
    }

    public static ItemStack createHead(int amount, String name, String lore, String owner) {
        ItemStack result = new ItemStack(Material.PLAYER_HEAD, amount, (byte) 3);
        SkullMeta meta = (SkullMeta) result.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> list = new ArrayList<String>();
        list.add(lore);
        meta.setLore(list);
        meta.setOwner(owner);
        result.setItemMeta(meta);
        return result;
    }

    public static ItemStack createHead(Material orangeBanner, int i, String translate, String translate1) {
        return null;
    }
}