package vgu.pe2026.ttt.basic;

public class Board_2D extends Board {
    private int[][] cells;

    public Board_2D(){
        this.cells = new int[3][3];
    }

    protected boolean makeMove(int pos, int player){
        int row = (pos - 1) / 3;
        int col = (pos -1 ) % 3;

        if (pos >= 1 && pos <= 9 && cells[row][col] == 0) {
            cells[row][col] = player;
            return true;
        }
        return false;

    }

    protected boolean isValidPos(int pos){
        int row = (pos - 1) / 3;
        int col = (pos -1 ) % 3;

        return pos >= 1 && pos <= 9 && cells[row][col] == 0;
    }

    protected boolean isBoardFull(){
        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[i].length; j++){
                if(cells[i][j] == 0) return false;
            }
        }
        return true;
    }

    protected void printBoard(){
        for (int i = 0; i < cells.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < cells[i].length; j++) {
                System.out.print(cells[i][j]);
                if(j < 2){
                    System.out.print(" | ");
                }
            }
            System.out.println(" |");
        }
    }

    protected int checkWinner(){
        // rows
        for (int i = 0; i < 3; i++) {
            if (cells[i][0] != 0 && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) {
                return cells[i][0];
            }
        }

        // columns
        for (int j = 0; j < 3; j++) {
            if (cells[0][j] != 0 && cells[0][j] == cells[1][j] && cells[1][j] == cells[2][j]) {
                return cells[0][j];
            }
        }

        // main diagonal
        if (cells[0][0] != 0 && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) {
            return cells[0][0];
        }

        // anti-diagonal
        if (cells[0][2] != 0 && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) {
            return cells[0][2];
        }

        if (isBoardFull()) return -1;

        return 0;
    }

    @Override
    protected String render() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            sb.append("| ");
            for (int j = 0; j < cells[i].length; j++) {
                sb.append(cells[i][j]);
                if (j < 2) sb.append(" | ");
            }
            sb.append(" |").append(System.lineSeparator());
        }
        return sb.toString();
    }

    // get the state of the board
    @Override
    protected int[] getBoardArray(){
        int[] result = new int[9];
        int idx = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[idx++] = cells[i][j];
            }
        }
        return result;
    }
}
