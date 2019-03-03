import static java.util.stream.Collectors.toList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class Picture {

    public final Orientation orientation;

    public final List<Long> keywords;

    public final String id;

    private static AtomicLong index = new AtomicLong(0);

    public static Map<String, Long> keywordsMap = new ConcurrentHashMap<>();

    public Picture(int id, String[] arrays) {
        this.id = String.valueOf(id);
        this.orientation = arrays[0].equals("V") ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        List<String> kw = Arrays.stream(arrays).skip(2).collect(toList());
        this.keywords = kw.stream().map(s -> keywordsMap.computeIfAbsent(s, st -> index.getAndIncrement())).sorted().collect(toList());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Picture.class.getSimpleName() + "[", "]")
                .add("orientation=" + orientation)
                .add("keywords=" + keywords)
                .toString();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        return Objects.equals(id, picture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
