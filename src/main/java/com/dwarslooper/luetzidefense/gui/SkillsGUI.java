package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.Utils;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class SkillsGUI extends ClickGUI{

    public SkillsGUI() {
        super("::gui.skills");
    }

    Player player;

    @Override
    @SuppressWarnings("all")
    public Inventory open(Player player) {

        this.player = player;

        GameLobby gameLobby = null;
        for (GameLobby game : LobbyHandler.GAMES) {
            if(game.getPlayers().contains(player)) gameLobby = game;
        }

        GameLobby lobby = gameLobby;
        Screen screen = new Screen(3, translate(Main.PREFIX + "§2" + super.getTitle()))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .setDefaultCondition(() -> {
                    if (lobby == null) {
                        player.sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                        return false;
                    }
                    return true;
                })
                .addButton(10, StackCreator.createItem(Material.IRON_SWORD, 1, translate("::ingame.gui.skills.pe.title"), List.of(translate("::text.cost", "22"), translate("::ingame.gui.skills.pe.desc"), Utils.getProgressBar(lobby.getSkillsPhysical().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))), () -> {
                    if (lobby.getSkillsPhysical().get(player) == 10)
                        player.sendMessage(Main.PREFIX + translate("::ingame.gui.skills.max_level"));
                    else if (lobby.getBalance() - 22 >= 0) {
                        lobby.removeBalance(22);
                        lobby.getSkillsPhysical().put(player, lobby.getSkillsPhysical().get(player) + 1);
                        refresh();
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.skills.pe.title").replace("§c", "")));
                    }
                })
                .addButton(11, StackCreator.createItem(Material.EXPERIENCE_BOTTLE, 1, translate("::ingame.gui.skills.points.title"), List.of(translate("::text.cost", "35"), translate("::ingame.gui.skills.points.desc"), Utils.getProgressBar(lobby.getSkillsPoints().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))), () -> {
                    if(lobby.getSkillsPoints().get(player) == 10) player.sendMessage(Main.PREFIX + translate("::ingame.gui.skills.max_level"));
                    else if(lobby.getBalance() - 35 >= 0) {
                        lobby.removeBalance(35);
                        lobby.getSkillsPoints().put(player, lobby.getSkillsPoints().get(player) + 1);
                        refresh();
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.skills.points.title").replace("§c","")));
                    }
                })
                .addButton(12, StackCreator.createItem(Material.FEATHER, 1, translate("::ingame.gui.skills.speed.title"), List.of(translate("::text.cost", "13"), translate("::ingame.gui.skills.speed.desc"), Utils.getProgressBar(lobby.getSkillsSpeed().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))), () -> {
                    if(lobby.getSkillsSpeed().get(player) == 10) player.sendMessage(Main.PREFIX + translate("::ingame.gui.skills.max_level"));
                    else if(lobby.getBalance() - 13 >= 0) {
                        lobby.removeBalance(13);
                        lobby.getSkillsSpeed().put(player, lobby.getSkillsSpeed().get(player) + 1);
                        refresh();
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.skills.speed.title").replace("§c","")));
                    }
                })
                .addButton(13, StackCreator.createItem(Material.ENDER_PEARL, 1, translate("::ingame.gui.skills.throw.title"), List.of(translate("::text.cost", "17"), translate("::ingame.gui.skills.throw.desc"), Utils.getProgressBar(lobby.getSkillsThrow().get(player), 10, 10, "※", ChatColor.YELLOW, ChatColor.GRAY))), () -> {
                    if(lobby.getSkillsThrow().get(player) == 10) player.sendMessage(Main.PREFIX + translate("::ingame.gui.skills.max_level"));
                    else if(lobby.getBalance() - 17 >= 0) {
                        lobby.removeBalance(17);
                        lobby.getSkillsThrow().put(player, lobby.getSkillsThrow().get(player) + 1);
                        refresh();
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.skills.throw.title").replace("§c","")));
                    }
                })
                .addButton(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")))
                /*

                .setDefaultClickAction(Screen.Position.RETURN, () -> {
                    refresh();
                })

                 */
                .open(player);

        return screen.getInventory();
    }

    private void refresh() {
        GuiUtils.open(new SkillsGUI(), this.player);
    }
}
