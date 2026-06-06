package vgu.pe2026.ttt.basic;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server_multi_pool implements GameIO {
    private final PrintStream out;
    private final Scanner in;
    private static final int PORT = 8080;
    private static final int MAX_THREADS = 4;

    public Server_multi_pool(PrintStream out, Scanner in) {
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
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Tic-Tac-Toe Server is running on port " + PORT);
            System.out.println("Thread pool size = " + MAX_THREADS);

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " 
                        + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                pool.execute(new ClientHandlerr(clientSocket));
            }
        }   
        catch(IOException e){
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}

class ClientHandlerr implements Runnable{
    private final Socket socket;

    public ClientHandlerr(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try(
            PrintStream clientOut = new PrintStream(socket.getOutputStream(), true);
            Scanner networkInput = new Scanner(socket.getInputStream())){

            GameIO io = new Server_multi_pool(clientOut, networkInput);
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