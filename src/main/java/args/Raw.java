package args;

public class Raw implements Rawable {

    private final byte[] raw;

    public Raw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
}
