package me.redth.exoitc.game.mode;

public enum GameGoal {
    KILLS("Kills"),
    LIVES("Lives");

    public final String name;

    GameGoal(String name) {
        this.name = name;
    }

    public static GameGoal valueOfCatch(String str) {
        try {
            return valueOf(str);
        } catch (Exception ex) {
            return KILLS;
        }
    }

    private static final GameGoal[] vals = values();

    public GameGoal next() {
        return vals[(ordinal() + 1) % vals.length];
    }
}
