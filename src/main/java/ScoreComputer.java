import static java.lang.Math.min;

public class ScoreComputer {

    public static int computeScore(Slide slideLeft, Slide slideRight) {
        int intersection = 0, diffLeft = 0, diffRight = 0;
        int iLeft = 0, iRight = 0;
        int leftSize = slideLeft.size();
        int rightSize = slideRight.size();
        while (!isDone(iLeft, leftSize) || !isDone(iRight, rightSize)) {
            if (!isDone(iLeft, leftSize) && (isDone(iRight, rightSize) ||
                    slideLeft.get(iLeft) < slideRight.get(iRight))) {
                diffLeft++;
                iLeft++;
            } else if (!isDone(iRight, rightSize) && (isDone(iLeft, leftSize)
                    || slideRight.get(iRight) < slideLeft.get(iLeft))) {
                diffRight++;
                iRight++;
            } else if (slideRight.get(iRight) == slideLeft.get(iLeft)) {
                intersection++;
                if (!isDone(iLeft, leftSize)) {
                    iLeft++;
                }
                if (!isDone(iRight, rightSize)) {
                    iRight++;
                }
            }
        }
//        System.out.println("intersection: " + intersection);
//        System.out.println("diffLeft: " + diffLeft);
//        System.out.println("diffRight: " + diffRight);
        return min(min(intersection, diffLeft), diffRight);
    }

    private static boolean isDone(int i, int size) {
        return i == size;
    }

}
