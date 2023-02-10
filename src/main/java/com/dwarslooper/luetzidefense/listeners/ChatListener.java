package com.dwarslooper.luetzidefense.listeners;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SettingManager;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.setup.AssetSetup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static com.dwarslooper.luetzidefense.Translate.translate;


public class ChatListener implements Listener {
    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if(AssetSetup.setupSteps.containsKey(e.getPlayer())) {
            if(e.getMessage().startsWith("/")) return;
            e.setCancelled(true);
            if(e.getMessage().equalsIgnoreCase("exit")) {
                AssetSetup.exit(e.getPlayer(), false);
                return;
            }
            AssetSetup.next(e.getPlayer(), e.getMessage());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String command = e.getMessage().substring(1);
        int index = command.indexOf(' ');
        command = (index >= 0 ? command.substring(0, index) : command);
        for(String msg : SettingManager.blockedCommands) {
            if(command.equalsIgnoreCase(msg)) {
                e.getPlayer().sendMessage(Main.PREFIX + translate("::ingame.blocked_command"));
                e.setCancelled(true);
                return;
            }
        }

        if(SettingManager.shortCommands) {
            String msgRaw = e.getMessage().replaceFirst("/", "");
            Main.LOGGER.info(msgRaw);
            if(msgRaw.equalsIgnoreCase("start") || msgRaw.equalsIgnoreCase("leave") || msgRaw.equalsIgnoreCase("reset")) {
                e.setCancelled(true);
                boolean found = false;
                String id = "";
                for (GameLobby game : LobbyHandler.GAMES) {
                    if (game.getPlayers().contains(e.getPlayer())) {
                        found = true;
                        id = game.getArena().getId();
                    }
                }
                if (found) e.getPlayer().chat("/dl game " + id + " " + e.getMessage().replaceFirst("/", ""));
                else e.getPlayer().sendMessage(Main.PREFIX + translate("::command.short.only_ingame"));
            }
        }

    }
}
