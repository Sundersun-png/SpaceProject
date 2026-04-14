package com.example.spaceproject;

public class Statistics {

    private static Statistics instance;

    private int missionsWon;
    private int missionsLost;
    private int coinsEarned;

    private Statistics() {
        missionsWon  = 0;
        missionsLost = 0;
        coinsEarned  = 0;
    }

    public static Statistics getInstance() {
        if (instance == null) {
            instance = new Statistics();
        }
        return instance;
    }

    public void recordWin() {
        missionsWon++;
        coinsEarned += 10;
    }

    public void recordLoss() {
        missionsLost++;
    }

    public int getMissionsWon()   { return missionsWon;  }
    public int getMissionsLost()  { return missionsLost; }
    public int getCoinsEarned()   { return coinsEarned;  }
    public int getTotalMissions() { return missionsWon + missionsLost; }

    public double getWinRate() {
        if (getTotalMissions() == 0) return 0.0;
        return (missionsWon * 100.0) / getTotalMissions();
    }
}