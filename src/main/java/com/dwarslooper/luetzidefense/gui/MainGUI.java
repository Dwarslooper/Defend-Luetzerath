package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class MainGUI extends ClickGUI{
    public MainGUI() {
        super("::gui.main", 0);
    }

    @Override
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        inv.setItem(10, StackCreator.createHead(1, translate("::gui.characters"), translate("::gui.description.characters"), "Dwarslooper"));
        inv.setItem(12, StackCreator.createItem(Material.GLOWSTONE_DUST, 1, translate("::gui.skills"), translate("::gui.description.skills")));
        inv.setItem(14, StackCreator.createItem(Material.EMERALD, 1, translate("::gui.shop"), translate("::gui.description.shop")));
        inv.setItem(16, StackCreator.createItem(Material.COMPARATOR, 1, translate("::gui.settings"), translate("::gui.description.settings")));
        inv.setItem(26, StackCreator.createItem(Material.BARRIER, 1, translate("::gui.close"), ""));

        player.openInventory(inv);
        return inv;
    }
}
