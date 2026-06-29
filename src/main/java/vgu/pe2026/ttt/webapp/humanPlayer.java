package vgu.pe2026.ttt.webapp;

public class humanPlayer extends Player {
    public humanPlayer(int playerNum) {
        super(playerNum);
    }

    @Override
    public int getMove(Board board, GameIO io) {
        try {
            String user = io.readLine();
            if (user == null || user.isEmpty()) return 0;
            if (user.equals("q")) return -1;

            try{
                int move = Integer.parseInt(user);

                if(move >= 1 && move <= 9 && board.isValidPos(move)){
                    return move;
                } else if(move < 1 || move > 9){
                    io.println("Please, input a valid number [1-9]");
                    return 0;
                } else{
                    io.println("The cell is occupied!");
                    return 0;
                }
            } catch(NumberFormatException e){
                io.println("Please, input a valid number [1-9]");
                return 0;
            }
        } catch (Exception e) {
            io.println("Unexpected exception when reading player's choice");
            return -2;
        }
    }
}
