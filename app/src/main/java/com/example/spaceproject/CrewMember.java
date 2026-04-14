package com.example.spaceproject;

public class CrewMember {
    String name;
    String role;
    int baseSkill;
    int experience;
    public String location = "Quarters";

    public CrewMember(String name, String role, int baseSkill) {
        this.name = name;
        this.role = role;
        this.baseSkill = baseSkill;
        this.experience = 0;
    }

    public int getSkill() {
        return baseSkill + experience;
    }

    public boolean isScientist() {
        return role.equalsIgnoreCase("Scientist");
    }

    public void train(int bonus) {
        if (isScientist()) {
            experience += 1 + bonus;
        } else {
            experience += 2 + bonus;
        }
    }

    @Override
    public String toString() {
        return name + " (" + role + ") - XP: " + experience + " Skill: " + getSkill();
    }
}
