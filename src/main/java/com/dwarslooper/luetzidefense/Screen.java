package com.dwarslooper.luetzidefense;

import com.dwarslooper.luetzidefense.setup.ScreenInit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

import static com.dwarslooper.luetzidefense.Screen.Position.*;

public class Screen implements Listener {

    public static HashMap<String, Screen> REGISTRY = new HashMap<>();
    public static ArrayList<Screen> screens = new ArrayList<>();
    private final HashMap<Integer, Clickable[]> interactions = new HashMap<>();
    private final HashMap<Integer, Conditional[]> conditions = new HashMap<>();
    BooleanSupplier condition;
    Inventory inventory;

    public static Screen get(String id) {
        if(REGISTRY.containsKey(id)) {
            return REGISTRY.get(id);
        }
        return null;
    }

    @EventHandler
    public void inventoryEvent(InventoryClickEvent event) {
        new ArrayList<>(screens).forEach(screen -> screen.onClick(event));
    }

    private final int size;
    private final String title;

    public Screen(int size, String title) {
        screens.add(this);
        inventory = Bukkit.createInventory(null, 9*size, title);
        this.size = size;
        this.title = title;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Screen open(Player player) {
        player.openInventory(inventory);
        return this;
    }

    public static Screen getRegistered(String id) {
        Screen screen = new Screen(3, "Invalid screen");
        Screen result = REGISTRY.get(id);
        if(result != null) screen = result;

        return ScreenInit.getConfirm();
    }

    public Screen build(String id) {
        REGISTRY.put(id, this);
        return this;
    }

    public Screen setBackground(Material material) {
        for(int i = 0; i < getInventory().getSize(); i++) {
            if(getInventory().getItem(i) == null) getInventory().setItem(i, StackCreator.createItem(material, 1, " "));
        }
        return this;
    }

    public Screen clone() {
        Screen val;
        val = this;
        return val;
    }

    public Screen addButton(int slot, ItemStack itemStack, Runnable run, InventoryAction... actionTypes) {
        getInventory().setItem(slot, itemStack);
        Clickable clickable = new Clickable(actionTypes.length == 0 ? null : actionTypes, run);
        writeClickable(slot, clickable);
        return this;
    }

    public Screen addButton(int slot, ItemStack itemStack) {
        getInventory().setItem(slot, itemStack);
        Clickable clickable = new Clickable(null, () -> {});
        writeClickable(slot, clickable);
        return this;
    }

    public Screen addInteraction(int slot, Runnable run, InventoryAction... actionTypes) {
        Clickable clickable = new Clickable(actionTypes, run);
        writeClickable(slot, clickable);
        return this;
    }

    public Screen addCondition(int slot, BooleanSupplier callable, InventoryAction... actionTypes) {
        Conditional conditional = new Conditional(actionTypes, callable);
        writeConditions(slot, conditional);
        return this;
    }

    public Screen setDefaultCondition(BooleanSupplier callable) {
        condition = callable;
        return this;
    }

    public Screen setDefaultClickAction(Position position, Runnable runnable) {
        position.setTask(runnable);
        return this;
    }

    public void onClick(InventoryClickEvent event) {
        if(event.getInventory() == getInventory()) {
            event.setCancelled(true);
            if(!interactions.containsKey(event.getSlot())) return;

            HEAD.runTask();

            if(condition != null) {
                if(!condition.getAsBoolean()) return;
            }

            if(conditions.get(event.getSlot()) != null) {
                for(Conditional conditional : conditions.get(event.getSlot())) {
                    if(conditional.getActions().contains(event.getAction()) || conditional.getActions().isEmpty()) {
                        if(!conditional.supplier.getAsBoolean()) {
                            return;
                        }
                    }
                }
            }

            CHECK.runTask();

            for(Clickable clickable : interactions.get(event.getSlot())) {
                Runnable task = clickable.getTask();

                if(task == null) {
                    return;
                } else if (clickable.getActions().contains(event.getAction()) || clickable.getActions().isEmpty()) {
                    RETURN.runTask();
                    task.run();
                }
            }
            TAIL.runTask();
        }
    }

    public static class Clickable {

        private final ArrayList<InventoryAction> actions;
        private final Runnable run;

        public Clickable(InventoryAction[] inventoryActions, Runnable runnable) {
            this.actions = inventoryActions == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(inventoryActions));
            this.run = runnable;
        }

        public ArrayList<InventoryAction> getActions() {
            return actions;
        }

        public Runnable getTask() {
            return run;
        }
    }

    public enum Position {
        HEAD(null),
        TAIL(null),
        CHECK(null),
        RETURN(null);

        Position(Runnable runnable) {
            run = runnable;
        }

        private Runnable run;

        public void setTask(Runnable run) {
            this.run = run;
        }

        public void runTask() {
            if(run != null) run.run();
        }
    }

    public static class Conditional {

        private final ArrayList<InventoryAction> actions;
        private final BooleanSupplier supplier;

        public Conditional(InventoryAction[] inventoryActions, BooleanSupplier supplier) {
            this.actions = inventoryActions == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(inventoryActions));
            this.supplier = supplier;
        }

        public ArrayList<InventoryAction> getActions() {
            return actions;
        }

        public BooleanSupplier getSupplier() {
            return supplier;
        }
    }

    public void writeClickable(int slot, Clickable clickable) {
        Clickable[] clickables = interactions.get(slot);
        if(clickables == null) {
            Clickable[] toPut = {clickable};
            interactions.put(slot, toPut);
        } else {
            List<Clickable> tempList = new ArrayList<>(Arrays.asList(clickables));
            tempList.add(clickable);
            interactions.put(slot, tempList.toArray(new Clickable[0]));
        }
    }

    public void writeConditions(int slot, Conditional conditional) {
        Conditional[] conditionals = conditions.get(slot);
        if(conditionals == null) {
            Conditional[] toPut = {conditional};
            conditions.put(slot, toPut);
        } else {
            List<Conditional> tempList = new ArrayList<>(Arrays.asList(conditionals));
            tempList.add(conditional);
            conditions.put(slot, tempList.toArray(new Conditional[0]));
        }
    }

}
