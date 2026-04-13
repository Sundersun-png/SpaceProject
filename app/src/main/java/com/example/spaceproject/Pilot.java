package com.example.spaceproject;

public class Pilot extends CrewMember {

    public Pilot(String name) {
        super(name, "Pilot", 7, 6);
    }

    @Override
    public String getAbility() {
        return "Evade";
    }
}