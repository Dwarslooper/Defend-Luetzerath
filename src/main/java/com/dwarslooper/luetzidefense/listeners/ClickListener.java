package com.dwarslooper.luetzidefense.listeners;

import com.dwarslooper.luetzidefense.gui.GuiUtils;
import com.dwarslooper.luetzidefense.gui.MainGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.dwarslooper.luetzidefense.Translate.translate;


public class ClickListener implements Listener {
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onClick(InventoryClickEvent e) {

        if(checkConditions(e)) return;

        if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(translate("::gui.back"))) {
            GuiUtils.open(new MainGUI(), (Player) e.getWhoClicked());
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(translate("::gui.close"))) {
            e.getWhoClicked().closeInventory();
        }



    }

    private boolean checkConditions(InventoryClickEvent e) {
        return (e.getCurrentItem() == null || !(e.getCurrentItem().hasItemMeta()) || e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE);
    }
}
