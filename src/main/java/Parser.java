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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class Parser {

    private static final String A_PATH = "/home/aboivin/workspace/hc19/src/main/resources/a_example.txt";
    private static final String B_PATH = "/home/aboivin/workspace/hc19/src/main/resources/b_lovely_landscapes.txt";
    private static final String C_PATH = "/home/aboivin/workspace/hc19/src/main/resources/c_memorable_moments.txt";
    private static final String D_PATH = "/home/aboivin/workspace/hc19/src/main/resources/d_pet_pictures.txt";
    private static final String E_PATH = "/home/aboivin/workspace/hc19/src/main/resources/e_shiny_selfies.txt";

    private static final int CHUNK_SIZE = 3_000;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.lines(Paths.get(B_PATH)).skip(1).collect(toList());

        Collection<Slide> slideShow = Collections.synchronizedCollection(new ArrayList<>());
        IntStream.range(0, 1).parallel().forEach(chunk -> {
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
        Map<Slide, Queue<Node>> graph = buildGraph(fullSlides);

        List<Slide> slides = new ArrayList<>();
        int bip = 0;
        final AtomicReference<Slide> currentSlide = new AtomicReference<>(findBest(graph).get().slide);
        slides.add(currentSlide.get());
        while (true) {
            List<Node> nextNodes = graph.get(currentSlide.get());
            graph.remove(currentSlide.get());

//            nextNodes.removeIf(node -> slides.contains(node.slide));
            int bestScore = -1;
            Node bestNode = null;
            for (Node nextNode : nextNodes) {
                if(nextNode.score > bestScore) {
                    bestNode = nextNode;
                }
            }
//            Optional<Node> nextNode = nextNodes.stream().sorted(scoreComparator.reversed()).findFirst();
            if (bestNode == null) {
                break;
            }

            Slide nextSlide = bestNode.slide;
            if(bestNode.score == 0) {
                nextSlide = findBest(graph).map(t -> t.slide).get();
            }

            graph.values().forEach(l -> l.stream()
                                         .filter(node -> node.slide.equals(currentSlide.get()))
                                         .forEach(n -> n.score = -2));

            currentSlide.set(nextSlide);
            if (bip++ % 100 == 0) {
                System.out.println(bip);
            }

            slides.add(currentSlide.get());
        }
        return slides;
    }

    private static Map<Slide, List<Node>> buildGraph(List<Slide> slides) {
        Map<Slide, List<Node>> multimap = new HashMap<>();
        for (Slide slide1 : slides) {
            for (Slide slide2 : slides) {
                if(slide1 != slide2) {
                    List<Node> list = multimap.computeIfAbsent(slide1, s -> new ArrayList<>());
                    list.add(new Node(slide2, ScoreComputer.computeScore(slide1, slide2)));
                }
            }
        }
        System.out.println("Graph built.");
        return multimap;
    }

    private static String formatResult(Collection<Slide> slides) {
        return slides.size() + "\n" + slides.stream().map(s -> s.picture1.id + (s.picture2 != null ? " " + s.picture2.id : "")).collect(joining("\n"));
    }

    private static Optional<Tuple> findBest(Map<Slide, List<Node>> multiMap) {
        PriorityQueue q = new PriorityQueue();
        q.peek()
        List<Tuple> bestTuples = new ArrayList<>();
        multiMap.forEach((slide, nodes) -> {
            Node maxNode = null;
            long bestScore = -1;
            for (Node node : nodes) {
                if(node.score > bestScore) {
                    bestScore = node.score;
                    maxNode = node;
                }
            }
            bestTuples.add(new Tuple(slide, maxNode));
        });

        Tuple best = null;
        int score = -1;
        for (Tuple bestTuple : bestTuples) {
            if(bestTuple.node.score > score) {
                best = bestTuple;
            }
        }
        return Optional.ofNullable(best);
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
