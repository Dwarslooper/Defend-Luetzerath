package com.dwarslooper.luetzidefense.characters.activist;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.Utils;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class Aggressive extends Activist {
    public Aggressive() {
        super("::activist.aggressive", 3, 0, 10);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.setCustomName(translate("§4" + super.getDisplay()));
        e.setProfession(Villager.Profession.WEAPONSMITH);
        e.setVillagerType(Villager.Type.JUNGLE);
        e.setCustomNameVisible(true);
        return e;
    }

    public static HashMap<Item, Villager> itemThrower = new HashMap<>();

    @Override
    public boolean tick(Villager e, GameLobby l) {
        List<Entity> entities = e.getNearbyEntities(8, 8, 8);
        for(Entity entity : entities) {
            if(!l.getEnemiesSpawned().contains(entity)) continue;
            if(e.hasLineOfSight(entity)) {
                if(e.getLocation().distance(entity.getLocation()) <= 1) e.damage(2);
                Utils.lookAt(e, entity);
                boolean skippedFirstBlock = false;
                for(Block b : e.getLineOfSight(null, 8)) {
                    if(!skippedFirstBlock) continue;
                    skippedFirstBlock = true;
                    for(Entity e0 : b.getLocation().getNearbyEntities(0.5, 0.5, 0.5)) {
                        if(l.getProtestersSpawned().contains(e0)) return true;
                    }
                }

                Item bomb = e.getLocation().getWorld().dropItem(e.getLocation().clone().add(0, 1, 0), StackCreator.createItem(Material.MUD, 1, "Bomb", "KABOOM!"));
                bomb.setPickupDelay(160);
                bomb.setCanPlayerPickup(false);
                bomb.setCanMobPickup(false);
                bomb.setHealth(1000);
                bomb.setVelocity(e.getLocation().getDirection().add(new Vector(0, 0.1, 0)));
                itemThrower.put(bomb, e);
                l.getDeleteOnReset().add(bomb);

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
                            if (e instanceof Villager vil) {
                                if (itemThrower.get(bomb) == vil) return;
                                vil.damage(2, bomb);
                                if (entity.isDead()) {
                                    if (l.getProtestersSpawned().contains(entity))
                                        l.removeBalance(4);
                                    else if (l.getEnemiesSpawned().contains(entity))
                                        l.addBalance(2);
                                }
                                itemThrower.remove(bomb);
                                bomb.remove();
                                this.cancel();
                            }
                        }
                        if (secs == 40) {
                            bomb.remove();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 0L, 1);
                break;
            }
        }
        return true;
    }
}
