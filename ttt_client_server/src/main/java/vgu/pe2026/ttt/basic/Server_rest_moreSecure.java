package vgu.pe2026.ttt.basic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// Assume player always play fair
// Server format: serverLastMove | board arr separate by ','
// client send format: postion | board arr serparate by ',' 
// parse message, place move(first available spot), send back response 

/*
Assume player can cheat
Client send format: position | board | hashed board + sk
Check if it the correct board by hasing the sent board with the shared sk
Reject if not equal
*/


public class Server_rest_moreSecure {
    private static final int PORT = 8080;
    private static final String SECRET_KEY = System.getenv("SECRET_KEY");

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server listen on port 8080");
            while(true){

                Socket connection = serverSocket.accept();
                System.out.println("New client connected: " 
                        + connection.getInetAddress() + ":" + connection.getPort());
                handleClient(connection);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket connection){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);

            String clientMsg = in.readLine();
            System.out.println("Client message: " + clientMsg);

            if(clientMsg == null) {
                connection.close();
                return;
            }    

            String[] parts = clientMsg.split("\\|");
            
            if(parts.length < 3){
                connection.close();
                return;
            }

            // String command = parts[0];
            int clientPos = Integer.parseInt(parts[0]);
            String boardStr = parts[1];
            String clientHMAC = parts[2];

            String calHMAC = generateHMAC(boardStr);
            if(!calHMAC.equals(clientHMAC)){
                System.out.println("Expected: " + calHMAC);
                System.out.println("Received: " + clientHMAC);
                connection.close();
                return;
            }

            System.out.println("Board verified");
            
            int lastServerMove = -1;

            // server moves
            String[] values = boardStr.split(",");
            for (int i = 0; i < values.length && i < 9; i++) {
                if ("0".equals(values[i])) {
                    values[i] = "2";
                    lastServerMove = i + 1;
                    break;
                }
            }
            String updatedBoard = String.join(",", values);

            // String response = formatResponse(lastServerMove, updatedBoard);
            String response = lastServerMove + "|" + updatedBoard;
            out.println(response);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private static String formatResponse(int lastMove, String board) {        
    //     return lastMove + "|" + board;
    // }

    private static String generateHMAC(String boardString){
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
