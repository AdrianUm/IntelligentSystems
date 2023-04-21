import java.util.HashSet;
import java.util.Random;

import com.qqwing.Difficulty;
import com.qqwing.QQWing;

public class ExampleQQWing {

    //create sudoku of specific difficulty level
    public static int[] computePuzzleByDifficulty (Difficulty d) {
        QQWing qq = new QQWing();
        qq.setRecordHistory(true);
        qq.setLogHistory(false);
        boolean go_on = true;
        while (go_on) {
            qq.generatePuzzle();
            qq.solve();
            Difficulty actual_d = qq.getDifficulty();
            //System.out.println("Difficulty: "+actual_d.getName());
            go_on = !actual_d.equals(d);
        }
        int []puzzle = qq.getPuzzle();
        return puzzle;
    }

    //cheat by creating absurdly simple sudoku, with a given number of holes per row
    public static int[] computePuzzleWithNHolesPerRow(int numHolesPerRow) {
        Random rnd = new Random();
        QQWing qq = new QQWing();

        qq.setRecordHistory(true);
        qq.setLogHistory(false);
        qq.generatePuzzle();
        qq.solve();
        int []solution = qq.getSolution();
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i=0; i<9; i++) {
            set.clear();
            while(set.size()<numHolesPerRow) {
                int n = rnd.nextInt(9);
                if (set.contains(n)) continue;
                set.add(n);
            }
            for (Integer hole_idx : set) {
                solution[i*9+hole_idx] = 0;
            }
        }
        return solution;
    }
}