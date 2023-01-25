package com.dwarslooper.luetzidefense.characters.activist;

import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class Chemist extends Activist {
    public Chemist() {
        super("::activist.chemist", 4, 0, 22);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.setCustomName(translate("§d" + super.getDisplay()));
        e.setProfession(Villager.Profession.NONE);
        e.setVillagerType(Villager.Type.SNOW);
        e.setCustomNameVisible(true);
        return e;
    }

    HashMap<Villager, Entity> taskTarget = new HashMap<>();

    @Override
    public boolean tick(Villager e, GameLobby l) {
        if(!taskTarget.containsKey(e)) {
            for (Entity entity : l.getEnemiesSpawned()) {
                if(entity.getLocation().distance(e.getLocation()) < 10) {
                    if((Math.random() * 10) < 2) {
                        if(entity instanceof LivingEntity entity1) taskTarget.put(e, entity1);
                    }
                }
            }
        } else {
            if(taskTarget.get(e).isDead()) {
                taskTarget.remove(e);
                tick(e, l);
            } else {
                e.getPathfinder().moveTo(taskTarget.get(e).getLocation());
                if (e.getLocation().distanceSquared(taskTarget.get(e).getLocation()) < 2) {
                    taskTarget.get(e).setFireTicks(160);
                    taskTarget.remove(e);
                }
            }
        }
        return true;
    }
}
