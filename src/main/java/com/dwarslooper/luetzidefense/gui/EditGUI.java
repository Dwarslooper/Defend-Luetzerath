package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class EditGUI extends ClickGUI{
    public EditGUI() {
        super("::gui.editarena", 0);
    }

    @Override
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        String ARENA_ID = ArenaManager.currently_editing.get(player);

        ConfigurationSection section = Main.config.getConfiguration().getConfigurationSection("arenas").getConfigurationSection(ARENA_ID);

        String changeText = translate("::mapsetting.helper.change");

        String set = translate("::mapsetting.status.set");
        String unset = translate("::mapsetting.status.unset");

        String isNameSet = set;
        String isCenterSet = section.getLocation("center") != null ? set : unset;
        String isSchemSet = new File(Main.getInstance().getDataFolder(),"arenas/" + ARENA_ID + "/arena.schem").exists() ? set : unset;

        int minSpawns = Main.getInstance().getConfig().getInt("min_enemy_spawns");

        String isSignsSet = translate("::mapsetting.status.numset", "0","0");
        if(section.isSet("signs")) isSignsSet = translate("::mapsetting.status.numset", String.valueOf(section.getList("signs") != null ? Objects.requireNonNull(section.getList("signs")).size() : 0),"0");

        String isFinished = section.getBoolean("isfinished") ? set : unset;

        String isAssetsSet = translate("::mapsetting.status.numset", "0","0");
        if(section.isSet("assets")) isAssetsSet = translate("::mapsetting.status.numset", String.valueOf(section.getConfigurationSection("assets").getKeys(false).size()),"0");

        String isSpawnsSet  = "";
        List<?> spawns = section.getList("spawns");
        if(spawns == null) isSpawnsSet = translate("::mapsetting.status.numunset", "0", "" + minSpawns);
        else if(spawns.size() == 0) isSpawnsSet = translate("::mapsetting.status.numunset", "0", "" + minSpawns);
        else if(spawns.size() < minSpawns) isSpawnsSet = translate("::mapsetting.status.numunset", "" + spawns.size(), "" + minSpawns);
        else isSpawnsSet = translate("::mapsetting.status.numset", "" + spawns.size(), "" + minSpawns);

        inv.setItem(10, StackCreator.createItem(Material.ORANGE_BANNER, 1, translate("::mapsetting.center.title"), List.of(translate("::mapsetting.center.desc"), isCenterSet + changeText)));
        inv.setItem(11, StackCreator.createItem(Material.MAP, 1, translate("::mapsetting.schem.title"), List.of(translate("::mapsetting.schem.desc"), isSchemSet + changeText)));
        inv.setItem(12, StackCreator.createItem(Material.ENDER_EYE, 1, translate("::mapsetting.spawn.title"), List.of(translate("::mapsetting.spawn.desc"), isSpawnsSet + changeText)));
        inv.setItem(13, StackCreator.createItem(Material.OAK_LOG, 1, translate("::mapsetting.assets.title"), List.of(translate("::mapsetting.assets.desc"), isAssetsSet + changeText)));
        inv.setItem(14, StackCreator.createItem(Material.OAK_SIGN, 1, translate("::mapsetting.signs.title"), List.of(translate("::mapsetting.signs.desc"), translate("::mapsetting.signs.right"), translate("::mapsetting.signs.left"), isSignsSet + changeText)));
        inv.setItem(15, StackCreator.createItem(Material.FIREWORK_ROCKET, 1, translate("::mapsetting.finish.title"), List.of(translate("::mapsetting.finish.desc0"), translate("::mapsetting.finish.desc1"), isFinished + changeText)));
        //inv.setItem(12, StackCreator.createItem(Material.EMERALD, 1, translate("::gui.shop"), translate("::gui.description.shop")));

        inv.setItem(16, StackCreator.createItem(Material.RED_CONCRETE, 1, translate("::mapsetting.delete.title"), List.of(translate("::mapsetting.delete.desc"), changeText)));
        inv.setItem(26, StackCreator.createItem(Material.BARRIER, 1, translate("::gui.close"), ""));

        player.openInventory(inv);
        return inv;
    }
}
