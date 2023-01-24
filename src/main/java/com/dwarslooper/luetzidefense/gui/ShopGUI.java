package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class ShopGUI extends ClickGUI{

    public ShopGUI() {
        super("::gui.shop", 1);
    }

    public static ArrayList<Entity> cancelExplosion = new ArrayList<>();

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        inv.setItem(10, StackCreator.createItem(Material.TNT, 1, translate("::ingame.gui.shop.tnt.title"), translate("::text.cost", "44")));
        inv.setItem(11, StackCreator.createItem(Material.LINGERING_POTION, 1, translate("::ingame.gui.shop.molotow.title"), translate("::text.cost", "26")));
        inv.setItem(12, StackCreator.createItem(Material.WATER_BUCKET, 1, translate("::ingame.gui.shop.water.title"), translate("::text.cost", "35")));
        inv.setItem(13, StackCreator.createItem(Material.SUSPICIOUS_STEW, 1, translate("::ingame.gui.shop.buff.title"), translate("::text.cost", "13")));
        inv.setItem(14, StackCreator.createItem(Material.STONE_SWORD, 1, translate("::ingame.gui.shop.sword.title"), translate("::text.cost", "53")));

        inv.setItem(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")));



        player.openInventory(inv);
        return inv;
    }
}
