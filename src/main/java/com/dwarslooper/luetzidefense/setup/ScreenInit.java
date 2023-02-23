package com.dwarslooper.luetzidefense.setup;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import org.bukkit.Material;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class ScreenInit {

    public static void initScreens() {
        new Screen(3, translate(Main.PREFIX + "§2" + "::gui.confirm"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createItem(Material.LIME_TERRACOTTA, 1, translate("::gui.action.confirm")))
                .addButton(16, StackCreator.createItem(Material.RED_TERRACOTTA, 1, translate("::gui.action.cancel")))
                .build("confirm");
    }

    public static Screen getConfirm() {
        return new Screen(3, translate(Main.PREFIX + "§2" + "::gui.confirm"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createItem(Material.LIME_TERRACOTTA, 1, translate("::gui.action.confirm")))
                .addButton(16, StackCreator.createItem(Material.RED_TERRACOTTA, 1, translate("::gui.action.cancel")));
    }

}
