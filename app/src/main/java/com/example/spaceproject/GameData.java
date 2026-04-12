package com.example.spaceproject;

// ═══════════════════════════════════════════════════════════════
// GameData.java — Shared Game State
// ═══════════════════════════════════════════════════════════════
// This class holds data that needs to be shared across
// multiple Activities (screens).
//
// For example, crewCount needs to be:
//   - READ in NavigationActivity  (to block if >= 2)
//   - WRITTEN in RecruitActivity  (to increment after recruiting)
//
// Because Android Activities cannot directly talk to each other,
// we store shared values here as static variables.
// Static means the value persists as long as the app is running.
//
// HOW TO USE FROM ANY ACTIVITY:
//   Read:  int count = GameData.crewCount;
//   Write: GameData.crewCount++;
// ═══════════════════════════════════════════════════════════════

public class GameData {

    // Number of crew members recruited so far.
    // Starts at 0. Max allowed at game start = 2.
    public static int crewCount = 0;

    // You will add more shared game state here later, for example:
    // public static int missionsCompleted = 0;
    // public static List<String> crewNames = new ArrayList<>();
}