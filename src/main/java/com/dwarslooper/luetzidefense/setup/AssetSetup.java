package com.dwarslooper.luetzidefense.setup;

import com.dwarslooper.luetzidefense.Main;
import com.dwarslooper.luetzidefense.arena.GameAsset;
import com.dwarslooper.luetzidefense.gui.ConfirmGUI;
import com.dwarslooper.luetzidefense.gui.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static com.dwarslooper.luetzidefense.Translate.translate;

public class AssetSetup {
    public static HashMap<Player, Integer> setupSteps = new HashMap<>();
    public static HashMap<Player, GameAsset> newAsset = new HashMap<>();

    public static void next(Player p, String s) {
        if(!setupSteps.containsKey(p)) {
            setupSteps.put(p, 0);
            newAsset.put(p, new GameAsset("", "", "", 0, false, null, ""));
        }

        int step = setupSteps.get(p);

        final boolean defaultCharsetNoSpaces = !s.replaceAll("[^a-zA-Z0-9]", "").equalsIgnoreCase(s) || s.contains(" ");
        GameAsset asset = newAsset.get(p);
        if(step == 0) {
            p.sendMessage(Main.PREFIX + translate("::setup.asset.step0"));
            asset.setArena(s);
            setupSteps.put(p, 1);
        } else if(step == 1) {
            // Asset ID
            if(defaultCharsetNoSpaces) {
                invalidParameter(p, "a-z, A-Z, 0-9");
                return;
            }
            p.sendMessage(Main.PREFIX + translate("::setup.asset.step1"));
            asset.setId(s.toLowerCase());
            setupSteps.put(p, 2);
        } else if(step == 2) {
            // Asset Name
            p.sendMessage(Main.PREFIX + translate("::setup.asset.step2"));
            asset.setDisplayName(s);
            setupSteps.put(p, 3);
        } else if(step == 3) {
            // Asset Filename
            if(defaultCharsetNoSpaces) {
                invalidParameter(p, "a-z, A-Z, 0-9");
                return;
            }
            p.sendMessage(Main.PREFIX + translate("::setup.asset.step3"));
            asset.setFileName(s);
            setupSteps.put(p, 4);
        } else if(step == 4) {
            // Asset cost
            int cost;
            try {
                cost = Integer.parseInt(s);
            } catch (Exception e) {
                invalidParameter(p, "1 - 1000");
                return;
            }
            if(cost < 1) {
                invalidParameter(p, "1 - 1000");
                return;
            }
            p.sendMessage(Main.PREFIX + translate("::setup.asset.step4"));
            asset.setCost(cost);
            setupSteps.put(p, 5);
        } else if(step == 5) {
            if(!s.equalsIgnoreCase("confirm")) {
                invalidParameter(p, "confirm");
                return;
            }
            asset.setDisplayAt(p.getLocation());
            setupSteps.remove(p);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                GuiUtils.open(new ConfirmGUI(translate("::setup.asset.prompt"), "REGISTER_ASSET"), p);
            });
        }
        newAsset.put(p, asset);

    }

    private static void invalidParameter(Player p, String parameters) {
        p.sendMessage(Main.PREFIX + translate("::setup.asset.invalid_params", parameters));
    }

    public static void exit(Player p, boolean finished) {
        setupSteps.remove(p);
        newAsset.remove(p);
        if(!finished) p.sendMessage(Main.PREFIX + translate("::setup.asset.exit"));
    }

}
