package core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class RespParser {

    // inspired by Jedis (https://github.com/redis/jedis/blob/11a4513ff9581a40530a84e6c8ee019c4a3f9e38/src/main/java/redis/clients/jedis/Protocol.java)
    // also some copy :))
    private static final char BULK_STRING = '$';
    private static final char ARRAY = '*';
    private static final char SIMPLE_STRING = '+';

    private static final char SIMPLE_ERROR = '-';


    public static byte[] fromBulk(String s) {

        return ("$" + s.length() + ctrlf() + s + ctrlf()).getBytes();
    }

    public static byte[] fromSimple(String s) {
        return ("+" + s + ctrlf()).getBytes();
    }

    public static byte[] fromSimpleError(String e, String m) {
        return ("-" + e + " " + m + ctrlf()).getBytes();
    }

    private static String ctrlf() {
        return "\r\n";
    }

    // will add more commands as needed

    // THis will need to be updated lol, just hard code for npw
    public static List<String> read(ByteBuffer buffer) {

        List<String> commandsRaw = new ArrayList<>();
        byte firstByte = buffer.get();

        char type = (char) firstByte;
        // assuming its a command
        if(type == ARRAY) {
            //TODO bad
           commandsRaw = processArray(buffer);
        } else {
            // FIX lATER
             commandsRaw.add("PONG");

        }
        return commandsRaw;
    }

    private static List<String> process(ByteBuffer buffer) {
        byte b = buffer.get();

        List<String> commands = new ArrayList<>();

        char type = (char) b;
        switch (type) {
            case BULK_STRING -> {
                commands.add(processBulkString(buffer));
            }
            case ARRAY -> commands.addAll(processArray(buffer));
        }

        return commands;
    }

    private static String processBulkString(ByteBuffer buffer) {
        byte secondByte = buffer.get(); //length
        int length = Character.getNumericValue((char) secondByte);
        readCtrl(buffer);
        StringBuilder sb = new StringBuilder();

        int read = 0;
        while (read < length) {
            char c = (char)buffer.get();
            sb.append(c);
            read++;
        }

        return sb.toString();
    }



    private static List<String> processArray(ByteBuffer buffer) {
        byte secondByte = buffer.get(); //length TODO remove
        readCtrl(buffer);
        List<String> rawCommands = new ArrayList<>();

        while (buffer.hasRemaining()) {
            rawCommands.addAll(process(buffer));
            readCtrl(buffer);
        }
        return rawCommands;
    }

    //TODO: buffer mark?
    private static void readCtrl(ByteBuffer buffer) {
        if((char)buffer.get() == '\r') {
            if((char)buffer.get() != '\n') {
                throw new RuntimeException("Invalid format");
            }
        }
    }

}
