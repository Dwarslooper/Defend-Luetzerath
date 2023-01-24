package com.dwarslooper.luetzidefense.listeners;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SchematicManager;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.Translate;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import com.dwarslooper.luetzidefense.arena.GameAssetManager;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.gui.GuiUtils;
import com.dwarslooper.luetzidefense.gui.MainGUI;
import com.dwarslooper.luetzidefense.gui.ShopGUI;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.dwarslooper.luetzidefense.Translate.translate;
import static com.dwarslooper.luetzidefense.game.LobbyHandler.isIngame;

public class InteractListener implements Listener {

    public static HashMap<Item, Player> itemThrower = new HashMap<>();

    @EventHandler
    public void onBlockBroken(BlockBreakEvent e) {
        if(isIngame.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvClickSlot(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player player) {
            if(isIngame.containsKey(player)) {
                if(isIngame.get(player).getArena().getStatus() == 1) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(isIngame.containsKey(e.getPlayer())) {
            if(isIngame.get(e.getPlayer()).getArena().getStatus() == 1) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        if(isIngame.containsKey(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(isIngame.containsKey(e.getPlayer())) {
            GameLobby lobby = isIngame.get(e.getPlayer());

            if(isIngame.get(e.getPlayer()).getArena().getStatus() == 1) {
                if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                        LobbyHandler.startGame(isIngame.get(e.getPlayer()));
                    } else if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.RED_BED) {
                        isIngame.get(e.getPlayer()).handleLeft(e.getPlayer());
                    }
                }
                e.setCancelled(true);
            }
            else if(isIngame.get(e.getPlayer()).getArena().getStatus() == 2) {

                if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if(e.getClickedBlock().getType() == Material.MUD && e.getPlayer().getInventory().getItemInMainHand().getType().isEmpty()) {
                        e.getPlayer().getInventory().addItem(StackCreator.createItem(Material.MUD, 1, translate("::ingame.gui.shop.mud.title")));
                    }
                }

                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.TNT) {
                        removeFromMainhand(e.getPlayer());
                        e.setCancelled(true);
                        Item bomb = e.getPlayer().getLocation().getWorld().dropItem(e.getPlayer().getLocation().clone().add(0, 1, 0), StackCreator.createItem(Material.TNT, 1, "Bomb", "KABOOM!"));
                        bomb.setPickupDelay(160);
                        bomb.setCustomNameVisible(true);
                        bomb.setCanPlayerPickup(false);
                        bomb.setCanMobPickup(false);
                        bomb.setHealth(1000);
                        bomb.setVelocity((e.getPlayer().getLocation().getDirection().multiply(0.5)).multiply(lobby.getSkillsThrow().get(e.getPlayer())));
                        ShopGUI.cancelExplosion.add(bomb);
                        lobby.getDeleteOnReset().add(bomb);
                        e.setCancelled(true);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            bomb.getWorld().createExplosion(bomb.getLocation(), 2, false, false);
                            bomb.getNearbyEntities(4, 4, 4).forEach(entity -> {
                                if (entity.isDead()) {
                                    lobby.addBalance(2);
                                }
                            });
                            bomb.remove();
                        }, 40);
                    } else if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.LINGERING_POTION) {
                        removeFromMainhand(e.getPlayer());
                        e.setCancelled(true);
                        Item bomb = e.getPlayer().getLocation().getWorld().dropItem(e.getPlayer().getLocation().clone().add(0, 1, 0), StackCreator.createItem(Material.LINGERING_POTION, 1, "Molotow", "KABOOM!"));
                        bomb.setPickupDelay(160);
                        bomb.setCustomNameVisible(true);
                        bomb.setCanPlayerPickup(false);
                        bomb.setCanMobPickup(false);
                        bomb.setHealth(1000);
                        bomb.setFireTicks(2000);
                        bomb.setVelocity((e.getPlayer().getLocation().getDirection().multiply(0.5)).multiply(lobby.getSkillsThrow().get(e.getPlayer())));
                        e.setCancelled(true);
                        lobby.getDeleteOnReset().add(bomb);

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            bomb.getWorld().createExplosion(bomb.getLocation(), 4, true, false);
                            bomb.getNearbyEntities(6, 6, 6).forEach(entity -> {
                                entity.setFireTicks(200);
                                if (entity.isDead()) {
                                    lobby.addBalance(2);
                                }
                            });
                            bomb.remove();
                        }, 40);

                    } else if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.SUSPICIOUS_STEW) {
                        removeFromMainhand(e.getPlayer());
                        e.setCancelled(true);
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 3));
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 80, 3));
                    }
                }

                if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.MUD) {

                        removeFromMainhand(e.getPlayer());
                        Item bomb = e.getPlayer().getLocation().getWorld().dropItem(e.getPlayer().getLocation().clone().add(0, 1, 0), StackCreator.createItem(Material.MUD, 1, "Mud", "If you read this u are cool!"));
                        bomb.setPickupDelay(160);
                        bomb.setCustomNameVisible(true);
                        bomb.setCanPlayerPickup(false);
                        bomb.setCanMobPickup(false);
                        bomb.setHealth(1000);
                        bomb.setVelocity((e.getPlayer().getLocation().getDirection().multiply(0.5)).multiply(lobby.getSkillsThrow().get(e.getPlayer())));
                        e.setCancelled(true);
                        lobby.getDeleteOnReset().add(bomb);
                        itemThrower.put(bomb, e.getPlayer());

                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            itemThrower.remove(bomb);
                            bomb.remove();
                        }, 40);

                        new BukkitRunnable() {
                            int secs = 0;
                            @Override
                            public void run() {
                                secs++;
                                for (Entity e : bomb.getNearbyEntities(0.2, 0.2, 0.2)) {
                                    if (e instanceof Player p) {
                                        if (itemThrower.get(bomb) == p) return;
                                        p.damage(2, bomb);
                                        itemThrower.remove(bomb);
                                        bomb.remove();
                                        this.cancel();
                                    } else {
                                        if (e instanceof LivingEntity entity) {
                                            entity.damage(2, bomb);
                                            if (entity.isDead()) {
                                                if (lobby.getProtestersSpawned().contains(entity))
                                                    lobby.removeBalance(4);
                                                else if (lobby.getEnemiesSpawned().contains(entity))
                                                    lobby.addBalance(2);
                                            }
                                            bomb.remove();
                                            this.cancel();
                                        }
                                    }
                                }
                                if (secs == 40) {
                                    bomb.remove();
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 1);
                    }
                }
            }
        }
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getClickedBlock().getState() instanceof Sign sign) {
                for(Arena a : ArenaManager.ARENAS.values()) {
                    if(a.getSigns().contains(sign.getLocation())) {
                        if(LobbyHandler.gameUsesArena(LobbyHandler.GAMES, a) == null) {
                            LobbyHandler.createGame(a, e.getPlayer());
                            e.getPlayer().chat("/dl game " + a.getId() + " join");
                        } else {
                            e.getPlayer().chat("/dl game " + a.getId() + " join");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityUse(PlayerInteractEntityEvent e) {
        if(e.getRightClicked() instanceof Villager villager) {
            LobbyHandler.GAMES.forEach(gameLobby -> {
                if(gameLobby.getEnemiesSpawned().contains(villager)) {
                    e.setCancelled(true);
                     if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET) {
                         gameLobby.getRemoveAssets().remove(villager);
                         removeFromMainhand(e.getPlayer());
                         e.getPlayer().sendMessage(villager.getCustomName() + "§8: §f" + translate("::ingame.talk.water"));
                         villager.getWorld().spawnParticle(Particle.WATER_SPLASH, villager.getLocation().clone().add(0, 2, 0), 80);
                         return;
                    }
                    villager.shakeHead();
                    ArrayList<String> messages = Translate.getEnemyMessages();
                    e.getPlayer().sendMessage(villager.getCustomName() + "§8: §f" + messages.get(Math.max(new Random().nextInt(messages.size()), 0)));
                } else if(gameLobby.getProtestersSpawned().contains(villager)) {
                    e.setCancelled(true);
                    ArrayList<String> messages = Translate.getFellowMessages();
                    e.getPlayer().sendMessage(villager.getCustomName() + "§8: §f" + messages.get(Math.max(new Random().nextInt(messages.size()), 0)));
                }
            });
        }
    }

    @EventHandler
    public void onOffhand(PlayerSwapHandItemsEvent e) {
        if(isIngame.containsKey(e.getPlayer())){
            e.setCancelled(true);
            if(isIngame.get(e.getPlayer()).getArena().getStatus() == 2) {
                GuiUtils.open(new MainGUI(), e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onEntity(PlayerInteractAtEntityEvent e) {
        LobbyHandler.GAMES.forEach(gameLobby -> {
            if(gameLobby.getPlayers().contains(e.getPlayer())) {
                if(gameLobby.getInteractAsset().contains(e.getRightClicked())) {
                    String id = e.getRightClicked().getMetadata("id").get(0).asString();
                    gameLobby.getArena().getAssets().forEach(gameAsset -> {
                        if(gameAsset.getId().equalsIgnoreCase(id)) {
                            if(gameLobby.getBalance() >= gameAsset.getCost()) {
                                if(gameAsset.isPlaced()) {
                                    e.getPlayer().sendMessage(Main.PREFIX + translate("::ingame.asset.already_bought", gameAsset.getDisplayName()));
                                    return;
                                }
                                gameAsset.setPlaced(true);
                                gameAsset.setRemoveProgress(0);
                                if(gameAsset.getRemProgBar() != null) gameAsset.getRemProgBar().remove();
                                gameAsset.setRemProgBar(null);
                                SchematicManager.paste(gameAsset.getDisplayAt(), gameAsset.getFile(), false);
                                gameLobby.setBalance(gameLobby.getBalance() - gameAsset.getCost());
                            } else {
                                e.getPlayer().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", gameAsset.getDisplayName()));
                            }
                        }
                    });
                }
            }
        });
    }

    public static void removeFromMainhand(Player p) {
        if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        ItemStack i = p.getInventory().getItemInMainHand();
        if(i.getAmount() > 1) {
            i.setAmount(i.getAmount() - 1);
        } else {
            i.setType(Material.AIR);
        }
        p.getInventory().setItemInMainHand(i);
    }

}
