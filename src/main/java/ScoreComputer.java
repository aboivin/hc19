import static java.lang.Math.min;

public class ScoreComputer {

    public static long computeScore(Slide slide1, Slide slide2) {
        int intersection = 0, diff1 = 0, diff2 = 0;
        int index1 = 0, index2 = 0;
        Long left, right;
        boolean doneLeft = false, doneRight = false;
        while(!doneLeft || !doneRight) {
            left = slide1.keywords.get(index1);
            right = slide2.keywords.get(index2);
            if(left < right) {
                diff1++;
                if(index1 + 1 < slide1.keywords.size()) {
                    index1++;
                } else {
                    doneLeft = true;
                    if(index2 + 1 < slide2.keywords.size()) {
                        index2++;
                    } else {
                        diff2++;
                        break;
                    }
                }
            } else if (right < left) {
                diff2++;
                if(index2 + 1 < slide2.keywords.size()) {
                    index2++;
                } else {
                    doneRight = true;
                    if(index1 + 1 < slide1.keywords.size()) {
                        index1++;
                    } else {
                        diff1++;
                        break;
                    }
                }
            } else {
                intersection++;
                if(index1 + 1 < slide1.keywords.size()) {
                    index1++;
                } else {
                    doneLeft = true;
                }
                if(index2 + 1 < slide2.keywords.size()) {
                    index2++;
                } else {
                    doneRight = true;
                }
            }
        }
        return min(min(intersection, diff1), diff2);
    }

}
