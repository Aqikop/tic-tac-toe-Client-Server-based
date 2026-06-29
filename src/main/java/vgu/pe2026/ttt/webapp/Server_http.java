package vgu.pe2026.ttt.webapp;

import java.io.BufferedReader;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/play")
public class Server_http extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        BufferedReader br = request.getReader();

        StringBuilder body = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            body.append(line);
        }

        String clientMsg = body.toString().trim();

        String result;
        if ("INIT".equals(clientMsg)) {
            result = "0,0,0,0,0,0,0,0,0|READY";
        } else {

            String[] parts = clientMsg.split("\\|");

            if (parts.length < 2) {
                result = "ERROR";
            } else {
                String board = parts[0];
                int pos = Integer.parseInt(parts[1]);

                result = processMove(board, pos);
            }
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "*");

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(result);
    }   

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


// Non modify http client plays with web server (use JakartaEE)

// create new maven project webapp (tomcat ver > 10, jakarta)
// package the server code to .war file
// modify client code browser based( to match port) 