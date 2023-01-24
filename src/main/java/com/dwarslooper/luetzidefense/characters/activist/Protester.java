package com.dwarslooper.luetzidefense.characters.activist;

import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.game.GameLobby;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class Protester extends Activist {
    public Protester() {
        super("::activist.protester", 2, 0, 4);
    }

    @Override
    public Villager manageEntity(Villager e) {
        e.setCustomName(translate("§a" + super.getDisplay()));
        e.setProfession(Villager.Profession.LEATHERWORKER);
        e.setVillagerType(Villager.Type.TAIGA);
        e.setCustomNameVisible(true);
        return e;
    }

    @Override
    public boolean tick(Villager e, GameLobby l) {
        //Works like NPC, has not mechanisms coded to do anything
        return false;
    }
}
