package ch.ethz.inf.vs.a4.fmorath.pac_man.actions;

/**
 * Created by linus on 16.12.2016.
 */

public enum ActionType {

    Movement(0), EatCoin(1), EatPlayer(2);

    private final int value;

    ActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ActionType getType(int value) {
        switch (value) {
            case 0: return Movement;
            case 1: return EatCoin;
            default: return EatPlayer;
        }
    }
}
