package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.Screen;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import com.dwarslooper.luetzidefense.arena.GameAssetManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class EditGUI extends ClickGUI{
    public EditGUI() {
        super("::gui.editarena");
    }

    @Override
    public Inventory open(Player player) {
        String ARENA_ID = ArenaManager.currently_editing.get(player);

        ConfigurationSection section = Main.config.getConfiguration().getConfigurationSection("arenas").getConfigurationSection(ARENA_ID);
        Arena a = ArenaManager.getByName(ARENA_ID);

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

        String isSpawnsSet;
        List<?> spawns = section.getList("spawns");
        if(spawns == null) isSpawnsSet = translate("::mapsetting.status.numunset", "0", "" + minSpawns);
        else if(spawns.size() == 0) isSpawnsSet = translate("::mapsetting.status.numunset", "0", "" + minSpawns);
        else if(spawns.size() < minSpawns) isSpawnsSet = translate("::mapsetting.status.numunset", "" + spawns.size(), "" + minSpawns);
        else isSpawnsSet = translate("::mapsetting.status.numset", "" + spawns.size(), "" + minSpawns);


        Screen s = new Screen(3, translate(Main.PREFIX + "§2" + "::gui.editarena"))
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .addButton(10, StackCreator.createItem(Material.ORANGE_BANNER, 1, translate("::mapsetting.center.title"), List.of(translate("::mapsetting.center.desc"), isCenterSet + changeText)), () -> {
                    section.set("center", player.getLocation());
                    Main.config.save();
                    GuiUtils.open(new EditGUI(), player);
                })
                .addButton(11, StackCreator.createItem(Material.MAP, 1, translate("::mapsetting.schem.title"), List.of(translate("::mapsetting.schem.desc"), isSchemSet + changeText)), () -> {
                    player.sendMessage(Main.PREFIX + translate("::mapsetting.schem.expl"));
                    player.closeInventory();
                })
                .addButton(12, StackCreator.createItem(Material.ENDER_EYE, 1, translate("::mapsetting.spawn.title"), List.of(translate("::mapsetting.spawn.desc"), translate("::mapsetting.spawn.left"), translate("::mapsetting.spawn.right"), isSpawnsSet + changeText)), () -> {
                    ArrayList<Location> locations = new ArrayList<>();
                    if(section.getList("spawns") != null) locations = (ArrayList<Location>) section.getList("spawns");
                    locations.add(player.getLocation());
                    section.set("spawns", locations);
                    Main.config.save();
                    player.closeInventory();
                }, InventoryAction.PICKUP_ALL)
                .addInteraction(12, () -> {
                    Screen.getRegistered("confirm")
                            .addButton(13, StackCreator.createItem(Material.PAPER, 1,translate("::confirm.delete_spawns.prompt", ARENA_ID)))
                            .addInteraction(10, () -> {
                                Main.config.getConfiguration().set("arenas." + ARENA_ID + ".spawns", null);
                                Main.config.getConfiguration().set("arenas." + ARENA_ID + ".isfinished", false);
                                Main.config.save();
                                ArenaManager.reload();
                                player.closeInventory();
                                player.sendMessage(Main.PREFIX + translate("::confirm.delete_spawns.success", ARENA_ID));
                                GameAssetManager.editing_asset.remove(((Player) player));
                            })
                            .addInteraction(16, player::closeInventory)
                            .open(player);
                }, InventoryAction.PICKUP_HALF)
                .addButton(13, StackCreator.createItem(Material.OAK_LOG, 1, translate("::mapsetting.assets.title"), List.of(translate("::mapsetting.assets.desc"), isAssetsSet + changeText)), () -> {
                    player.sendMessage(Main.PREFIX + translate("::mapsetting.assets.expl", ARENA_ID));
                    player.closeInventory();
                })
                .addButton(14, StackCreator.createItem(Material.OAK_SIGN, 1, translate("::mapsetting.signs.title"), List.of(translate("::mapsetting.signs.desc"), translate("::mapsetting.signs.right"), translate("::mapsetting.signs.left"), isSignsSet + changeText)), player::closeInventory)
                .addInteraction(14, () -> {
                    if(a.addSign(player.getTargetBlock(4).getLocation())) {
                        a.updateSigns();
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.added"));
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.already_existing"));
                    }
                }, InventoryAction.PICKUP_ALL)
                .addInteraction(14, () -> {
                    if(a.removeSign(player.getTargetBlock(4).getLocation())) {
                        a.updateSigns();
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.removed"));
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.not_existing"));
                    }
                }, InventoryAction.PICKUP_HALF)
                .addCondition(14, () -> {
                    Block b = player.getTargetBlock(4);
                    if(b == null) {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.look_at"));
                        return false;
                    }
                    if(!(b.getState() instanceof Sign sign)) {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.look_at"));
                        return false;
                    }
                    if(a == null) {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.signs.must_be_registered"));
                        return false;
                    }
                    return true;
                })
                .addButton(15, StackCreator.createItem(Material.FIREWORK_ROCKET, 1, translate("::mapsetting.finish.title"), List.of(translate("::mapsetting.finish.desc0"), translate("::mapsetting.finish.desc1"), isFinished + changeText)), () -> {
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
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.already_done", ARENA_ID));
                        player.closeInventory();
                        return;
                    }

                    if(complete == 3) {
                        section.set("isfinished", true);
                        Main.config.save();
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.success", ARENA_ID));
                        GuiUtils.open(new EditGUI(), ((Player) player));
                        ArenaManager.reload();
                    } else {
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.task0"));
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.task1"));
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.task2"));
                        player.sendMessage(Main.PREFIX + translate("::mapsetting.finish.task3",  + min_spawns + ""));
                        player.closeInventory();
                    }
                })
                .addButton(16, StackCreator.createItem(Material.RED_CONCRETE, 1, translate("::mapsetting.delete.title"), List.of(translate("::mapsetting.delete.desc"), changeText)), () -> {
                    Screen.getRegistered("confirm")
                            .addButton(13, StackCreator.createItem(Material.PAPER, 1, translate("::confirm.delete_arena.prompt", ARENA_ID)))
                            .addInteraction(10, () -> {
                                Main.config.getConfiguration().set("arenas." + ARENA_ID, null);
                                Main.config.save();
                                try {
                                    FileUtils.deleteDirectory(new File(Main.getInstance().getDataFolder(), "arenas/" + ARENA_ID));
                                } catch (Exception exception) {
                                   player.sendMessage(Main.PREFIX + translate("::system.exception"));
                                    exception.printStackTrace();
                                }
                                ArenaManager.reload();
                               player.closeInventory();
                               player.sendMessage(Main.PREFIX + translate("::confirm.delete_arena.success", ARENA_ID));
                            })
                            .addInteraction(16, player::closeInventory)
                            .open(player);
                })
                .addButton(26, StackCreator.createItem(Material.BARRIER, 1, translate("::gui.close"), ""))
                .open(player);

        return s.getInventory();
    }
}
