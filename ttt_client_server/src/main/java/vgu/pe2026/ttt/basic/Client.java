package vgu.pe2026.ttt.basic;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception{
        try {
            Socket socket = new Socket("localhost", 8080);
            
            Scanner serverIn = new Scanner(socket.getInputStream());
            PrintWriter clientOut = new PrintWriter(socket.getOutputStream(), true);
            Scanner consoleIn = new Scanner(System.in);
            
            
            // Loop reading local input and writing it to the server
            while (serverIn.hasNextLine()) {
                String line = serverIn.nextLine();

                if(line.equals("END_OF_GAME")){
                    break;
                }
                // String input = consoleIn.nextLine();
                // clientOut.println(input);

                System.out.println(line);

                if (line.contains("Player#1's turn") || 
                    line.contains("Please, input a valid number [1-9]") ||
                    line.contains("The cell is occupied!")) {

                    String input = consoleIn.nextLine();
                    clientOut.println(input);

                    if(input.equalsIgnoreCase("q")){
                        break;
                    }
                }
            }
            
            socket.close();
            System.out.println("Disconnect from server");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
