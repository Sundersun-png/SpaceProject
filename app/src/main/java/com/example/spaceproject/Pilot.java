package com.example.spaceproject;

public class Pilot extends CrewMember {
    public Pilot(String name) {
        super(name, "Pilot", 4, 20);
        this.skillLevel = 3;
    }
}
