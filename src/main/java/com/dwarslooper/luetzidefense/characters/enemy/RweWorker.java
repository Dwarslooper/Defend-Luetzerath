package com.dwarslooper.luetzidefense.characters.enemy;

import com.dwarslooper.luetzidefense.characters.Enemy;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class RweWorker extends Enemy {
    public RweWorker() {
        super("::enemy.rweworker", 1001, 0);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.setCustomName(translate("§c" + super.getDisplay()));
        e.setProfession(Villager.Profession.FISHERMAN);
        e.setVillagerType(Villager.Type.SAVANNA);
        e.setCustomNameVisible(true);
        return e;
    }

    @Override
    public boolean tick(Villager e, GameLobby l) {
        return false;
    }
}
