import configuration.RedisConfiguration;
import configuration.ReplicationConfiguration;
import core.Redis;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws ParseException {

        Options options = new Options();

        options.addOption(Option.builder()
                .longOpt("port")
                .hasArg()
                .desc("Redis server port")
                .build());

        // Add --replicaof option (with two arguments for host and port)
        options.addOption(Option.builder()
                .longOpt("replicaof")
                .hasArgs()
                .valueSeparator(' ')
                .desc("Configure Redis instance as a slave in the form of 'host port'")
                .build());

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = parser.parse(options, args);
        RedisConfiguration.Builder builder = new RedisConfiguration.Builder();

        if (cmd.hasOption("port")) {
            builder.withPort(Integer.parseInt(cmd.getOptionValue("port")));
        }

        if(cmd.hasOption("replicaof")) {
            String[] values = cmd.getOptionValues("replicaof");
            if (values != null && values.length == 2) {
                String replicaHost = values[0];
                String replicaPort = values[1];
                builder.withReplication(replicaHost, Integer.parseInt(replicaPort));
            } else {
                throw new MissingArgumentException("usage: --replicaof <host> <port>");
            }
        }


        Redis redis = new Redis(builder.build());
        redis.startServer();
    }
}
