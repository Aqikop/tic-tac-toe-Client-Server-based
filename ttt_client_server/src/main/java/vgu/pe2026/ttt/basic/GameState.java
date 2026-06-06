package vgu.pe2026.ttt.basic;

public class GameState {
    public String gameId;
    public int[] board;
    public int currentPlayer;   
    public String status;         // "ongoing", "won", "draw"
    public int winner;            // 1 or 2, only if status="won"
    public String message;        // Display message for client
    public int lastMove;          // Last move made by computer
    
    public GameState(String gameId, int[] board, int currentPlayer, 
                     String status, int winner, String message, int lastMove) {
        this.gameId = gameId;
        this.board = board.clone();
        this.currentPlayer = currentPlayer;
        this.status = status;
        this.winner = winner;
        this.message = message;
        this.lastMove = lastMove;
    }
}