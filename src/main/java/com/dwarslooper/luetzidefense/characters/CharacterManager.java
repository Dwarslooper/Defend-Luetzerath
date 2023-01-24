package com.dwarslooper.luetzidefense.characters;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterManager {

    public CharacterManager() {

    }

    private HashMap<Entity, Character> bounds = new HashMap<>();

    private ArrayList<Character> characters = new ArrayList<>();

    public void add(Character character) {
        characters.add(character);
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public HashMap<Entity, Character> getBounds() {
        return bounds;
    }

    public Character getByName(String translation) {
        for(Character c : characters) {
            if(c.getDisplay().equalsIgnoreCase(translation)) return c;
        }
        return null;
    }

    public Character getAssigned(Entity e) {
        if(!e.isDead() && getBounds().containsKey(e)) return getBounds().get(e);
        return null;
    }

    @Override
    public String toString() {
        return "CharacterManager{" +
                "characters=" + characters +
                '}';
    }

    public void updateCharacters() {

    }
}
