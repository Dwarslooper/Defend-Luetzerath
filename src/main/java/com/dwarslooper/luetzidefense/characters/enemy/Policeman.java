package com.dwarslooper.luetzidefense.characters.enemy;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.characters.Enemy;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class Policeman extends Enemy {
    public Policeman() {
        super("::enemy.police", 1002, 0);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.setCustomName(translate("§9" + super.getDisplay()));
        e.setProfession(Villager.Profession.MASON);
        e.setVillagerType(Villager.Type.PLAINS);
        e.getEquipment().setHelmet(new ItemStack(Material.TINTED_GLASS));
        e.getEquipment().setItemInMainHand(new ItemStack(Material.SHIELD), true);
        e.setCustomNameVisible(true);
        return e;
    }

    HashMap<Villager, Entity> taskTarget = new HashMap<>();

    @Override
    public boolean tick(Villager e, GameLobby l) {
        if(!taskTarget.containsKey(e)) {
            for (Entity entity : l.getProtestersSpawned()) {
                if(entity.getLocation().distance(e.getLocation()) < 2) {
                    if((Math.random() * 10) < 8) {
                        if(entity instanceof LivingEntity entity1) {
                            taskTarget.put(e, entity1);
                            tick(e, l);
                        }
                    }
                }
            }
        } else if(taskTarget.get(e).isDead()) {
            taskTarget.remove(e);
            tick(e, l);
        } else {
            e.getPathfinder().moveTo(taskTarget.get(e).getLocation());
            if(e.getLocation().distanceSquared(taskTarget.get(e).getLocation()) < 2 && taskTarget.get(e) instanceof LivingEntity livingEntity) {
                livingEntity.damage(4);
                taskTarget.remove(e);
            }
        }
        return true;
    }
}
