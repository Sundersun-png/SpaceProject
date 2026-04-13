package com.example.spaceproject;

import java.util.ArrayList;
import java.util.List;

// ═══════════════════════════════════════════════════════════════
// GameData.java — Single source of truth for all game state.
// All fields are static so any Activity can read/write them.
// ═══════════════════════════════════════════════════════════════

public class GameData {

    // ── Crew ──────────────────────────────────────────────────────
    public static List<CrewMember> crewList = new ArrayList<>();

    // ── Currency ──────────────────────────────────────────────────
    // Player starts with 300 coins. Instant training costs 30.
    public static int coins = 300;
    public static final int INSTANT_TRAIN_COST = 30;

    // ── Mission tracking ──────────────────────────────────────────
    public static int missionsCompleted = 0;
    public static int missionsWon       = 0;
    public static int missionsLost      = 0;

    // ── Helper: create crew member by role name ───────────────────
    public static CrewMember createCrew(String name, String role) {
        switch (role) {
            case "Pilot":     return new Pilot(name);
            case "Engineer":  return new Engineer(name);
            case "Medic":     return new Medic(name);
            case "Scientist": return new Scientist(name);
            case "Soldier":   return new Soldier(name);
            default:          return new Pilot(name);
        }
    }

    // ── Crew B opponents (fixed, with portrait drawable names) ────
    public static final String[] CREW_B_NAMES = {
            "Graviton", "Beast", "Tyranide", "Horizon", "Grapple"
    };

    // Portrait drawable name for each Crew B member
    // Place graviton.png, beast.png etc. in res/drawable/
    public static String getCrewBPortrait(String name) {
        switch (name) {
            case "Graviton": return "graviton";
            case "Beast":    return "beast";
            case "Tyranide": return "tyranide";
            case "Horizon":  return "horizon";
            case "Grapple":  return "grapple";
            default:         return "graviton";
        }
    }

    // ── Mission types ─────────────────────────────────────────────
    public static final String[] MISSION_TYPES = {
            "Asteroid Field Navigation",
            "Reactor Meltdown",
            "Virus Outbreak",
            "Alien Attack",
            "Potion Making"
    };
}