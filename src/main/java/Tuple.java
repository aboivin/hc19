import java.util.StringJoiner;

public class Tuple {
    public final Slide slide;
    public final Node node;
    public Tuple(Slide slide, Node node) {
        this.slide = slide;
        this.node = node;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Tuple.class.getSimpleName() + "[", "]")
                .add("slide=" + slide)
                .add("node=" + node)
                .toString();
    }
}
