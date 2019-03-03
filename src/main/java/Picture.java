import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Picture {

    public final Orientation orientation;

    public final List<Integer> keywords;

    public final int id;

    private static AtomicInteger index = new AtomicInteger(0);

    public static Map<String, Integer> keywordsMap = new ConcurrentHashMap<>();

    public Picture(int id, String[] arrays) {
        this.id = id;
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
