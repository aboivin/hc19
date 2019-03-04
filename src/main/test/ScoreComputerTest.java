import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class ScoreComputerTest {

    @Test
    public void should_return_0_when_no_intersection() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "c"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "d", "e", "f"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_when_same() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "c"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "a", "b", "c"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_1() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "2", "a", "b"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "2", "b", "c"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(1);
    }

    @Test
    public void should_return_1_reversed() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "2", "a", "b"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "2", "c", "b"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(1);
    }

    @Test
    public void should_return_0_when_subset() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "c"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "2", "b", "c"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_when_subset2() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "2", "b", "c"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "b", "c", "a"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_when_match_reversed() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "c"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "c", "a", "b"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_3() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "c", "d", "e", "f"}));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "g", "c", "h", "a", "i", "b"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(3);
    }

    @Test
    public void should_return_0_only_match() throws Exception {
        // Given
        Slide slide1 = new Slide(new Picture(1, new String[]{"H", "1", "a", }));
        Slide slide2 = new Slide(new Picture(2, new String[]{"H", "3", "a", "c", "h", "a", "i", "b"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_only_match_reversed() throws Exception {
        // Given
        Slide slide2 = new Slide(new Picture(1, new String[]{"H", "3", "a", "c", "h", "a", "i", "b"}));
        Slide slide1 = new Slide(new Picture(2, new String[]{"H", "1", "a"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }


    @Test
    public void should_return_0_two_match() throws Exception {
        // Given
        Slide slide2 = new Slide(new Picture(1, new String[]{"H", "3", "a", "d", "c"}));
        Slide slide1 = new Slide(new Picture(2, new String[]{"H", "1", "a", "c"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_two_match_reversed() throws Exception {
        // Given
        Slide slide2 = new Slide(new Picture(1, new String[]{"H", "3", "a", "c"}));
        Slide slide1 = new Slide(new Picture(2, new String[]{"H", "1", "a", "d", "c"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }

    @Test
    public void should_return_0_cross_match() throws Exception {
        // Given
        Slide slide2 = new Slide(new Picture(1, new String[]{"H", "3", "a", "c", "b"}));
        Slide slide1 = new Slide(new Picture(2, new String[]{"H", "1", "a", "d", "b"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(1);
    }

    @Test
    public void should_return_0_same() throws Exception {
        // Given
        Slide slide2 = new Slide(new Picture(1, new String[]{"H", "3", "a", "b", "d"}));
        Slide slide1 = new Slide(new Picture(2, new String[]{"H", "1", "b", "a", "d"}));

        // When
        int i = ScoreComputer.computeScore(slide1, slide2);

        // Then
        assertThat(i).isEqualTo(0);
    }
}
