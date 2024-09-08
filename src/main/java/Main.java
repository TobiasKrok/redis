import configuration.RedisConfiguration;
import core.Redis;
import org.apache.commons.cli.*;

import java.util.Arrays;

public class Main {
  public static void main(String[] args) throws ParseException {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");


    Options options = new Options();

    options.addOption("--port", "Redis server port");
    options.addOption("--replicaof", "Configure Redis instance as a slave");


    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    RedisConfiguration.Builder builder = new RedisConfiguration.Builder();


      if(cmd.hasOption("--port")) {
        builder.withPort(cmd.getParsedOptionValue("--port"));
      } else {
        // default port
        builder.withPort(6379);
      }
      //if(cmd.hasOption("--re"))

      Redis redis = new Redis(builder.build());
      redis.startServer();
  }
}
