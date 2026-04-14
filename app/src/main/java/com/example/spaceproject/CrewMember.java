package com.example.spaceproject;

import android.graphics.Color;

public class CrewMember {

    private String name;
    private String specialization;
    private int experience;
    private int energy;
    private int skill;
    private boolean isSelected;
    private final int maxEnergy;

    public CrewMember(String name, String specialization, int experience, int energy, int skill) {
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.energy = energy;
        this.skill = skill;
        this.maxEnergy = energy;
        this.isSelected = false;
    }

    public int getSpecializationColor() {
        switch (specialization) {
            case "Pilot":     return Color.parseColor("#5C6BC0");
            case "Engineer":  return Color.parseColor("#EF5350");
            case "Medic":     return Color.parseColor("#26A69A");
            case "Scientist": return Color.parseColor("#AB47BC");
            case "Soldier":   return Color.parseColor("#FFA726");
            default:          return Color.GRAY;
        }
    }

    public String getName()           { return name; }
    public String getSpecialization() { return specialization; }
    public int getExperience()        { return experience; }
    public int getEnergy()            { return energy; }
    public int getSkill()             { return skill; }
    public int getMaxEnergy()         { return maxEnergy; }
    public boolean isSelected()       { return isSelected; }

    public void setEnergy(int energy)         { this.energy = energy; }
    public void setSkill(int skill)           { this.skill = skill; }
    public void setExperience(int exp)        { this.experience = exp; }
    public void setSelected(boolean selected) { this.isSelected = selected; }

    public void restoreEnergy() {
        this.energy = this.maxEnergy;
    }

    public void gainExp(int amount) {
        this.experience += amount;
        this.skill = this.skill + this.experience;
    }
}