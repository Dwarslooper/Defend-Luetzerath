package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class MainGUI extends ClickGUI{
    public MainGUI() {
        super("::gui.main");
    }

    @Override
    public Inventory open(Player player) {

        Screen screen = new Screen(3, translate(Main.PREFIX + "§2" + "::gui.main"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createHead(1, translate("::gui.characters"), translate("::gui.description.characters"), "Dwarslooper"), () -> {
                    GuiUtils.open(new CharactersGUI(), player);
                })
                .addButton(12, StackCreator.createItem(Material.GLOWSTONE_DUST, 1, translate("::gui.skills"), translate("::gui.description.skills")), () -> {
                    GuiUtils.open(new SkillsGUI(), player);
                })
                .addButton(14, StackCreator.createItem(Material.EMERALD, 1, translate("::gui.shop"), translate("::gui.description.shop")), () -> {
                    GuiUtils.open(new ShopGUI(), player);
                })
                .addButton(16, StackCreator.createItem(Material.COMPARATOR, 1, translate("::gui.settings"), translate("::gui.description.settings")), () -> {
                    GuiUtils.open(new SettingsGUI(), player);
                })
                .addButton(26, StackCreator.createItem(Material.BARRIER, 1, translate("::gui.close"), ""))
                .open(player);

        return screen.getInventory();
    }
}
