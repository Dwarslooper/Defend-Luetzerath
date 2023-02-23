package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.characters.Character;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class CharactersGUI extends ClickGUI{

    public CharactersGUI() {
        super("::gui.characters");
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Screen screen = new Screen(3, translate(Main.PREFIX + "§2" + "::gui.characters")).setBackground(Material.BLACK_STAINED_GLASS_PANE);

        int currentSlot = 10;

        List<Character> characters = Main.CM.getCharacters();

        for (Character character : characters) {
            if(!(character instanceof Activist activist)) continue;
            ArrayList<String> lore = new ArrayList<>();
            lore.add(translate("§r§a::text.cost", "" + activist.getCost()));
            lore.add(translate("§r§a::text.skills"));
            screen.addButton(currentSlot, StackCreator.createItem(Material.PLAYER_HEAD, 1, "§r§e" + translate(activist.getDisplay()), lore), () -> {
                GameLobby lobby = null;
                for (GameLobby game : LobbyHandler.GAMES) {
                    if(game.getPlayers().contains(player)) lobby = game;
                }

                if(lobby == null) {
                    player.sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                    return;
                }

                GameLobby finalLobby = lobby;

                if(finalLobby.getBalance() >= activist.getCost()) {
                    Villager e0 = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                    if (character.manageEntity(e0) == null) {
                        Main.getInstance().getServer().getConsoleSender().sendMessage(Main.PREFIX + "§cWarning! Changes have been made to characters, but character §6" + character.getDisplay().replaceFirst("::", "") + " §creturned null when initializing! Maybe the §fmanageEntity() §cmethod is not configured properly?!");
                    }
                    finalLobby.getProtestersSpawned().add(e0);
                    finalLobby.getDeleteOnReset().add(e0);
                    Main.CM.getBounds().put(e0, character);
                    finalLobby.removeBalance(activist.getCost());
                } else {
                    player.sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate(activist.getDisplay())));
                }

            });
            currentSlot++;
        }

        screen.addButton(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")));
        screen.open(player);

        return screen.getInventory();
    }
}
