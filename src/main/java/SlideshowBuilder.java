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
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import com.google.common.collect.MinMaxPriorityQueue;

public class SlideshowBuilder {

    private static final String A_PATH = "/home/aboivin/workspace/hc19/src/main/resources/a_example.txt";
    private static final String B_PATH = "/home/aboivin/workspace/hc19/src/main/resources/b_lovely_landscapes.txt";
    private static final String C_PATH = "/home/aboivin/workspace/hc19/src/main/resources/c_memorable_moments.txt";
    private static final String D_PATH = "/home/aboivin/workspace/hc19/src/main/resources/d_pet_pictures.txt";
    private static final String E_PATH = "/home/aboivin/workspace/hc19/src/main/resources/e_shiny_selfies.txt";

    private static final int CHUNK_SIZE = 5_000;
    public static final int ITERATION = 1;

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.lines(Paths.get(E_PATH)).skip(1).collect(toList());

        Collection<Slide> slideShow = Collections.synchronizedCollection(new ArrayList<>());
        IntStream.range(0, ITERATION).forEach(chunk -> {
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
        System.out.println(computeScore(new ArrayList<>(slideShow)));
        write(result);
    }

    private static List<Slide> createSlides(List<Picture> horizontalPics, List<Picture> verticalPics) {
        List<Slide> allSlides = new ArrayList<>();

        OptionalDouble average = verticalPics.stream().mapToInt(v -> v.keywords.size()).average();
        if(average.isPresent()) {
            int threshold = (int) (2 * average.getAsDouble());
            List<Slide> verticalSlides = new ArrayList<>();
            int bip = 0;
            while (verticalPics.size() > 1) {
                int i = 0;
                while (i < verticalPics.size()) {
                    Picture v1 = verticalPics.get(i);
                    Picture bestMatch = null;
                    f2:
                    for (Picture v2 : verticalPics) {
                        if (bip++ % 1_000_000 == 0) {
                            System.out.println(bip - 1 + " " + verticalSlides.size());
                        }
                        if (v1 != v2) {
                            Slide slide = new Slide(v1, v2);
                            if (slide.matchThreshold(threshold)) {
                                bestMatch = v2;
                                break f2;
                            }
                        }
                    }
                    if (bestMatch != null) {
                        verticalSlides.add(new Slide(v1, bestMatch));
                        verticalPics.remove(v1);
                        verticalPics.remove(bestMatch);
                    }
                    i++;
                }
                threshold--;
            }
            allSlides.addAll(verticalSlides);
        }
        List<Slide> horizontalSlides = horizontalPics.stream().map(Slide::new).collect(toList());
        allSlides.addAll(horizontalSlides);
        return allSlides;
    }

    private static List<Slide> generateSlideShow(List<Slide> fullSlides) {
        Map<Slide, MinMaxPriorityQueue<Node>> graph = buildGraph(fullSlides);

        List<Slide> slides = new ArrayList<>();
        int bip = 0;
        final AtomicReference<Slide> currentSlide = new AtomicReference<>(findBest(graph).get().slide);
        slides.add(currentSlide.get());
        while (true) {
            MinMaxPriorityQueue<Node> nextNodes = graph.get(currentSlide.get());
            graph.remove(currentSlide.get());

            Node bestNode;
            do {
                bestNode = nextNodes.poll();
            } while (bestNode != null && slides.contains(bestNode.slide));


            Slide nextSlide;
            if (bestNode == null || bestNode.score == 0) {
                Optional<Slide> nextSlide1 = findBest(graph).map(t -> t.slide);
                if (!nextSlide1.isPresent()) {
                    break;
                }
                nextSlide = nextSlide1.get();
            } else {
                nextSlide = bestNode.slide;
            }

            currentSlide.set(nextSlide);
            if (bip++ % 1000 == 0) {
                System.out.println(bip);
            }

            slides.add(currentSlide.get());
        }
        return slides;
    }

    private static Map<Slide, MinMaxPriorityQueue<Node>> buildGraph(List<Slide> slides) {
        long i = 0;
        Map<Slide, MinMaxPriorityQueue<Node>> multimap = new HashMap<>();
        for (Slide slide1 : slides) {
            for (Slide slide2 : slides) {
                if (slide1 != slide2) {
                    int score = ScoreComputer.computeScore(slide1, slide2);
                    if(score != 0) {
                        MinMaxPriorityQueue<Node> queue = multimap.computeIfAbsent(slide1, s -> MinMaxPriorityQueue.maximumSize(50).create());
                        Node node = queue.peekLast();
                        if(node == null || node.score < score) {
                            queue.add(new Node(slide2, score));
                        }
                    }
                }
                if (i++ % 10_000_000 == 0) {
                    System.out.println(i);
                }
            }
        }
        System.out.println("Graph built.");
        return multimap;
    }

    private static String formatResult(Collection<Slide> slides) {
        return slides.size() + "\n" + slides.stream().map(s -> s.picture1Id + (s.picture2Id != null ? " " + s.picture2Id : "")).collect(joining("\n"));
    }

    private static Optional<Tuple> findBest(Map<Slide, MinMaxPriorityQueue<Node>> multiMap) {
        List<Tuple> bestTuples = new ArrayList<>();
        multiMap.forEach((slide, nodes) -> {
            Node maxNode = nodes.peek();
            bestTuples.add(new Tuple(slide, maxNode));
        });

        Tuple best = null;
        int score = -1;
        for (Tuple bestTuple : bestTuples) {
            if (bestTuple.node.score > score) {
                best = bestTuple;
            }
        }
        return Optional.ofNullable(best);
    }

    private static long computeScore(List<Slide> slides) {
        long score = 0;
        for (int i = 0; i < slides.size() - 1; i++) {
            score += ScoreComputer.computeScore(slides.get(i), slides.get(i + 1));
        }
        return score;
    }

    private static void write(String content) throws IOException {
        Path path = Paths.get("/home/aboivin/workspace/hc19/src/main/java/output.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        }
    }
}
