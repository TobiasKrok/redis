import core.Redis;

import java.util.Arrays;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");


      int port = 6379;
      // only --port is supported for now
      if(args.length == 2) {
        port = Integer.parseInt(args[1]);
      }
      Redis redis = new Redis(port);
      redis.startServer();
  }
}
