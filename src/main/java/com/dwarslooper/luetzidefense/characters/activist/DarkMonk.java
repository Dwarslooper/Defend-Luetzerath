package com.dwarslooper.luetzidefense.characters.activist;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.SettingManager;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class DarkMonk extends Activist {
    public DarkMonk() {
        super("::activist.monk", 1, 0, 44);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
        e.setHealth(100);
        e.setCustomName(translate("§6" + super.getDisplay()));
        e.setProfession(Villager.Profession.NONE);
        e.setVillagerType(Villager.Type.DESERT);
        e.setCustomNameVisible(true);
        return e;
    }



    @Override
    public boolean tick(Villager e, GameLobby l) {
        for(Entity e0 : e.getLocation().getNearbyEntities(1, 1, 1)) {
            if(l.getEnemiesSpawned().contains(e0)) {
                if(e0 instanceof Villager villager) {
                    villager.sleep(villager.getLocation());
                    villager.damage(8);
                    villager.setVelocity(e.getLocation().getDirection().add(new Vector(0, 0.4, 0)).multiply(0.8));
                    villager.getPathfinder().stopPathfinding();
                    if(villager.isDead()) {
                        l.addBalance(SettingManager.pointsOnKill + 4);
                    }
                }
            }
        }
        return true;
    }
}
