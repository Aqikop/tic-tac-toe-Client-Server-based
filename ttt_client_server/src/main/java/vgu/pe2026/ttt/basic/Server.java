package vgu.pe2026.ttt.basic;

import vgu.Game;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws Exception{
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server listen on port 8080");

            while(true){
                Socket connection = serverSocket.accept();
                System.out.println("Client connected.");

                PrintStream originalOut = System.out;
                PrintStream clientOut = new PrintStream(connection.getOutputStream(), true);

                Scanner networkInput = new Scanner(connection.getInputStream());

                System.setOut(clientOut);

                Game game = new Game(1, networkInput); //Human alsways first
                game.play();                

                System.out.println("END_OF_GAME");

                System.setOut(originalOut);
                System.out.println("Game over.");

                // networkInput.close();
                connection.close();
                // serverSocket.close();
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
