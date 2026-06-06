package vgu.pe2026.ttt.basic;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Server_multi implements GameIO {
    private final PrintStream out;
    private final Scanner in;

    public Server_multi(PrintStream out, Scanner in) {
        this.out = out;
        this.in = in;
    }

    @Override
    public void print(String s) {
        out.print(s);
    }

    @Override
    public void println(String s) {
        out.println(s);
    }

    @Override
    public String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " 
                        + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                ClientHandler player = new ClientHandler(clientSocket);
                new Thread(player).start();
            }
        }   
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try(
            PrintStream clientOut = new PrintStream(socket.getOutputStream(), true);
            Scanner networkInput = new Scanner(socket.getInputStream())){

            GameIO io = new Server_multi(clientOut, networkInput);
            Game game = new Game(1, io);
            game.play();
            clientOut.println("END_OF_GAME");

        }catch(IOException e){
            System.err.println("Client disconnect: " + e.getMessage());
        } finally{
            try{socket.close();}
            catch(Exception e1){
                e1.printStackTrace();
            }
        }
        
    }
}