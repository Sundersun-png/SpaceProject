package com.example.spaceproject;

public class Engineer extends CrewMember {

    public Engineer(String name) {
        super(name, "Engineer", 5, 9);
    }

    @Override
    public String getAbility() {
        return "Repair";
    }
}
