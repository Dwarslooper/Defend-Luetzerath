package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class SettingsGUI extends ClickGUI{

    public SettingsGUI() {
        super("::gui.settings", 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        inv.setItem(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")));

        player.openInventory(inv);
        return inv;
    }
}
