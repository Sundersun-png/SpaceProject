package com.example.spaceproject;

import java.util.ArrayList;
import java.util.List;

public class CrewRepository {

    private static final List<CrewMember> crewList = new ArrayList<>();

    static {
        if (crewList.isEmpty()) {
            crewList.add(new CrewMember("Andrew", "Medic", 25, 5, 0));
            crewList.add(new CrewMember("Maya", "Pilot", 25, 5, 0));
        }
    }

    public static List<CrewMember> getCrewList() {
        return crewList;
    }
}