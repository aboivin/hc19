import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Slide {

    public final Picture picture1;
    public final Picture picture2;
    public final List<Integer> keywords;
    public final int doublon;

    public Slide(Picture picture1, Picture picture2) {
        this.picture1 = picture1;
        this.picture2 = picture2;
        List<Integer> keywords = new ArrayList<>(picture1.keywords);
        keywords.addAll(picture2.keywords);
        this.keywords = keywords.stream().distinct().collect(toList());
        this.doublon = picture1.keywords.size() + picture2.keywords.size() - keywords.size();
    }


    public Slide(Picture picture1) {
        this.picture1 = picture1;
        this.picture2 = null;
        List<Integer> keywords = new ArrayList<>(picture1.keywords);
        this.keywords = keywords;
        this.doublon = 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Slide.class.getSimpleName() + "[", "]")
                .add("picture1=" + picture1)
                .add("picture2=" + picture2)
                .add("keywords=" + keywords)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slide slide = (Slide) o;
        return Objects.equals(picture1, slide.picture1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(picture1);
    }

    public boolean matchThreshold(int threshold) {
        return 40 * doublon <= threshold;
    }
}
