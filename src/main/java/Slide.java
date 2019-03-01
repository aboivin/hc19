import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Slide {

    public final Picture picture1;
    public final Picture picture2;
    public final List<Long> keywords;

    public Slide(Picture picture1, Picture picture2) {
        this.picture1 = picture1;
        this.picture2 = picture2;
        List<Long> keywords = new ArrayList<>(picture1.keywords);
        keywords.addAll(picture2.keywords);
        this.keywords = keywords;
    }


    public Slide(Picture picture1) {
        this.picture1 = picture1;
        this.picture2 = null;
        List<Long> keywords = new ArrayList<>(picture1.keywords);
        this.keywords = keywords;
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
}
