package vgu.pe2026.ttt.webapp;

public class comPlayer extends Player{
    public comPlayer(int playerNum){
        super(playerNum);
    }

    @Override
    public int getMove(Board board, GameIO io){
        for(int i = 1; i <= 9; i++){
            if(board.isValidPos(i)){
                return i;
            }
        }
        //return -1;
        return 0;
    }
}
