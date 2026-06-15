package vgu.pe2026.ttt.basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client_rest_moreSecure {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Board_2D board;
    private Scanner scanner;

    public Client_rest_moreSecure() throws Exception{
        this.board = new Board_2D();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try {
            Client_rest_moreSecure client = new Client_rest_moreSecure();
            client.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play() {
        try {
            // Initialize game - request empty board from server
            String response = sendRequest("INIT");
            String[] parts = response.split("\\|");
            String boardStr = parts[0];
            String status = parts[1];

            System.out.println("Server response: " + status);
            updateBoard(boardStr);

            boolean gameOver = false;

            while (!gameOver) {
                board.printBoard();

                System.out.println("Your turn. Enter position (1-9) or 'quit' to exit:");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q")) {
                    System.out.println("Game ended.");
                    break;
                }

                try {
                    int position = Integer.parseInt(input);

                    if(position < 1 || position > 9){
                        System.out.println("Please, input a valid number [1-9]");
                        continue;
                    }

                    if(!board.isValidPos(position)){
                        System.out.println("The cell is occupied!");
                        continue;
                    }

                    // Send board and move to server
                    String boardString = boardToString();
                    String message = boardString + "|" + position;
                    response = sendRequest(message);

                    // Parse response: boardString|status
                    parts = response.split("\\|");
                    if(parts.length < 2) {
                        System.out.println("Invalid response from server");
                        gameOver = true;
                        continue;
                    }

                    boardStr = parts[0];
                    status = parts[1];

                    System.out.println("Server response: " + status);

                    // Update board with server's response
                    updateBoard(boardStr);

                    // Check game status
                    if("PLAYER_WON".equals(status)) {
                        board.printBoard();
                        System.out.println("Player won!");
                        gameOver = true;
                    } else if("SERVER_WON".equals(status)) {
                        board.printBoard();
                        System.out.println("Server won!");
                        gameOver = true;
                    } else if("DRAW".equals(status)) {
                        board.printBoard();
                        System.out.println("It is a draw");
                        gameOver = true;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number");
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private String sendRequest(String message) throws Exception {
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Sending to server: " + message);
            out.println(message);
            out.flush();

            String response = in.readLine();
            return response;

        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        }
    }

    private String boardToString() {
        int[] boardArray = board.getBoardArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < boardArray.length; i++) {
            sb.append(boardArray[i]);
            if (i < boardArray.length - 1) sb.append(",");
        }
        return sb.toString();
    }

    private void updateBoard(String boardString) {
        String[] values = boardString.split(",");
        for (int i = 0; i < values.length && i < 9; i++) {
            int value = Integer.parseInt(values[i]);
            int pos = i + 1;
            board.makeMove(pos, value);
        }
    }
}