package com.dwarslooper.luetzidefense.game;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SchematicManager;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.GameAsset;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class LobbyHandler {

    public static HashMap<UUID, Long> interactCooldown = new HashMap<>();
    public static HashMap<Player, GameLobby> isIngame = new HashMap<>();
    public static ArrayList<GameLobby> GAMES = new ArrayList<>();

    public static int createGame(Arena arena, Player p) {
        //if(!checkCooldown(p)) return -1;
        GameLobby lobby = new GameLobby(arena);
        for(GameLobby g : GAMES) {
            if(g.arena == arena) return 0;
        }
        GAMES.add(lobby);
        arena.setStatus(1);
        arena.updateSigns();
        return 1;
    }

    public static void resetGame(Arena arena) {
        arena.setStatus(3);
        arena.updateSigns();
        SchematicManager.paste(arena.getCenter(), arena.getFile(), false);
        GameLobby gl = gameUsesArena(GAMES, arena);
        if(gl != null) {
            gl.getArena().getAssets().forEach(gameAsset -> gameAsset.setPlaced(false));
            GAMES.remove(gl);
            for(Entity e : gl.deleteOnReset) {
                if(!e.isDead()) {
                    e.remove();
                }
            }
            ((ArrayList<Player>)gl.getPlayers().clone()).forEach(gl::handleLeft);
        }
    }

    public static GameLobby gameUsesArena(ArrayList<GameLobby> lobbies, Arena arena) {
        for (GameLobby lobby : lobbies) {
            if(lobby.arena == arena) return lobby;
        }
        return null;
    }

    public static void startGame(GameLobby gameLobby) {
        gameLobby.getArena().setStatus(2);
        gameLobby.getArena().updateSigns();
        gameLobby.start();
    }

    public static void addToGame(GameLobby lobby, Player player) {
        if(!checkCooldown(player)) return;
        lobby.handleJoined(player);
    }

    public static void removeFromGame(GameLobby lobby, Player player) {
        if(!checkCooldown(player)) return;
        lobby.handleLeft(player);
    }

    public static boolean checkCooldown(Player p) {
        if(!onCooldown(p, Main.getInstance().getConfig().getInt("ratelimit"))) {
            p.sendMessage(Main.PREFIX + translate("::game.message.ratelimited"));
            return false;
        }
        return true;
    }

    public static boolean onCooldown(Player player, int cooldown) {
        if(interactCooldown.containsKey(player.getUniqueId())) {
            long secondsLeft = ((interactCooldown.get(player.getUniqueId()) / 1000) + cooldown) - (System.currentTimeMillis() / 1000);
            if(secondsLeft > 0) {
                return false;
            }
        }
        interactCooldown.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }

}
