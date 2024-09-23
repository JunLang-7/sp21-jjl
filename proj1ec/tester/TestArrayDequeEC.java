package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizeTest() {
        StudentArrayDeque<Integer> sd = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> sol = new ArrayDequeSolution<>();
        String msg = "";

        for (int i = 0; i < 5000; i++) {
            int operator = StdRandom.uniform(0, 4);

            if (operator == 0) {
                sd.addFirst(i);
                sol.addFirst(i);
                msg += "addFirst(" + i + ")\n";
                assertEquals(msg, sol.get(0), sd.get(0));
            }else if (operator == 1) {
                sd.addLast(i);
                sol.addLast(i);
                msg += "addLast(" + i + ")\n";
                assertEquals(msg, sol.get(sol.size() - 1), sd.get(sd.size() - 1));
            }else if (operator == 2) {
                if (sd.isEmpty() || sol.isEmpty()) {
                    return;
                }
                Integer sdValue = sd.removeFirst();
                Integer solValue = sol.removeFirst();
                msg += "removeFirst()\n";
                assertEquals(msg, sdValue, solValue);
            }else if (operator == 3) {
                if (sd.isEmpty() || sol.isEmpty()) {
                    return;
                }
                Integer sdValue = sd.removeLast();
                Integer solValue = sol.removeLast();
                msg += "removeLast()\n";
                assertEquals(msg, sdValue, solValue);
            }
        }

    }
}
