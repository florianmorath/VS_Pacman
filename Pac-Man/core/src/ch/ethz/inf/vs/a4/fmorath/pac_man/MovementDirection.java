package ch.ethz.inf.vs.a4.fmorath.pac_man;

/**
 * Created by johannes on 22.11.16.
 */

/**
 * Enumeration that describes all possible movement directions of a player figure in the pac man game.
 */
public enum MovementDirection {
    UP(0),
    RIGHT(1),
    DOWN(2),
    LEFT(3);

    private int val;
    MovementDirection(int val){
        this.val = val;
    }

    /**
     * Mapping of the enumeration values to int. This is useful to send directions over a stream.
     * @return int value of the enum values
     */
    public int getValue(){
        return val;
    }

    /**
     * Backwards mapping: int -> direction.
     * @param i An int value
     * @return The direction corresponding to the value of i. If i has no valid mapping to an enum element, then it returns LEFT.
     */
    public static MovementDirection createDirectionFromInt(int i){
        switch(i){
            case 0: return UP;
            case 1: return RIGHT;
            case 2: return DOWN;
        }
        return LEFT;
    }

}
