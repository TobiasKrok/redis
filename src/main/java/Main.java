import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

        int port = 6379;
        try {
            EventLoop eventLoop = new EventLoop();
            eventLoop.run(port);
        } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
        }
  }
}
