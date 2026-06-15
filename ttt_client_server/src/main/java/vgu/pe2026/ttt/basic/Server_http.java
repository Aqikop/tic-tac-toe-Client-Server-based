package vgu.pe2026.ttt.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server_http {
    private static final int PORT = 8080;

    public static void main(String[] args){
        try {
            // ServerSocket serverSocket = new ServerSocket(PORT);
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", new playHandle());
            server.setExecutor(null);
            server.start();
            // while(true){
            //     Socket connection = serverSocket.accept();
            //     System.out.println("New client connected: " 
            //             + connection.getInetAddress() + ":" + connection.getPort());
            //     handleClient(connection);
            // }
            System.out.println("Server listen on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    static class playHandle implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            br.close();
            
            String clientMsg = requestBody.toString().trim();
            System.out.println("Client message: " + clientMsg);
            
            String response;
            
            if("INIT".equals(clientMsg)) {
                String emptyBoard = "0,0,0,0,0,0,0,0,0";
                response = emptyBoard + "|READY";
            } else {
                String[] parts = clientMsg.split("\\|");
                if(parts.length < 2) {
                    response = "ERROR";
                } else {
                    String boardStr = parts[0];
                    int playerPos = Integer.parseInt(parts[1]);
                    response = processMove(boardStr, playerPos);
                }
            }
            
            System.out.println("Sending response: " + response);
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // private static void handleClient(Socket connection){
    //     try {
    //         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    //         PrintWriter out = new PrintWriter(connection.getOutputStream(), true);

    //         String clientMsg = in.readLine();
    //         System.out.println("Client message: " + clientMsg);

    //         if(clientMsg == null) {
    //             connection.close();
    //             return;
    //         }

    //         String response;

    //         if("INIT".equals(clientMsg)) {
    //             // Send empty board
    //             String emptyBoard = "0,0,0,0,0,0,0,0,0";
    //             response = emptyBoard + "|READY";
    //         } else {
    //             // Parse: boardString|position
    //             String[] parts = clientMsg.split("\\|");
    //             if(parts.length < 2) {
    //                 connection.close();
    //                 return;
    //             }

    //             String boardStr = parts[0];
    //             int playerPos = Integer.parseInt(parts[1]);

    //             // Process game move
    //             response = processMove(boardStr, playerPos);
    //         }

    //         System.out.println("Sending response: " + response);
    //         out.println(response);
    //         connection.close();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    private static String processMove(String boardStr, int playerPos) {
        String[] values = boardStr.split(",");
        
        // Place player move
        if(playerPos >= 1 && playerPos <= 9 && "0".equals(values[playerPos - 1])) {
            values[playerPos - 1] = "1";
        }

        // Check if player won
        int winner = checkWinner(values);
        if(winner == 1) {
            String updatedBoard = String.join(",", values);
            return updatedBoard + "|PLAYER_WON";
        }

        // Check draw
        if(isBoardFull(values)) {
            String updatedBoard = String.join(",", values);
            return updatedBoard + "|DRAW";
        }

        // Server makes move (first available 0)
        int serverPos = -1;
        for (int i = 0; i < values.length; i++) {
            if ("0".equals(values[i])) {
                values[i] = "2";
                serverPos = i;
                break;
            }
        }

        // Check if server won
        winner = checkWinner(values);
        if(winner == 2) {
            String updatedBoard = String.join(",", values);
            return updatedBoard + "|SERVER_WON";
        }

        // Check draw
        if(isBoardFull(values)) {
            String updatedBoard = String.join(",", values);
            return updatedBoard + "|DRAW";
        }

        // Game continues
        String updatedBoard = String.join(",", values);
        return updatedBoard + "|CONTINUE";
    }

    private static int checkWinner(String[] values) {
        // Convert to 2D array for easier checking
        int[][] board = new int[3][3];
        for (int i = 0; i < 9; i++) {
            board[i / 3][i % 3] = Integer.parseInt(values[i]);
        }

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }

        // Check main diagonal
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }

        // Check anti-diagonal
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }

        return 0;
    }

    private static boolean isBoardFull(String[] values) {
        for (String val : values) {
            if ("0".equals(val)) {
                return false;
            }
        }
        return true;
    }    
}
