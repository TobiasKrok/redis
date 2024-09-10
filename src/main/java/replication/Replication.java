package replication;

import command.PingCommand;
import configuration.ReplicationConfiguration;
import core.RespParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Replication {

    private final ReplicationRole replicationRole;

    // can be null
    private final String masterHost;
    // can be null
    private final int masterPort;

    // not good to use uuid but w/e
    private final String id = UUID.randomUUID().toString();

    private final int offset = 0;

    public Replication(ReplicationConfiguration replicationConfiguration) {
        this.replicationRole = replicationConfiguration.getRole();
        this.masterHost = replicationConfiguration.getHost();
        this.masterPort = replicationConfiguration.getPort();

    }

    public void startReplicationServer() {
        try(SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(masterHost, masterPort));
            initiateHandShake(channel);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void initiateHandShake(SocketChannel channel) throws IOException {
        System.out.println("Initiating handshake...");
        channel.write(ByteBuffer.wrap(RespParser.fromArray(List.of("PING"))));
        System.out.println("Sent PING to master");

    }

    public ReplicationRole getRole() {
        return replicationRole;
    }

    //todo toString() instead
    public List<String> getReplicationInfo() {
        List<String> info = new ArrayList<>();

        info.add("role:" + replicationRole.toString().toLowerCase());
        info.add("master_replid:" + id);
        info.add("master_repl_offset:" + offset);
        return info;
    }

    public String getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }
}
