package vgu.pe2026.ttt.webapp;

import java.util.Scanner;

abstract class Player {
    protected int playerNum;

    public Player(int playerNum){
        this.playerNum = playerNum;
    }

    public int getPlayerNum(){
        return playerNum;
    }

    public abstract int getMove(Board board, GameIO io);
}
