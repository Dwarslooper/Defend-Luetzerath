package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.Utils;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class SkillsGUI extends ClickGUI{

    public SkillsGUI() {
        super("::gui.skills", 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        GameLobby lobby = null;
        for (GameLobby game : LobbyHandler.GAMES) {
            if(game.getPlayers().contains(player)) lobby = game;
        }

        if(lobby == null) {
            player.sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
        } else {
            inv.setItem(10, StackCreator.createItem(Material.IRON_SWORD, 1, translate("::ingame.gui.skills.pe.title"), List.of(translate("::text.cost", "22"), translate("::ingame.gui.skills.pe.desc"), Utils.getProgressBar(lobby.getSkillsPhysical().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))));
            inv.setItem(11, StackCreator.createItem(Material.EXPERIENCE_BOTTLE, 1, translate("::ingame.gui.skills.points.title"), List.of(translate("::text.cost", "35"), translate("::ingame.gui.skills.points.desc"), Utils.getProgressBar(lobby.getSkillsPoints().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))));
            inv.setItem(12, StackCreator.createItem(Material.FEATHER, 1, translate("::ingame.gui.skills.speed.title"), List.of(translate("::text.cost", "13"), translate("::ingame.gui.skills.speed.desc"), Utils.getProgressBar(lobby.getSkillsSpeed().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))));
            inv.setItem(13, StackCreator.createItem(Material.ENDER_PEARL, 1, translate("::ingame.gui.skills.throw.title"), List.of(translate("::text.cost", "17"), translate("::ingame.gui.skills.throw.desc"), Utils.getProgressBar(lobby.getSkillsThrow().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))));

            inv.setItem(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")));

            player.openInventory(inv);
        }
        return inv;
    }
}
