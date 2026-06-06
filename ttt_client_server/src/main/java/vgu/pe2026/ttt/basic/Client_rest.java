package vgu.pe2026.ttt.basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// client send format: postion | board arr serparate by ','
// client start game first, move, then connect and send to server to move
// server format: serverLastMove | board arr separate by ','
// disconnect server after send and receive answer

/*
To ensure client cannot cheat by sending a fake board
Implement a HMAC-SHA256 with a share secret key
Sent format: position | board | hashed board + sk to server
*/

public class Client_rest {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Board_2D board;
    private Scanner scanner;
    private humanPlayer p1;

    public Client_rest() throws Exception{
        // this.socket = new Socket(HOST, PORT);
        // this.out = new PrintWriter(socket.getOutputStream(), true);
        // this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.board = new Board_2D();
        this.scanner = new Scanner(System.in);
        this.p1 = new humanPlayer(1);

        System.out.println("Hi");
    }

    public static void main(String[] args) {
        try {
            Client_rest client = new Client_rest();
            client.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void play() {
        boolean gameOver = false;
        
        while (!gameOver) {
            board.printBoard();
            
            // Check winner
            int winner = board.checkWinner();
            if(winner == 1 || winner == 2){
                System.out.println("Player#" + winner + " won!");
                gameOver = true;
                break;
            }
            else if(winner == -1){
                System.out.println("It is a draw");
                gameOver = true;
                break;
            }
            
            // Get player move
            System.out.println("Your turn. Enter position (1-9) or 'quit' to exit:");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("q")) {
                break;
            }
            
            try {
                int position = Integer.parseInt(input);
                // Turn into while loop to avoid using complete
                if(position >= 1 && position <= 9 && board.isValidPos(position)){
                    board.makeMove(position, 1);
                } else if(position < 1 || position > 9){
                    System.out.println("Please, input a valid number [1-9]");
                    continue;
                } else{
                    System.out.println("The cell is occupied!");
                    continue;
                }
                
                // Connect to server after validating move
                Socket socket = null;
                PrintWriter out = null;
                BufferedReader in = null;
                
                try {
                    socket = new Socket(HOST, PORT);
                    out = new PrintWriter(socket.getOutputStream(), true); 
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Send move to server
                    String boardString = boardToString();

                    String hmac = generateHMAC(boardString);

                    String message = position + "|" + boardString + "|" + hmac;

                    // String fakeBoard = "1,1,1,1,0,0,0,0,0";
                    // String fakeMessage = "5|" + fakeBoard;
                    // System.out.println("Sending to server: " + fakeMessage);
                    // out.println(fakeMessage);
                    
                    System.out.println("Sending to server: " + message);
                    out.println(message);
                    out.flush();
                    
                    // Receive response
                    String response = in.readLine();
                    System.out.println("Server response: " + response);
                    
                    if (response == null) {
                        System.out.println("Connection closed by server");
                        gameOver = true;
                    } else {
                        // Parse response: "serverMove|boardArray"
                        String[] parts = response.split("\\|");
                        if (parts.length < 2) {
                            System.out.println("Invalid response format");
                            continue;
                        }
                        
                        int serverMove = Integer.parseInt(parts[0]);
                        String boardStr = parts[1];
                        
                        // Display server move
                        if (serverMove > 0) {
                            System.out.println("Server played position: " + serverMove);
                            board.makeMove(serverMove, 2);
                        }

                        updateBoard(boardStr); // synchonize board
                    }
                } finally {
                    // Properly close resources
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            } catch (Exception e) {
                System.err.println("Connection error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
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
            if (value != 0) {
                int pos = i + 1;
                board.makeMove(pos, value);
            }
        }
    }

    private String generateHMAC(String boardString){
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secret_key);
            byte[] hmacData = sha256HMAC.doFinal(boardString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hmacData);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}


/*
Powershell:
set SECRET_KEY=my-shared-secret-key-12345
java -cp target/classes vgu.pe2026.ttt.basic.Server_rest
java -cp target/classes vgu.pe2026.ttt.basic.Client_rest

Cmd:
set SECRET_KEY=my-shared-secret-key-12345
java -cp target/classes vgu.pe2026.ttt.basic.Server_rest
java -cp target/classes vgu.pe2026.ttt.basic.Client_rest
*/