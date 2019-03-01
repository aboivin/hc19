import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Parser {

    private static final String A_PATH = "/home/aboivin/workspace/hc19/src/main/resources/a_example.txt";
    private static final String B_PATH = "/home/aboivin/workspace/hc19/src/main/resources/b_lovely_landscapes.txt";
    private static final String C_PATH = "/home/aboivin/workspace/hc19/src/main/resources/c_memorable_moments.txt";
    private static final String D_PATH = "/home/aboivin/workspace/hc19/src/main/resources/d_pet_pictures.txt";
    private static final String E_PATH = "/home/aboivin/workspace/hc19/src/main/resources/e_shiny_selfies.txt";

    private static List<Picture> pictures = new ArrayList<>();
    private static List<Picture> vertical = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.lines(Paths.get(E_PATH)).skip(1).collect(toList());
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0 || i>6000 || i<3000) {
                continue;
            }
            Picture picture = new Picture(i, lines.get(i).split(" "));
         //   if(picture.orientation == Orientation.HORIZONTAL) {
                pictures.add(picture);
           // } else {
             //   vertical.add(picture);
           // }
        }

        List<Slide> singlePics = new ArrayList<>();
        for (int i = 0; i < pictures.size(); i=i+2) {
            singlePics.add(new Slide(pictures.get(i), pictures.get(i+1)));
        }
//        List<Slide> singlePics = pictures.stream().map(Slide::new).collect(toList());

        ListMultimap<Slide, Node> multiMap = ArrayListMultimap.create();
        int i = 0;
        for (Slide slide1 : singlePics) {
            for (Slide slide2 : singlePics) {
                if(i++ % 100 == 0) {
                    System.out.println(i);
                }
                multiMap.put(slide1, new Node(slide2, ScoreComputer.computeScore(slide1, slide2)));
            }
        }

//        List<Slide> slides = new ArrayList<>();
//        slides.add(new Slide(pictures.get(0)));
//        slides.add(new Slide(pictures.get(3)));
//        slides.add(new Slide(pictures.get(1), pictures.get(2)));
//        long score = computeScore(slides);
//

        List<Slide> slides = new ArrayList<>();

        Comparator<Tuple> comparing = comparing(tuple -> tuple.node.score);
        Comparator<Node> comparing2 = comparing(node -> node.score);
        int bip = 0;
        final AtomicReference<Slide> first = new AtomicReference<>(findBest(singlePics, multiMap).get().slide);
        while (true) {

            List<Tuple> tuples = new ArrayList<>();
            multiMap.forEach((slide,node) -> {
                if(node.slide.equals(first.get())) {
                    tuples.add(new Tuple(slide, node));
                }
            });
            Optional<Tuple> first1 = tuples.stream().sorted(comparing.reversed()).findFirst();
            if (!first1.isPresent()) {
                break;
            }

            first.set(first1.get().slide);
            System.out.println(bip++);

            slides.add(first.get());

            multiMap.removeAll(first.get());
            multiMap.asMap().values().remove(first.get());
        }
        String result = slides.size() + "\n" + slides.stream().map(s -> s.picture1.id + " " + s.picture2).collect(joining("\n"));
          write(result);

//        System.out.println(first);
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
