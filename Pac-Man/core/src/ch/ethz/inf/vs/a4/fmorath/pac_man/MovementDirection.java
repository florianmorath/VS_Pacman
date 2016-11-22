package com.mygdx.game;

/**
 * Created by johannes on 22.11.16.
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
    public int getValue(){
        return val;
    }

    public static MovementDirection createDirectionFromInt(int i){
        switch(i){
            case 0: return UP;
            case 1: return RIGHT;
            case 2: return DOWN;
        }
        return LEFT;
    }

}
