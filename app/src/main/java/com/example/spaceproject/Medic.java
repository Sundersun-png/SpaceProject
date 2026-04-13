package com.example.spaceproject;

public class Medic extends CrewMember {

    public Medic(String name) {
        super(name, "Medic", 4, 8);
    }

    @Override
    public String getAbility() {
        return "Heal Teammate";
    }
}
