package com.dwarslooper.luetzidefense.listeners;

import com.dwarslooper.luetzidefense.setup.AssetSetup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;


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
}
