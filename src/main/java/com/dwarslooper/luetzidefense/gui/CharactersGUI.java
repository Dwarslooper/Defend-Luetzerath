package com.dwarslooper.luetzidefense.gui;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.StackCreator;
import com.dwarslooper.luetzidefense.characters.Activist;
import com.dwarslooper.luetzidefense.characters.Character;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class CharactersGUI extends ClickGUI{

    public CharactersGUI() {
        super("::gui.characters", 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Inventory open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, translate(Main.PREFIX + "§2" + super.getTitle()));

        for(int i = 0; i < 9*3; i++) {
            inv.setItem(i, StackCreator.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
        }

        int currentSlot = 10;

        List<Character> characters = Main.CM.getCharacters();

        for (Character character : characters) {
            if(!(character instanceof Activist activist)) continue;
            ArrayList<String> lore = new ArrayList<>();
            lore.add(translate("§r§a::text.cost", "" + activist.getCost()));
            lore.add(translate("§r§a::text.skills"));
            inv.setItem(currentSlot, StackCreator.createItem(Material.PLAYER_HEAD, 1, "§r§e" + translate(activist.getDisplay()), lore));
            currentSlot++;
        }

        inv.setItem(26, StackCreator.createItem(Material.ARROW, 1, translate("::gui.back")));

        player.openInventory(inv);
        return inv;
    }
}
