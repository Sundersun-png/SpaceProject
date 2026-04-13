package com.example.spaceproject;

public class Soldier extends CrewMember {

    public Soldier(String name) {
        super(name, "Soldier", 9, 7);
    }

    @Override
    public String getAbility() {
        return "Heavy Attack";
    }
}