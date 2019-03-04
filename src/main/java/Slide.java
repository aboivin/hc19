import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Slide {

    public final Integer picture1Id;
    public final Integer picture2Id;
    public final List<Integer> keywords;
    public final int doublon;

    public Slide(Picture picture1, Picture picture2) {
        List<Integer> keywords = new ArrayList<>(picture1.keywords);
        keywords.addAll(picture2.keywords);
        this.keywords = keywords.stream().distinct().sorted().collect(toList());
        this.doublon = picture1.keywords.size() + picture2.keywords.size() - this.keywords.size();
        this.picture1Id = picture1.id;
        this.picture2Id = picture2.id;
    }

    public Slide(Picture picture1) {
        this.keywords = picture1.keywords;
        this.doublon = 0;
        this.picture1Id = picture1.id;
        this.picture2Id = null;
    }

    public int get(int i) {
        return this.keywords.get(i);
    }

    public int size() {
        return keywords.size();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Slide.class.getSimpleName() + "[", "]")
                .add("keywords=" + keywords)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slide slide = (Slide) o;
        return Objects.equals(picture1Id, slide.picture1Id) &&
                Objects.equals(picture2Id, slide.picture2Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(picture1Id, picture2Id);
    }

    public boolean matchThreshold(int threshold) {
        return keywords.size() - doublon >= threshold;
    }
}
