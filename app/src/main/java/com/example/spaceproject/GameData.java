package com.example.spaceproject;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    public static List<CrewMember> crewList = new ArrayList<>();
    public static int coins = 100; // Starting coins
    public static final int INSTANT_TRAIN_COST = 5;
    public static final int MISSION_WIN_REWARD = 5;
    public static int crewCount = 0; // Legacy support if needed

    // Inventory states
    public static final int ITEM_BROKEN = 0;
    public static final int ITEM_REPAIRED = 1;
    public static final int ITEM_SENT = 2;

    public static int[] inventoryItemState = {ITEM_BROKEN, ITEM_BROKEN, ITEM_BROKEN, ITEM_BROKEN};
    public static int pendingRepairIndex = -1;

    public static CrewMember createCrew(String name, String spec) {
        switch (spec) {
            case "Pilot":     return new Pilot(name);
            case "Medic":     return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Engineer":  return new Engineer(name);
            case "Soldier":   return new Soldier(name);
            default:          return new CrewMember(name, spec, 5);
        }
    }

    public static void addCoins(int amount) {
        coins += amount;
    }
}
