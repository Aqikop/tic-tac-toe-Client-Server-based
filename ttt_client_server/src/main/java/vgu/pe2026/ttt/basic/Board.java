package vgu.pe2026.ttt.basic;

abstract class Board {
    abstract protected boolean makeMove(int pos, int player);
    abstract protected boolean isValidPos(int pos);
    abstract protected boolean isBoardFull();
    abstract protected void printBoard();
    abstract protected int checkWinner();

    // New: return the board as a string instead print to terminal
    abstract protected String render();

    abstract protected int[] getBoardArray();
}
