package com.example.spaceproject;

// ═══════════════════════════════════════════════════════════════
// CrewMember.java — Abstract base class for all crew members
// Subclasses: Pilot, Medic, Engineer, Scientist, Soldier
// ═══════════════════════════════════════════════════════════════

public abstract class CrewMember {

    // ── Fields ───────────────────────────────────────────────────
    public String name;
    public String role;
    public int baseSkill;
    public int experience;
    public int energy;
    public int maxEnergy = 100;
    public int resilience;
    public int missionCount = 0;

    // Training state
    public boolean isTraining   = false;
    public boolean isTrained    = false;  // true once first training done
    public boolean inHospital   = false;
    public String hospitalStage = "";     // "Critical", "Recovering", "Healed"

    // Location: "Quarters", "Simulator", "MissionControl", "Hospital"
    public String location = "Quarters";

    // ── Constructor ───────────────────────────────────────────────
    public CrewMember(String name, String role, int baseSkill, int resilience) {
        this.name       = name;
        this.role       = role;
        this.baseSkill  = baseSkill;
        this.resilience = resilience;
        this.experience = 0;
        this.energy     = maxEnergy;
    }

    // ── getSkill() ────────────────────────────────────────────────
    // New Skill = baseSkill + experience  (from project plan)
    public int getSkill() {
        return baseSkill + experience;
    }

    // ── train() ──────────────────────────────────────────────────
    // Called when training completes (after 30s or instant).
    // Adds 2 XP as per the updated spec.
    public void train(int bonusXP) {
        experience  += 2 + bonusXP;
        isTrained    = true;
        isTraining   = false;
        location     = "Quarters";
    }

    // ── restoreEnergy() ──────────────────────────────────────────
    public void restoreEnergy() {
        energy = maxEnergy;
    }

    // ── getAbility() ─────────────────────────────────────────────
    // Each subclass returns its unique ability name
    public abstract String getAbility();

    // ── getDrawableId() ──────────────────────────────────────────
    // Returns the drawable resource name for this role's portrait
    // e.g. "img_pilot", "img_medic" etc.
    public String getPortraitDrawable() {
        switch (role) {
            case "Pilot":     return "img_pilot";
            case "Medic":     return "img_medic";
            case "Engineer":  return "img_engineer";
            case "Scientist": return "img_scientist";
            case "Soldier":   return "img_soldier";
            default:          return "img_pilot";
        }
    }
}
