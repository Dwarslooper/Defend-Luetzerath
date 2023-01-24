package com.dwarslooper.luetzidefense.game;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.GameAsset;
import com.dwarslooper.luetzidefense.characters.Character;
import com.dwarslooper.luetzidefense.characters.Enemy;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class GameLobby {

    boolean isIngame = false;
    ArrayList<Player> players = new ArrayList<>();
    int balance = 20;
    ArrayList<Entity> deleteOnReset = new ArrayList<>();
    ArrayList<Entity> interactAsset = new ArrayList<>();
    ArrayList<Entity> enemiesSpawned = new ArrayList<>();
    ArrayList<Entity> protestersSpawned = new ArrayList<>();
    HashMap<Player, Integer> skillsPhysical = new HashMap<>();
    HashMap<Player, Integer> skillsSpeed = new HashMap<>();
    HashMap<Player, Integer> skillsPoints = new HashMap<>();
    HashMap<Player, Integer> skillsThrow = new HashMap<>();
    HashMap<Player, ItemStack[]> inventory = new HashMap<>();
    HashMap<Player, Location> prevPos = new HashMap<>();
    Arena arena;
    int spawnDelay;
    int nextSpawn;
    int timePlayed;
    HashMap<Entity, GameAsset> removeAssets = new HashMap<>();
    boolean isLost;

    public GameLobby(Arena arena) {
        this.arena = arena;
    }

    public void start() {
        players.forEach(player -> {
            skillsPhysical.put(player, 0);
            skillsSpeed.put(player, 0);
            skillsPoints.put(player, 0);
            skillsThrow.put(player, 0);
            player.getInventory().clear();
        });
        Main.getLOGGER().info(getArena().getAssets().toString());
        initFellowProtesters();
        isIngame = true;
        spawnDelay = 80;
        for(GameAsset asset : getArena().getAssets()) {
            ArmorStand display = (ArmorStand) arena.getCenter().getWorld().spawnEntity(asset.getDisplayAt(), EntityType.ARMOR_STAND);
            display.setSmall(true);
            display.setBasePlate(false);
            display.setGravity(false);
            display.setInvisible(true);
            display.setInvulnerable(true);
            display.setMetadata("id", new FixedMetadataValue(Main.getInstance(), asset.getId()));
            display.setItem(EquipmentSlot.HEAD, StackCreator.createItem(Material.EMERALD_BLOCK, 1, ""));
            display.setDisabledSlots(EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS, EquipmentSlot.OFF_HAND, EquipmentSlot.HAND);
            display.setCustomName("§a" + asset.getDisplayName() + " " + translate("::ingame.asset.buy", "" + asset.getCost()));
            display.setCustomNameVisible(true);
            deleteOnReset.add(display);
            interactAsset.add(display);
        }
    }

    public void tick() {
        for(Player p : getPlayers()) {
            p.setFoodLevel(20);
            if(getSkillsSpeed().containsKey(p)) {
                if(getSkillsSpeed().get(p) > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, getSkillsSpeed().get(p)));
                }
            }
            p.sendActionBar("§6" + getBalance() + " ◎");
        }

        if(!isIngame) return;

        timePlayed += 1;

        enemiesSpawned.removeIf(Entity::isDead);
        protestersSpawned.removeIf(Entity::isDead);
        updateRemoveAssets();
        if(nextSpawn < spawnDelay) {
            nextSpawn++;
        } else {
            updateEntities();
            getProtestersSpawned().forEach(entity -> {
                Character c = Main.CM.getAssigned(entity);
                if(c != null && entity instanceof Villager villager) {
                    c.tick(villager, this);
                }
            });
            getEnemiesSpawned().forEach(entity -> {
                Character c = Main.CM.getAssigned(entity);
                if(c != null && entity instanceof Villager villager) {
                    c.tick(villager, this);
                }
            });
            if (enemiesSpawned.size() < 20) {
                spawnWave();
            }
            nextSpawn = 0;
        }

        deleteOnReset.removeIf(Entity::isDead);

        removeAssets.forEach((entity, value) -> {
            ((Mob) entity).getPathfinder().moveTo(value.getDisplayAt(), 0.8);
        });


        if(protestersSpawned.isEmpty()) {
            boolean isSmthPlaced = false;
            for(GameAsset asset : getArena().getAssets()) {
                if (asset.isPlaced()) {
                    isSmthPlaced = true;
                    break;
                }
            }
            if(!isSmthPlaced) lostGame();
        }

    }

    private void initFellowProtesters() {
        for(int i = 0; i < 8; i++) {
            Villager e = (Villager) getArena().getCenter().getWorld().spawnEntity(getArena().getCenter(), EntityType.VILLAGER);
            e.setProfession(Villager.Profession.LEATHERWORKER);
            e.setVillagerType(Villager.Type.TAIGA);
            e.setVillagerExperience(100);
            e.setVillagerLevel(2);
            e.getEquipment().setItemInMainHand(new ItemStack(Material.SHIELD), true);
            e.setCustomNameVisible(true);
            e.setAI(true);
            e.setCustomName(translate("§a::activist.protester"));
            deleteOnReset.add(e);
            protestersSpawned.add(e);
            Main.CM.getBounds().put(e, Main.CM.getByName("::activist.protester"));
        }
    }

    private void spawnWave() {
        getArena().getEnemySpawns().forEach(location -> {
            if(Math.random() * 10 < ((10 / getArena().getEnemySpawns().size()) * Main.getInstance().getConfig().getInt("difficulty"))) {
                Villager e = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
                e.setVillagerExperience(100);
                e.setVillagerLevel(2);
                e.getEquipment().setItemInMainHandDropChance(0);
                e.getEquipment().setHelmetDropChance(0);
                Random rand = new Random();
                ArrayList<Character> characters = new ArrayList<>();
                Main.CM.getCharacters().forEach(character -> {
                    if(character instanceof Enemy e0) characters.add(e0);
                });
                int randomNum = Math.max(rand.nextInt(characters.size()), 0);
                Character c = characters.get(randomNum);
                Main.CM.getBounds().put(e, c);
                if(c.manageEntity(e) == null) {
                    Main.getInstance().getServer().getConsoleSender().sendMessage("§cWarning! Changes have been made to characters, but character §6" + c.getDisplay().replaceFirst("::", "") + " §creturned null when initializing! Maybe the §fmanageEntity() §cmethod is not configured properly?!");
                }
                enemiesSpawned.add(e);
                deleteOnReset.add(e);
            }
        });
    }

    private void updateEntities() {
        ArrayList<GameAsset> assets = (ArrayList<GameAsset>) getArena().getAssets().clone();
        enemiesSpawned.forEach(entity -> {
            if(removeAssets.size() < 4) {
                assets.removeIf(a -> !a.isPlaced());
                assets.removeIf(a -> removeAssets.containsValue(a));
                if (Math.random() * 10 < 4 && !assets.isEmpty()) {
                    Random rand = new Random();

                    int randomNum = Math.max(rand.nextInt(assets.size()) - 1, 0);
                    //Main.LOGGER.info("");

                    removeAssets.put(entity, assets.get(randomNum));
                    // TODO: 23.01.2023 Implement code below to debug
                    //Main.LOGGER.info("Villager " + entity.getUniqueId() + " is now going to " + assets.get(randomNum).getId());
                }
            }
        });
    }

    private void updateRemoveAssets() {
        if((Math.random() * 10) < (9 - 1)) return;
        ArrayList<GameAsset> toHeal = (ArrayList<GameAsset>) arena.getAssets().clone();
        enemiesSpawned.forEach(entity -> {
            if(removeAssets.keySet().contains(entity)) {
                GameAsset asset = removeAssets.get(entity);
                if(!asset.isPlaced()) removeAssets.remove(entity);
                if(asset.getDisplayAt().distance(entity.getLocation()) < 2) {
                    toHeal.remove(asset);
                    asset.updateRemoveProgress(1);
                    if(asset.getRemProgBar() != null && !deleteOnReset.contains(asset.getRemProgBar())) deleteOnReset.add(asset.getRemProgBar());
                }
            }
        });
        toHeal.forEach(gameAsset -> gameAsset.updateRemoveProgress(-1));
    }

    public void handleJoined(Player p) {
        if(!getPlayers().contains(p)) {
            if(getArena().getStatus() == 2 || getArena().getStatus() == 3) {
                p.sendMessage(Main.PREFIX + translate("::game.message.already_started"));
                return;
            }
            LobbyHandler.GAMES.forEach(gameLobby -> {
                if(gameLobby.getPlayers().contains(p)) gameLobby.handleLeft(p);
            });
            inventory.put(p, p.getInventory().getContents());
            prevPos.put(p, p.getLocation());
            LobbyHandler.isIngame.put(p, this);
            p.getInventory().clear();
            p.getInventory().setItem(0, StackCreator.createItem(Material.EMERALD, 1, translate("::ingame.hud.start")));
            p.getInventory().setItem(8, StackCreator.createItem(Material.RED_BED, 1, translate("::ingame.hud.leave")));
            p.sendMessage(Main.PREFIX + translate("::game.message.joined", getArena().getName()));
            getPlayers().add(p);
            getArena().setStatus(1);
            getArena().updateSigns();
            p.teleport(getArena().getCenter());
            p.sendMessage(translate("::ingame.welcome.chat", p.getName()));
            p.sendTitle(translate("::ingame.welcome.title"), translate("::ingame.welcome.subtitle", p.getName()));
        } else {
            p.sendMessage(Main.PREFIX + translate("::game.message.already_in_game"));
        }
    }

    public void handleLeft(Player p) {
        if(getPlayers().contains(p)) {
            p.sendMessage(Main.PREFIX + translate("::game.message.left", getArena().getName()));
            p.getInventory().setContents(inventory.get(p));
            p.updateInventory();
            p.teleport(prevPos.get(p));
            prevPos.remove(p);
            inventory.remove(p);
            getPlayers().remove(p);
            LobbyHandler.isIngame.remove(p);
            if(getPlayers().isEmpty()) {
                if(getArena().getStatus() == 3) return;
                if(getArena().getStatus() == 2 ) {
                    LobbyHandler.resetGame(getArena());
                }
                getArena().setStatus(0);
                getArena().updateSigns();
            }
        } else {
            p.sendMessage(Main.PREFIX + translate("::game.message.not_in_game"));
        }
    }


    private void lostGame() {

        if(isLost) return;
        isLost = true;

        getArena().setStatus(3);
        getArena().updateSigns();

        int ptt = timePlayed / 20;
        int ptminutes = ptt / 60;
        int pthours = ptminutes / 60;
        int ptdays = pthours / 24;

        ptt = ptt - ptminutes * 60;
        ptminutes = ptminutes - pthours * 60;
        pthours = pthours - ptdays * 24;

        for(Player p : getPlayers()) {
            p.closeInventory();
            p.sendMessage(Main.PREFIX + translate("::ingame.lost.chat"));
            p.sendTitle(translate("§c::ingame.lost.title"), translate("§e::ingame.lost.subtitle", ""+ptdays, ""+pthours, ""+ptminutes, ""+ptt), 10, 100, 10);
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 100, 1);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            LobbyHandler.resetGame(getArena());
        }, 20 * 20);
    }

    public Arena getArena() {
        return arena;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Entity> getDeleteOnReset() {
        return deleteOnReset;
    }

    public ArrayList<Entity> getInteractAsset() {
        return interactAsset;
    }

    public ArrayList<Entity> getProtestersSpawned() {
        return protestersSpawned;
    }

    public ArrayList<Entity> getEnemiesSpawned() {
        return enemiesSpawned;
    }

    public int getBalance() {
        return balance;
    }

    public void addBalance(int balance) {
        setBalance(getBalance() + balance);
    }

    public boolean removeBalance(int balance) {
        if(getBalance() - balance < 0) {
            setBalance(0);
            return false;
        } else {
            setBalance(getBalance() - balance);
            return true;
        }
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public HashMap<Entity, GameAsset> getRemoveAssets() {
        return removeAssets;
    }

    public HashMap<Player, Integer> getSkillsPhysical() {
        return skillsPhysical;
    }

    public HashMap<Player, Integer> getSkillsSpeed() {
        return skillsSpeed;
    }

    public HashMap<Player, Integer> getSkillsPoints() {
        return skillsPoints;
    }

    public HashMap<Player, Integer> getSkillsThrow() {
        return skillsThrow;
    }
}
