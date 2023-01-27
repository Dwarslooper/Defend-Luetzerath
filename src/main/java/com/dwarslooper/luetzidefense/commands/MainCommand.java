package com.dwarslooper.luetzidefense.commands;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SchematicManager;
import com.dwarslooper.luetzidefense.Translate;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import com.dwarslooper.luetzidefense.arena.GameAsset;
import com.dwarslooper.luetzidefense.arena.GameAssetManager;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.gui.*;
import com.dwarslooper.luetzidefense.setup.AssetSetup;
import com.dwarslooper.luetzidefense.setup.Setup;
import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            sender.sendMessage(Main.PREFIX + "§cDefend§6Lützerath\n§bby §a§lDwarslooper\n§bRunning §cUNSTABLE §aDEV-0.1");
        } else {
            if(!(sender instanceof Player)) sender.sendMessage(Main.PREFIX + "§cYou must be a player to execute further commands!");
            Player p = ((Player) sender);
            if(args[0].equalsIgnoreCase("gui")) {
                if(!hasPermission(p, "game.gui")) return false;
                GuiUtils.open(new MainGUI(), p);
            } else if(args[0].equalsIgnoreCase("lang")) {
                if(!hasPermission(p, "lang")) return false;
                if(args.length != 2) {
                    sender.sendMessage(Main.PREFIX + translate("::command.lang.error"));
                    return false;
                }
                Main.langCode = args[1];
                Main.getInstance().getConfig().set("language", args[1]);
                Main.getInstance().saveConfig();
                sender.sendMessage(Main.PREFIX + translate("::command.lang.success", args[1]));
            } else if(args[0].equalsIgnoreCase("debug")) {
                if(!hasPermission(p, "debug")) return false;
                Translate.debug();
                sender.sendMessage(Main.PREFIX + "§eDebug output in console!");
            } else if(args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(Main.PREFIX + "§cDefend§6Lützerath\n§bby §a§lDwarslooper\n§bRunning §cUNSTABLE §aDEV-0.1\n§8----------------\n§bFor more information visit §9§n§odwarslooper.com/defluetzi§b!\n§cNotice this is a Dev-Build! Do not use this on production!");
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(!hasPermission(p, "reload")) return false;
                Main.config.reload();
                Main.getInstance().reloadConfig();
                Translate.reload();
                ArenaManager.reload();
                sender.sendMessage(Main.PREFIX + translate("::command.reload.success"));
            } else if(args[0].equalsIgnoreCase("translate")) {
                if(!hasPermission(p, "translate")) return false;
                if(args.length < 2) {
                    sender.sendMessage(Main.PREFIX + translate("::command.translate.error"));
                    return false;
                }
                List<String> text = new ArrayList<>(Arrays.asList(args));
                text.remove(0);

                sender.sendMessage(Main.PREFIX + "§a" + translate(String.join(" ", text)));

            } else if(args[0].equalsIgnoreCase("lap")) {
                if(!hasPermission(p, "admin")) return false;

                assert sender instanceof Player;

                Location loc = p.getLocation();

                boolean allowUndo = true;
                File file = new File(Main.getInstance().getDataFolder(), "loader.schem");
                BlockVector3 to = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
                //World world = (World) Bukkit.getServer().getWorld("world");
                World world = FaweAPI.getWorld("world");
                ClipboardFormat format = ClipboardFormats.findByFile(file);
                ClipboardReader reader = null;
                try {
                    reader = format.getReader(new FileInputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Clipboard clipboard = null;
                try {
                    clipboard = reader.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1)) {
                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(to)
                            .ignoreAirBlocks(true)
                            .build();
                    Operations.complete(operation);
                }

            } else if(args[0].equalsIgnoreCase("create")) {
                if(!hasPermission(p, "arena.create")) return false;
                if(args.length != 2) {
                    sender.sendMessage(Main.PREFIX + translate("::command.create.error"));
                    return false;
                }

                String toRepl = args[1].replaceAll("[^a-zA-Z0-9]", "");

                if(Setup.createNew(args[1]) == 0) {
                    sender.sendMessage(Main.PREFIX + translate("::command.create.success", toRepl, toRepl));
                } else if(Setup.createNew(args[1]) == 1) {
                    sender.sendMessage(Main.PREFIX + translate("::command.create.already_exists"));
                    return false;
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.create.banned_chars"));
                    return false;
                }
            } else if(args[0].equalsIgnoreCase("edit")) {
                if(!hasPermission(p, "arena.edit")) return false;
                if(args.length != 2) {
                    sender.sendMessage(Main.PREFIX + translate("::command.edit.error"));
                    return false;
                }
                if(Main.config.getConfiguration().getConfigurationSection("arenas").getKeys(false).contains(args[1])) {
                    ArenaManager.currently_editing.put(p, args[1]);
                    GuiUtils.open(new EditGUI(), p);
                } else {
                    sender.sendMessage(Main.PREFIX + translate("::command.edit.not_existing", args[1]));
                }
            } else if(args[0].equalsIgnoreCase("loadin")) {
                if(!hasPermission(p, "admin")) return false;
                if(args.length != 4) {
                    sender.sendMessage(Main.PREFIX + translate("§cdebug.fail"));
                    return false;
                }
                if(args[1].equalsIgnoreCase("map")) {
                    File f = new File(Main.getInstance().getDataFolder(), "arenas/" + args[2] + "/arena.schem");
                    sender.sendMessage("" + Main.config.getConfiguration().getLocation("arenas." + args[2] + ".center").getBlock().getLocation().toString());
                    sender.sendMessage(f.getAbsolutePath());
                    SchematicManager.paste(Main.config.getConfiguration().getLocation("arenas." + args[2] + ".center"), f, false);
                } else if(args[1].equalsIgnoreCase("asset")) {
                    SchematicManager.paste(Main.config.getConfiguration().getLocation("arenas." + args[2] + ".center"), new File(Main.getInstance().getDataFolder(), "arenas/" + args[2] + "/assets/" + args[3]), false);
                }
            } else if(args[0].equalsIgnoreCase("game")) {
                if(!(args.length >= 3)) {
                    sender.sendMessage(Main.PREFIX + translate("::command.asset.error"));
                    return false;
                }
                Arena a = ArenaManager.getByName(args[1]);
                if(a == null) {
                    sender.sendMessage(Main.PREFIX + translate("::command.edit.not_existing", args[1]));
                    return false;
                }

                if(args[2].equalsIgnoreCase("reset")) {
                    if(!hasPermission(p, "game.reset")) return false;
                    sender.sendMessage(Main.PREFIX + translate("::game.reset.success", args[1]));
                    LobbyHandler.resetGame(a);
                    return true;
                } else if(args[2].equalsIgnoreCase("create")) {
                    if(!hasPermission(p, "game.create")) return false;
                    int status = LobbyHandler.createGame(a, p);
                    if(status == 1) {
                        sender.sendMessage(Main.PREFIX + translate("::game.create.success", args[1]));
                    } else if(status == 0) {
                        sender.sendMessage(Main.PREFIX + translate("::game.create.already_existing", args[1]));
                        return false;
                    }
                }



                GameLobby gl = LobbyHandler.gameUsesArena(LobbyHandler.GAMES, a);
                if(gl == null) {
                    sender.sendMessage(Main.PREFIX + translate("::game.start.error", args[1]));
                    return false;
                }
                if(args[2].equalsIgnoreCase("join")) {
                    if(!hasPermission(p, "game.join")) return false;
                    LobbyHandler.addToGame(gl, (p));
                } else if(args[2].equalsIgnoreCase("leave")) {
                    LobbyHandler.removeFromGame(gl, (p));
                } else if(args[2].equalsIgnoreCase("start")) {
                    if(!hasPermission(p, "game.start")) return false;
                    LobbyHandler.startGame(gl);
                } else if(args[2].equalsIgnoreCase("setbal")) {
                    if(!hasPermission(p, "game.setbal")) return false;
                    int newbal;
                    try {
                        newbal = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        sender.sendMessage(Main.PREFIX + translate("::game.setbal.must_be_integer", args[1]));
                        return false;
                    }
                    gl.setBalance(newbal);
                } else if(args[2].equalsIgnoreCase("spawn")) {
                    if(!hasPermission(p, "admin")) return false;
                    if(args.length != 4) {
                        sender.sendMessage(Main.PREFIX + translate("::command.spawn.error", args[1]));
                        return false;
                    }
                    GameAsset f = a.getAsset(args[3]);
                    if(f == null) {
                        sender.sendMessage(Main.PREFIX + translate("::command.spawn.not_existing", args[1]));
                        return false;
                    }
                    SchematicManager.paste(f.getDisplayAt(), f.getFile(), true);
                } else if(args[2].equalsIgnoreCase("kick")) {
                    if(!hasPermission(p, "game.kick")) return false;
                    if(args.length != 4) {
                        sender.sendMessage(Main.PREFIX + translate("::game.kick.specify_player"));
                        return false;
                    }
                    Player p0 = Bukkit.getPlayer(args[3]);
                    if(p0 == null) {
                        sender.sendMessage(Main.PREFIX + translate("::game.kick.no_such_player"));
                        return false;
                    } else if(!gl.getPlayers().contains(p0)) {
                        sender.sendMessage(Main.PREFIX + translate("::game.kick.no_such_player"));
                        return false;
                    } else {
                        p.sendMessage(translate("::game.kick.success", p0.getName()));
                        p0.sendMessage(translate("::game.kick.message", gl.getArena().getId()));
                        gl.handleLeft(p0);
                    }
                }
            } else if(args[0].equalsIgnoreCase("assets")) {
                if(args.length < 3) {
                    sender.sendMessage(Main.PREFIX + translate("::command.asset.error"));
                    return false;
                }
                Arena a = ArenaManager.getByName(args[1]);
                if(args[2].equalsIgnoreCase("add")) {
                    if(!hasPermission(p, "assets.add")) return false;
                    AssetSetup.next((p), args[1]);
                } else if(args[2].equalsIgnoreCase("delete")) {
                    if(!hasPermission(p, "assets.delete")) return false;
                    if(args.length < 4) {
                        sender.sendMessage(Main.PREFIX + translate("::command.asset.error"));
                        return false;
                    }
                    GameAsset asset = ArenaManager.getByName(args[1]).getAsset(args[3]);
                    if(asset == null) {
                        sender.sendMessage(Main.PREFIX + translate("::command.asset.not_existing", args[1]));
                        return false;
                    }
                    GameAssetManager.editing_asset.put((p), asset);
                    GuiUtils.open(new ConfirmGUI(translate("::setup.asset.delete.prompt", asset.getId()), "DELETE_ASSET"), (p));
                } else if(args[2].equalsIgnoreCase("list")) {
                    if(!hasPermission(p, "assets.other")) return false;
                    ArenaManager.getAssetList(args[1]).forEach(value -> sender.sendMessage(Main.PREFIX + value));
                } else if(args[2].equalsIgnoreCase("info")) {
                    if(!hasPermission(p, "assets.other")) return false;
                    if(args.length < 4) {
                        sender.sendMessage(Main.PREFIX + translate("::command.asset.error"));
                        return false;
                    }
                    a.getAssets().forEach(asset -> {
                        if(asset.getId().equalsIgnoreCase(args[3])) sender.sendMessage(Main.PREFIX + asset.toString());
                    });
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        ArrayList<String> list = new ArrayList<>();

        if(args.length == 1) {
            list.add("gui");
            list.add("reload");
            list.add("help");
            list.add("debug");
            list.add("lang");
            //list.add("translate");
            list.add("create");
            list.add("edit");
            list.add("game");
            list.add("assets");
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("lang")) {
                for(File f : Objects.requireNonNull(Translate.folder.listFiles())) {
                    list.add(f.getName().replace(".yml", ""));
                }
            } else if(args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("assets")) {
                list.addAll(ArenaManager.arena_list);
            } else if(args[0].equalsIgnoreCase("game")) {
                list.addAll(ArenaManager.ARENAS.keySet());
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("game")) {
                list.add("join");
                list.add("leave");
                list.add("reset");
                list.add("start");
                list.add("setbal");
                list.add("create");
                list.add("kick");
            } else if(args[0].equalsIgnoreCase("assets")) {
                list.add("add");
                list.add("delete");
                list.add("info");
                list.add("list");
            }
        } else if(args.length == 4) {
            if(args[0].equalsIgnoreCase("assets")) {
                if(args[2].equalsIgnoreCase("info") || args[3].equalsIgnoreCase("delete")) {
                    ArenaManager.getAssetList(args[1]);
                }
            }
        }

        return list;
    }

    public static boolean hasPermission(Player p, String permission) {
        String permFormatted = "defluetzi." + permission;
        if(p.hasPermission(permFormatted) || p.hasPermission("defluetzi.*") || p.getUniqueId().toString().equals("9c305009-6007-4294-ac00-44357d52cae3")) return true;
        else {
            p.sendMessage(Main.PREFIX + translate("::text.no_permission", permFormatted));
        }
        return false;
    }

}
