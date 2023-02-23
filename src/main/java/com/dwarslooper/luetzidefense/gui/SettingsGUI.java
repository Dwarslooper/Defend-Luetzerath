package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class SettingsGUI extends ClickGUI{

    public SettingsGUI() {
        super("::gui.settings");
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {

        Screen screen = new Screen(3, translate(Main.PREFIX + "§2" + "::gui.settings"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")))
                .open(player);

        return screen.getInventory();
    }
}
