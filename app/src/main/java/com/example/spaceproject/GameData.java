package com.example.spaceproject;

import java.util.ArrayList;
import java.util.List;

public class GameData {

    public static List<CrewMember> crewList = new ArrayList<>();
    public static final int MAX_COINS = 100;
    public static final int INSTANT_TRAIN_COST = 5;
    public static int coins = 25;

    public static void addCoins(int amount) {
        coins = Math.min(MAX_COINS, coins + amount);
    }

    public static CrewMember createCrew(String name, String specialization) {
        switch (specialization) {
            case "Pilot":
                return new Pilot(name);
            case "Medic":
                return new Medic(name);
            case "Scientist":
                return new Scientist(name);
            case "Engineer":
                return new Engineer(name);
            case "Soldier":
                return new Soldier(name);
            default:
                return new CrewMember(name, specialization, 0);
        }
    }

    // ── Engineering inventory ─────────────────────────────────────
    public static boolean torpedoPurchased = false;
    public static boolean torpedoAdded     = false;
    
    public static boolean grenadePurchased = false;
    public static boolean grenadeAdded     = false;

    public static boolean gunPurchased     = false;
    public static boolean gunAdded         = false;

    public static boolean rocketshipPurchased = false;
    public static boolean rocketshipAdded     = false;

    // ── Scientist lab ─────────────────────────────────────────────
    public static int     weaknessPotionsPurchased = 0;
    public static boolean weaknessPotionAdded     = false; // If scientist added the effect

    public static boolean powerBoostPurchased = false;
    public static boolean powerBoostAdded     = false; // If scientist added for free use

    public static boolean skillBoostPurchased = false;
    public static boolean skillBoostAdded     = false;

    public static boolean allCrewSkillBoosted = false;

    // ── Mission types ─────────────────────────────────────────────
    public static final String MISSION_ASTEROID = "Asteroid Field Navigation";
    public static final String MISSION_REACTOR  = "Reactor Meltdown";
    public static final String MISSION_VIRUS    = "Virus Outbreak";
    public static final String MISSION_ALIEN    = "Alien Attack";
    public static final String MISSION_POTION   = "Potion Making";
}
