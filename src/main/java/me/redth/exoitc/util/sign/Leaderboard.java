package me.redth.exoitc.util.sign;

import me.redth.exoitc.data.PlayerStats;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    public static List<PlayerStats> killsSorted = new ArrayList<>();
    public static List<PlayerStats> winsSorted = new ArrayList<>();

    public static void update() {
        killsSorted = PlayerStats.killsSorted();
        winsSorted = PlayerStats.winsSorted();
    }

}
