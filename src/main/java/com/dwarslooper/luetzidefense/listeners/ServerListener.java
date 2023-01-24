package com.dwarslooper.luetzidefense.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.gui.ShopGUI;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Iterator;

import static com.dwarslooper.luetzidefense.game.LobbyHandler.GAMES;
import static com.dwarslooper.luetzidefense.game.LobbyHandler.isIngame;

public class ServerListener implements Listener {

    @EventHandler
    public void onTick(ServerTickStartEvent e) {
        for(GameLobby gl : GAMES) {
            gl.tick();
        }

        ArrayList<Player> remove = new ArrayList<>();
        LobbyHandler.interactCooldown.forEach((player, integer) -> {
            if(integer > 0) {
                LobbyHandler.interactCooldown.put(player, (integer - 1));
            } else {
                remove.add(player);
            }
        });
        remove.forEach(integer -> LobbyHandler.interactCooldown.remove(integer));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(isIngame.containsKey(e.getPlayer())) {
            isIngame.get(e.getPlayer()).handleLeft(e.getPlayer());
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player player) {
            if(LobbyHandler.isIngame.containsKey(player)) e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player player) {
            if(LobbyHandler.isIngame.containsKey(player)) {
                int pointsMultiplier = isIngame.get(player).getSkillsPoints().get(player);
                e.setDamage(e.getDamage() + LobbyHandler.isIngame.get(player).getSkillsPhysical().get(player));
                if(e.getEntity() instanceof ArmorStand) e.setCancelled(true);
                else if(LobbyHandler.isIngame.get(player).getEnemiesSpawned().contains(e.getEntity()) && e.getFinalDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
                    LobbyHandler.isIngame.get(player).addBalance(2 * (pointsMultiplier + 1));
                } else if(LobbyHandler.isIngame.get(player).getProtestersSpawned().contains(e.getEntity()) && e.getFinalDamage() >= ((LivingEntity) e.getEntity()).getHealth()) {
                    LobbyHandler.isIngame.get(player).removeBalance(4);
                }
            }
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        
    }

    @EventHandler
    public void noUproot(PlayerInteractEvent event) {
        if(LobbyHandler.isIngame.containsKey(event.getPlayer())) {
            if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }

/*
    @EventHandler
    public void onExplosion(EntityExplodeEvent e) {
        if(ShopGUI.cancelExplosion.contains(e.getEntity())) {
            e.blockList().clear();
        }
    }

 */

}
