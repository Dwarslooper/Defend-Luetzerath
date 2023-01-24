package com.dwarslooper.luetzidefense.gui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class GuiUtils {

    public static ClickGUI MAIN, CHARACTERS, SKILLS, SHOP, EDITARENA, CONFIRM;

    public static ArrayList<Inventory> guisOpened = new ArrayList<>();

    public static void register() {

        MAIN = new MainGUI();
        CHARACTERS = new CharactersGUI();
        SKILLS = new SkillsGUI();
        SHOP = new ShopGUI();
        EDITARENA = new EditGUI();
        CONFIRM = new ConfirmGUI("", "");

    }

    public static void open(ClickGUI gui, Player p) {
        guisOpened.add(gui.open(p));
    }

    public static String getTitle(ClickGUI gui) {
        return gui.getTitle();
    }
}
