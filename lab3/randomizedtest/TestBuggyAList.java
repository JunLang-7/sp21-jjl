package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void randomizedTest() {
      AListNoResizing<Integer> correct = new AListNoResizing<>();
      BuggyAList<Integer> broken = new BuggyAList<>();

      int N = 5000;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 4);
          if (operationNumber == 0) {
              // addLast
              int randVal = StdRandom.uniform(0, 100);
              correct.addLast(randVal);
              broken.addLast(randVal);
              // System.out.println("addLast(" + randVal + ")");
          }else if (operationNumber == 1) {
              int correctSize = correct.size();
              int brokenSize = broken.size();
              // System.out.println("size: " + correctSize + " " + brokenSize);
              assertTrue(correctSize == brokenSize);
          } else if (operationNumber == 2) {
              // getLast
              if (correct.size() <= 0 || broken.size() <= 0) {
                  continue;
              }
              int correctVal = correct.getLast();
              int brokenVal = broken.getLast();
              // System.out.println("getLast:" + correctVal + " " + brokenVal);
              assertEquals(correctVal, brokenVal);
          } else{
              if (correct.size() <= 0 || broken.size() <= 0) {
                  continue;
              }
              int correctVal = correct.removeLast();
              int brokenVal = broken.removeLast();
              // System.out.println("removeLast:" + correctVal + " " + brokenVal);
              assertEquals(correctVal, brokenVal);
          }
      }
    }



}
