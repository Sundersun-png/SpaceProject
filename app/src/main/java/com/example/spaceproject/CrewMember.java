package com.example.spaceproject;

import java.io.Serializable;

public class CrewMember implements Serializable {
    public String name;
    public String role;
    public int skillLevel;
    public int experience;
    public int resilience;
    public int maxEnergy;
    public int currentEnergy;
    public String location = "Quarters";
    public boolean isTrained = false;

    public CrewMember(String name, String role, int resilience, int maxEnergy) {
        this.name = name;
        this.role = role;
        this.skillLevel = 1;
        this.experience = 0;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

    public int getSkill() {
        return skillLevel;
    }

    public boolean isScientist() {
        return role.equalsIgnoreCase("Scientist");
    }

    public void train(int bonus) {
        isTrained = true;
        if (isScientist()) {
            experience += 1 + bonus;
        } else {
            experience += 2 + bonus;
        }
    }

    public String getPortraitDrawable() {
        if (role == null) return "ic_launcher_foreground";
        switch (role) {
            case "Pilot": return "pilot";
            case "Engineer": return "engineer";
            case "Medic": return "medic";
            case "Scientist": return "scientist";
            case "Soldier": return "soldier";
            default: return "ic_launcher_foreground";
        }
    }

    @Override
    public String toString() {
        return name + " (" + role + ") - XP: " + experience + " Skill: " + skillLevel + " Energy: " + currentEnergy + "/" + maxEnergy;
    }
}
