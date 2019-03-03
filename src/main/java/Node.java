import java.util.Objects;
import java.util.StringJoiner;

public class Node implements Comparable<Node> {

    public final Slide slide;

    public int score;

    public Node(Slide slide, int score) {
        this.slide = slide;
        this.score = score;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
                .add("slide=" + slide)
                .add("score=" + score)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(slide, node.slide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slide);
    }

    @Override
    public int compareTo(Node node) {
        if(node.score == score) {
            return 0;
        }
        return score > node.score ? -1 : 1; // reversed
    }
}
