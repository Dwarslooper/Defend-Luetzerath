package com.dwarslooper.luetzidefense.characters.activist;

import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

    private LivingEntity taskTarget;

    @Override
    public boolean tick(Villager e, GameLobby l) {
        if(taskTarget == null) {
            for (Entity entity : l.getEnemiesSpawned()) {
                if(entity.getLocation().distance(e.getLocation()) < 10) {
                    if((Math.random() * 10) < 2) {
                        if(entity instanceof LivingEntity entity1) taskTarget = entity1;
                    }
                }
            }
        } else {
            if(taskTarget.isDead()) {
                taskTarget = null;
                tick(e, l);
            } else {
                e.getPathfinder().moveTo(taskTarget.getLocation());
                if (e.getLocation().distanceSquared(taskTarget.getLocation()) < 2) {
                    taskTarget.setFireTicks(160);
                }
            }
        }
        return true;
    }
}
