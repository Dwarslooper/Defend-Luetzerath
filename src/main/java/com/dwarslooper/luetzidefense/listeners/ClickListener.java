package com.dwarslooper.luetzidefense.listeners;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import com.dwarslooper.luetzidefense.arena.GameAsset;
import com.dwarslooper.luetzidefense.arena.GameAssetManager;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.gui.*;
import com.dwarslooper.luetzidefense.setup.AssetSetup;
import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.util.ArrayList;

import static com.dwarslooper.luetzidefense.Translate.translate;
import static com.dwarslooper.luetzidefense.commands.MainCommand.checkPermission;


public class ClickListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if(!GuiUtils.guisOpened.contains(e.getInventory())) return;
        e.setCancelled(true);
        if(checkConditions(e)) return;

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.back"))) {
            GuiUtils.open(new MainGUI(), (Player) e.getWhoClicked());
            return;
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.close"))) {
            e.getWhoClicked().closeInventory();
            return;
        }


        if(e.getView().getTitle().contains(translate(GuiUtils.MAIN.getTitle()))) {

            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.characters"))) GuiUtils.open(new CharactersGUI(), (Player) e.getWhoClicked());
            else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.skills"))) GuiUtils.open(new SkillsGUI(), (Player) e.getWhoClicked());
            else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.shop"))) GuiUtils.open(new ShopGUI(), (Player) e.getWhoClicked());
            else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::gui.settings"))) GuiUtils.open(new SettingsGUI(), (Player) e.getWhoClicked());

        } else if(e.getView().getTitle().contains(translate(GuiUtils.CHARACTERS.getTitle()))) {

            GameLobby lobby = null;
            for (GameLobby game : LobbyHandler.GAMES) {
                if(game.getPlayers().contains((Player) e.getWhoClicked())) lobby = game;
            }

            if(lobby == null) {
                e.getWhoClicked().sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                return;
            }

            GameLobby finalLobby = lobby;
            Main.CM.getCharacters().forEach(character -> {
                if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate(character.getDisplay()))) {
                    if(character instanceof Activist activist) {
                        if(finalLobby.getBalance() >= activist.getCost()) {
                            Villager e0 = (Villager) e.getWhoClicked().getWorld().spawnEntity(e.getWhoClicked().getLocation(), EntityType.VILLAGER);
                            if (character.manageEntity(e0) == null) {
                                Main.getInstance().getServer().getConsoleSender().sendMessage(Main.PREFIX + "§cWarning! Changes have been made to characters, but character §6" + character.getDisplay().replaceFirst("::", "") + " §creturned null when initializing! Maybe the §fmanageEntity() §cmethod is not configured properly?!");
                            }
                            finalLobby.getProtestersSpawned().add(e0);
                            finalLobby.getDeleteOnReset().add(e0);
                            Main.CM.getBounds().put(e0, character);
                            finalLobby.removeBalance(activist.getCost());
                        } else {
                            e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", translate(activist.getDisplay())));
                        }
                    }
                }
            });


        } else if(e.getView().getTitle().contains(translate(GuiUtils.SKILLS.getTitle()))) {

            GameLobby lobby = null;
            for (GameLobby game : LobbyHandler.GAMES) {
                if(game.getPlayers().contains((Player) e.getWhoClicked())) lobby = game;
            }

            if(lobby == null) {
                e.getWhoClicked().sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                return;
            }

            Player p = ((Player) e.getWhoClicked());

            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.skills.pe.title"))) {
                if(lobby.getSkillsPhysical().get(p) == 10) e.getWhoClicked().sendMessage(Main.PREFIX + translate("", e.getCurrentItem().getItemMeta().getDisplayName()));
                else if(lobby.getBalance() - 22 >= 0) {
                    lobby.removeBalance(22);
                    lobby.getSkillsPhysical().put(p, lobby.getSkillsPhysical().get(p) + 1);
                } else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.skills.points.title"))) {
                if(lobby.getSkillsPoints().get(p) == 10) e.getWhoClicked().sendMessage(Main.PREFIX + translate("", e.getCurrentItem().getItemMeta().getDisplayName()));
                else if(lobby.getBalance() - 35 >= 0) {
                    lobby.removeBalance(35);
                    lobby.getSkillsPoints().put(p, lobby.getSkillsPoints().get(p) + 1);
                } else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.skills.speed.title"))) {
                if(lobby.getSkillsSpeed().get(p) == 10) e.getWhoClicked().sendMessage(Main.PREFIX + translate("", e.getCurrentItem().getItemMeta().getDisplayName()));
                else if(lobby.getBalance() - 13 >= 0) {
                    lobby.removeBalance(13);
                    lobby.getSkillsSpeed().put(p, lobby.getSkillsSpeed().get(p) + 1);
                } else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.skills.throw.title"))) {
                if(lobby.getSkillsThrow().get(p) == 10) e.getWhoClicked().sendMessage(Main.PREFIX + translate("", e.getCurrentItem().getItemMeta().getDisplayName()));
                else if(lobby.getBalance() - 17 >= 0) {
                    lobby.removeBalance(17);
                    lobby.getSkillsThrow().put(p, lobby.getSkillsThrow().get(p) + 1);
                } else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                }
            }

            GuiUtils.open(new SkillsGUI(), p);

        } else if(e.getView().getTitle().contains(translate(GuiUtils.SHOP.getTitle()))) {

            GameLobby lobby = null;
            for (GameLobby game : LobbyHandler.GAMES) {
                if(game.getPlayers().contains((Player) e.getWhoClicked())) lobby = game;
            }

            if(lobby == null) {
                e.getWhoClicked().sendMessage(Main.PREFIX + translate("::gui.fail.not_ingame"));
                return;
            }

            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.shop.tnt.title"))) {
                if(lobby.getBalance() - 44 >= 0) lobby.setBalance(lobby.getBalance() - 44);
                else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                    return;
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.shop.molotow.title"))) {
                if(lobby.getBalance() - 26 >= 0) lobby.setBalance(lobby.getBalance() - 26);
                else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                    return;
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.shop.water.title"))) {
                if(lobby.getBalance() - 35 >= 0) lobby.setBalance(lobby.getBalance() - 35);
                else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                    return;
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.shop.buff.title"))) {
                if(lobby.getBalance() - 13 >= 0) lobby.setBalance(lobby.getBalance() - 13);
                else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                    return;
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::ingame.gui.shop.sword.title"))) {
                if(lobby.getBalance() - 53 >= 0) lobby.setBalance(lobby.getBalance() - 53);
                else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::ingame.asset.too_expensive", e.getCurrentItem().getItemMeta().getDisplayName().replace("§c","")));
                    return;
                }
            }

            e.getWhoClicked().getInventory().addItem(e.getCurrentItem());

        } else if(e.getView().getTitle().contains(translate(GuiUtils.EDITARENA.getTitle()))) {

            String ARENA_ID = ArenaManager.currently_editing.get((Player) e.getWhoClicked());

            ConfigurationSection section = Main.config.getConfiguration().getConfigurationSection("arenas").getConfigurationSection(ARENA_ID);

            if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.delete.title"))) {
                if(!checkPermission(((Player) e.getWhoClicked()), "arena.delete")) return;
                GuiUtils.open(new ConfirmGUI(translate("::confirm.delete_arena.prompt", ARENA_ID), "DELETE_ARENA"), ((Player) e.getWhoClicked()));
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.schem.title"))) {
                e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.schem.expl"));
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.spawn.title"))) {
                if(e.getAction() == InventoryAction.PICKUP_ALL) {
                    ArrayList<Location> locations = new ArrayList<>();
                    if(section.getList("spawns") != null) locations = (ArrayList<Location>) section.getList("spawns");
                    locations.add(e.getWhoClicked().getLocation());
                    section.set("spawns", locations);
                    Main.config.save();
                    e.getWhoClicked().closeInventory();
                } else if(e.getAction() == InventoryAction.PICKUP_HALF) {
                    GuiUtils.open(new ConfirmGUI(translate("::confirm.delete_spawns.prompt", ARENA_ID), "DELETE_SPAWNS"), ((Player) e.getWhoClicked()));
                }
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.center.title"))) {
                section.set("center", e.getWhoClicked().getLocation());
                Main.config.save();
                GuiUtils.open(new EditGUI(), ((Player) e.getWhoClicked()));
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.assets.title"))) {
                e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.assets.expl", ARENA_ID));
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.signs.title"))) {
                Block b = e.getWhoClicked().getTargetBlock(4);
                e.getWhoClicked().closeInventory();
                if(b == null) {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.look_at"));
                    return;
                }
                if(!(b.getState() instanceof Sign sign)) {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.look_at"));
                    return;
                }
                Arena a = ArenaManager.getByName(ARENA_ID);
                if(a == null) {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.must_be_registered"));
                    return;
                }
                if(e.getAction() == InventoryAction.PICKUP_ALL) {
                    if(a.addSign(sign.getLocation())) {
                        a.updateSigns();
                        e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.added"));
                    } else {
                        e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.already_existing"));
                    }
                } else if(e.getAction() == InventoryAction.PICKUP_HALF) {
                    if(a.removeSign(sign.getLocation())) {
                        a.updateSigns();
                        e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.removed"));
                    } else {
                        e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.signs.not_existing"));
                    }
                } else if(e.getAction() == InventoryAction.CLONE_STACK) {
                    a.updateSigns();
                }
                e.getWhoClicked().closeInventory();
            } else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(translate("::mapsetting.finish.title"))) {

                int min_spawns = Main.getInstance().getConfig().getInt("min_enemy_spawns");

                int complete = 0;
                if(section.getLocation("center") != null) {
                    complete += 1;
                }
                if(section.getList("spawns") != null) {
                    if(section.getList("spawns").size() >= min_spawns) {
                        complete += 1;
                    }
                }
                if(new File(Main.getInstance().getDataFolder(),"arenas/" + ARENA_ID + "/arena.schem").exists()) {
                    complete += 1;
                }

                if(section.getBoolean("isfinished")) {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.already_done", ARENA_ID));
                    e.getWhoClicked().closeInventory();
                    return;
                }

                if(complete == 3) {
                    section.set("isfinished", true);
                    Main.config.save();
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.success", ARENA_ID));
                    GuiUtils.open(new EditGUI(), ((Player) e.getWhoClicked()));
                    ArenaManager.reload();
                } else {
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.task0"));
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.task1"));
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.task2"));
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::mapsetting.finish.task3",  + min_spawns + ""));
                    e.getWhoClicked().closeInventory();
                }

            }

        } else if(e.getView().getTitle().contains(translate(GuiUtils.CONFIRM.getTitle()))) {
            if(e.getCurrentItem().getType() == Material.LIME_TERRACOTTA) {
                if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("DELETE_ARENA")) {
                    String id = ArenaManager.currently_editing.get(((Player) e.getWhoClicked()));
                    Main.config.getConfiguration().set("arenas." + id, null);
                    Main.config.save();
                    try {
                        FileUtils.deleteDirectory(new File(Main.getInstance().getDataFolder(), "arenas/" + id));
                    } catch (Exception exception) {
                        e.getWhoClicked().sendMessage(Main.PREFIX + translate("::system.exception"));
                        exception.printStackTrace();
                    }
                    ArenaManager.reload();
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(Main.PREFIX + translate("::confirm.delete_arena.success", id));
                } else if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("REGISTER_ASSET")) {
                    Player p = ((Player) e.getWhoClicked());
                    GameAsset asset = AssetSetup.newAsset.get(p);
                    ConfigurationSection cs = Main.config.getConfiguration().createSection("arenas." + asset.getArena() + ".assets." + asset.getId());
                    cs.set("id", asset.getId());
                    cs.set("name", asset.getDisplayName());
                    cs.set("file", asset.getFileName() + ".schem");
                    cs.set("cost", asset.getCost());
                    cs.set("location", asset.getDisplayAt());
                    cs.set("ignoreair", asset.getIgnoreAir());
                    Main.config.save();
                    p.sendMessage(Main.PREFIX + translate("::setup.asset.success", asset.getId()));
                    p.closeInventory();
                } else if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("DELETE_ASSET")) {
                    Player p = ((Player) e.getWhoClicked());
                    GameAsset asset = GameAssetManager.editing_asset.get(((Player) e.getWhoClicked()));
                    Main.config.getConfiguration().set("arenas." + asset.getArena() + ".assets." + asset.getId(), null);
                    Main.config.save();
                    ArenaManager.reload();
                    e.getWhoClicked().closeInventory();
                    p.sendMessage(Main.PREFIX + translate("::setup.asset.delete.success", asset.getId()));
                    GameAssetManager.editing_asset.remove(((Player) e.getWhoClicked()));
                } else if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("DELETE_SPAWNS")) {
                    Player p = ((Player) e.getWhoClicked());
                    String a = ArenaManager.currently_editing.get(((Player) e.getWhoClicked()));
                    Main.config.getConfiguration().set("arenas." + a + ".spawns", null);
                    Main.config.getConfiguration().set("arenas." + a + ".isfinished", false);
                    Main.config.save();
                    ArenaManager.reload();
                    e.getWhoClicked().closeInventory();
                    p.sendMessage(Main.PREFIX + translate("::confirm.delete_spawns.success", a));
                    GameAssetManager.editing_asset.remove(((Player) e.getWhoClicked()));
                }
            } else if(e.getCurrentItem().getType() == Material.RED_TERRACOTTA) {
                if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("REGISTER_ASSET")) {
                    AssetSetup.exit(((Player) e.getWhoClicked()), false);
                } else if(e.getInventory().getItem(13).getItemMeta().getLore().get(0).equalsIgnoreCase("DELETE_ASSET")) {
                    GameAssetManager.editing_asset.remove(((Player) e.getWhoClicked()));
                }
                e.getWhoClicked().closeInventory();
            }
        }

    }

    private boolean checkConditions(InventoryClickEvent e) {
        return (e.getCurrentItem() == null || !(e.getCurrentItem().hasItemMeta()) || e.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE);
    }
}
