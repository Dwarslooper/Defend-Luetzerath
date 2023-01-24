package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class ConfirmGUI extends ClickGUI{

    String toConfirm;
    String action;

    public ConfirmGUI(String toConfirm, String action) {
        super("::gui.confirm", 1);
        this.toConfirm = toConfirm;
        this.action = action;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        inv.setItem(10, StackCreator.createItem(Material.LIME_TERRACOTTA, 1, translate("::gui.action.confirm")));
        inv.setItem(16, StackCreator.createItem(Material.RED_TERRACOTTA, 1, translate("::gui.action.cancel")));
        inv.setItem(13, StackCreator.createItem(Material.PAPER, 1, toConfirm, action));



        player.openInventory(inv);
        return inv;
    }
}
