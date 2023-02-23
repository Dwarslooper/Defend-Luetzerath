package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class ShopGUI extends ClickGUI{

    public ShopGUI() {
        super("::gui.shop");
    }

    @Override
    @SuppressWarnings("all")
    public Inventory open(Player player) {

        GameLobby gameLobby = null;
        for (GameLobby game : LobbyHandler.GAMES) {
            if(game.getPlayers().contains(player)) gameLobby = game;
        }

        ItemStack tnt = StackCreator.createItem(Material.TNT, 1, translate("::ingame.gui.shop.tnt.title"), translate("::text.cost", "44"));
        ItemStack molotow = StackCreator.createItem(Material.LINGERING_POTION, 1, translate("::ingame.gui.shop.molotow.title"), translate("::text.cost", "26"));
        ItemStack water = StackCreator.createItem(Material.WATER_BUCKET, 1, translate("::ingame.gui.shop.water.title"), translate("::text.cost", "35"));
        ItemStack buff = StackCreator.createItem(Material.SUSPICIOUS_STEW, 1, translate("::ingame.gui.shop.buff.title"), translate("::text.cost", "13"));
        ItemStack sword = StackCreator.createItem(Material.STONE_SWORD, 1, translate("::ingame.gui.shop.sword.title"), translate("::text.cost", "53"));


        GameLobby lobby = gameLobby;
        Screen screen = new Screen(3, translate(Main.PREFIX + "§2" + "::gui.shop"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .setDefaultCondition(() -> {
                    if(lobby == null) {
                        player.sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                        return false;
                    }
                    return true;
                })
                .addButton(10, tnt, () -> {
                    if(lobby.getBalance() - 44 >= 0) lobby.setBalance(lobby.getBalance() - 44);
                    else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.shop.tnt.title").replace("§c","")));
                        return;
                    }
                    player.getInventory().addItem(tnt);
                    GuiUtils.open(new SkillsGUI(), player);
                })
                .addButton(11, molotow, () -> {
                    if(lobby.getBalance() - 26 >= 0) lobby.setBalance(lobby.getBalance() - 26);
                    else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.shop.molotow.title").replace("§c","")));
                        return;
                    }
                    player.getInventory().addItem(molotow);
                })
                .addButton(12, water, () -> {
                    if(lobby.getBalance() - 35 >= 0) lobby.setBalance(lobby.getBalance() - 35);
                    else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.shop.water.title").replace("§c","")));
                        return;
                    }
                    player.getInventory().addItem(water);
                })
                .addButton(13, buff, () -> {
                    if(lobby.getBalance() - 13 >= 0) lobby.setBalance(lobby.getBalance() - 13);
                    else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.shop.buff.title").replace("§c","")));
                        return;
                    }
                    player.getInventory().addItem(buff);
                })
                .addButton(14, sword, () -> {
                    if(lobby.getBalance() - 53 >= 0) lobby.setBalance(lobby.getBalance() - 53);
                    else {
                        player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate("::ingame.gui.shop.sword.title").replace("§c","")));
                        return;
                    }
                    player.getInventory().addItem(sword);
                })
                .addButton(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")))
                .open(player);

        return screen.getInventory();
    }
}
