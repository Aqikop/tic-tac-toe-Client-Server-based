package vgu.pe2026.ttt.webapp;

public class Game {
    private Board board;
    private Player p1;
    private Player p2;
    private Player currPlayer;
    private GameIO io;
    private boolean gameOver;

    public Game(int startingPlayer, GameIO io) {
        this.board = new Board_2D();
        this.io = io;

        this.p1 = new humanPlayer(1);
        this.p2 = new comPlayer(2);

        this.currPlayer = (startingPlayer == 1) ? p1 : p2;
        this.gameOver = false;
    }

    public int getTurn() {
        return gameOver ? 0 : 1;
    }

    public int playOneTurn() {
        io.print(board.render());
        io.println("Player#" + currPlayer.getPlayerNum() + "'s turn");

        int move = 0;
        while (move == 0) {
            move = currPlayer.getMove(board, io);
            if (move == 0) {
                io.println("Player#" + currPlayer.getPlayerNum() + "'s turn");
            }
            if (move == -1) {
                io.println("End of the game");
                gameOver = true;
                return 0;
            }
            if (move == -2) {
                return 0;
            }
        }

        board.makeMove(move, currPlayer.getPlayerNum());

        int res = board.checkWinner();
        if (res == 1 || res == 2) {
            io.print(board.render());
            io.println("Player#" + res + " won!");
            gameOver = true;
        } else if (res == -1) {
            io.print(board.render());
            io.println("It is a draw!");
            gameOver = true;
        } else {
            currPlayer = (currPlayer.getPlayerNum() == 1) ? p2 : p1;
        }

        return getTurn();
    }

    public void play() {
        int state = getTurn();
        while (state != 0) {
            state = playOneTurn();
        }
    }
}