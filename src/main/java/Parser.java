import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class Parser {

    private static final String A_PATH = "/home/aboivin/workspace/hc19/src/main/resources/a_example.txt";
    private static final String B_PATH = "/home/aboivin/workspace/hc19/src/main/resources/b_lovely_landscapes.txt";
    private static final String C_PATH = "/home/aboivin/workspace/hc19/src/main/resources/c_memorable_moments.txt";
    private static final String D_PATH = "/home/aboivin/workspace/hc19/src/main/resources/d_pet_pictures.txt";
    private static final String E_PATH = "/home/aboivin/workspace/hc19/src/main/resources/e_shiny_selfies.txt";

    private static final int CHUNK_SIZE = 5000;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.lines(Paths.get(D_PATH)).skip(1).collect(toList());

        Collection<Slide> slideShow = Collections.synchronizedCollection(new ArrayList<>());
        IntStream.range(0, 4).parallel().forEach(chunk -> {
            System.out.println("======= CHUNK " + chunk + "===============");
            List<Picture> horizontalSlides = new ArrayList<>();
            List<Picture> verticalSlides = new ArrayList<>();
            for (int i = CHUNK_SIZE * chunk; i < CHUNK_SIZE * (chunk + 1); i++) {
                Picture picture = new Picture(i, lines.get(i).split(" "));
                if (picture.orientation == Orientation.HORIZONTAL) {
                    horizontalSlides.add(picture);
                } else {
                    verticalSlides.add(picture);
                }
            }

            List<Slide> allSlides = createSlides(horizontalSlides, verticalSlides);
            List<Slide> slides = generateSlideShow(allSlides);
            slideShow.addAll(slides);
        });
        String result = formatResult(slideShow);
        write(result);
    }

    private static List<Slide> createSlides(List<Picture> horizontalSlides, List<Picture> verticalSlides) {
        List<Slide> verticalPics = new ArrayList<>();
        for (int i = 0; i < verticalSlides.size(); i = i + 2) {
            if (i == verticalSlides.size() - 1) {
                break;
            }
            verticalPics.add(new Slide(verticalSlides.get(i), verticalSlides.get(i + 1)));
        }
        List<Slide> horizontalPics = horizontalSlides.stream().map(Slide::new).collect(toList());

        List<Slide> allSlides = new ArrayList<>();
        allSlides.addAll(horizontalPics);
        allSlides.addAll(verticalPics);
        return allSlides;
    }

    private static List<Slide> generateSlideShow(List<Slide> fullSlides) {
        ListMultimap<Slide, Node> graph = buildGraph(fullSlides);

        List<Slide> slides = new ArrayList<>();
        Comparator<Tuple> comparing = comparing(tuple -> tuple.node.score);
        int bip = 0;
        final AtomicReference<Slide> first = new AtomicReference<>(findBest(fullSlides, graph).get().slide);
        while (true) {

            List<Tuple> tuples = new ArrayList<>();
            graph.forEach((slide, node) -> {
                if (node.slide.equals(first.get())) {
                    tuples.add(new Tuple(slide, node));
                }
            });
            Optional<Tuple> first1 = tuples.stream().sorted(comparing.reversed()).findFirst();
            if (!first1.isPresent()) {
                break;
            }

            first.set(first1.get().slide);
            if (bip++ % 100 == 0) {
                System.out.println(bip);
            }

            slides.add(first.get());

            graph.removeAll(first.get());
            graph.asMap().values().remove(first.get());
        }
        return slides;
    }

    private static ListMultimap<Slide, Node> buildGraph(List<Slide> singlePics) {
        ListMultimap<Slide, Node> multiMap = ArrayListMultimap.create();
        for (Slide slide1 : singlePics) {
            for (Slide slide2 : singlePics) {
                multiMap.put(slide1, new Node(slide2, ScoreComputer.computeScore(slide1, slide2)));
            }
        }
        System.out.println("Graph built.");
        return multiMap;
    }

    private static String formatResult(Collection<Slide> slides) {
        return slides.size() + "\n" + slides.stream().map(s -> s.picture1.id + " " + s.picture2.id).collect(joining("\n"));
    }

    private static Optional<Tuple> findBest(List<Slide> singlePics, ListMultimap<Slide, Node> multiMap) {
        Comparator<Tuple> comparing = comparing(tuple -> tuple.node.score);
        Comparator<Node> comparing2 = comparing(node -> node.score);
        return singlePics.stream().map(slide -> {
            List<Node> nodes = multiMap.get(slide);
            Node node = nodes.stream().sorted(comparing2.reversed()).findFirst().get();
            return new Tuple(slide, node);
        }).sorted(comparing.reversed()).findFirst();
    }

    private static long computeScore(List<Slide> slides) {
        long score = 0;
        for (int i = 0; i < slides.size(); i++) {
            if (i == 0) {
                continue;
            }
            score += ScoreComputer.computeScore(slides.get(i - 1), slides.get(i));
        }
        return score;
    }

    private static void write(String content) throws IOException {
        Path path = Paths.get("/home/aboivin/workspace/hc19/src/main/resources/output.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        }
    }
}
