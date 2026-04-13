package com.example.spaceproject;

public class Scientist extends CrewMember {

    public Scientist(String name) {
        super(name, "Scientist", 6, 5);
    }

    @Override
    public String getAbility() {
        return "Boost Attack";
    }
}