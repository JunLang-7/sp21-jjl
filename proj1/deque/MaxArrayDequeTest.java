package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void intMaxArrayDequeTest() {
        Comparator<Integer> cmpInt = new Comparator<Integer>(){
          @Override
            public int compare(Integer o1, Integer o2) {
              if (o1 < o2){
                  return -1;
              }
              if (o1 > o2){
                  return 1;
              }
              return 0;
          }
        };
        MaxArrayDeque<Integer> intMAD = new MaxArrayDeque<>(cmpInt);
        for (int i = 0; i < 100; i++){
            intMAD.addLast(i);
        }
        assertEquals(99, (int)intMAD.max());

        intMAD.addFirst(100);
        intMAD.addLast(100);
        assertEquals(100, (int)intMAD.max());
    }

    @Test
    public void doubleMaxArrayDequeTest() {
        Comparator<Double> cmpDouble = new Comparator<Double>(){
          @Override
          public int compare(Double o1, Double o2) {
              if (o1 < o2){
                  return -1;
              }
              if (o1 > o2){
                  return 1;
              }
              return 0;
          }
        };
        MaxArrayDeque<Double> doubleMAD = new MaxArrayDeque<>(cmpDouble);
        for (int i = 0; i <= 100; i++){
            doubleMAD.addFirst(i / 2.0);
        }
        assertEquals((double) 50.0, (double)doubleMAD.max(), 1e-7);
        doubleMAD.addLast(100.0);
        doubleMAD.addLast(100.0);
        assertEquals((double) 100.0, (double) doubleMAD.max(), 1e-7);
    }

    @Test
    public void longMaxArrayDequeTest() {
        Comparator<Long> cmpLong = new Comparator<Long>(){
            @Override
            public int compare(Long o1, Long o2) {
                if (o1 < o2){
                    return -1;
                }
                if (o1 > o2){
                    return 1;
                }
                return 0;
            }
        };
        MaxArrayDeque<Long> longMAD = new MaxArrayDeque<>(cmpLong);
        for (long i = 1000000; i <= 10000000; i++){
            longMAD.addLast(i);
        }
        assertEquals((long) 1e7, (long)longMAD.max());
    }

    @Test
    public void stringMaxArrayDequeTest() {
        Comparator<String> cmpString = new Comparator<String>(){
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        MaxArrayDeque<String> stringMaxArrayDeque = new MaxArrayDeque<>(cmpString);
        for (char i = 'a'; i <= 'z'; i++){
            stringMaxArrayDeque.addLast(String.valueOf(i));
        }
        assertEquals("z", stringMaxArrayDeque.max());
    }

    @Test
    public void MaxArrayDequeTestWithComparator() {
        Comparator<Integer> cmpInt = new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        Comparator<Integer> cmpIntAbs = new Comparator<Integer>(){
            @Override
            public int compare(Integer o1, Integer o2) {
                return Math.abs(o1 - o2);
            }
        };
        MaxArrayDeque<Integer> intMAD = new MaxArrayDeque<>(cmpInt);
        for (int i = 0; i < 100; i++){
            intMAD.addLast(i * (int) Math.pow(-1, i));
        }
        assertEquals(-99, (int)intMAD.max(cmpIntAbs));
    }
}
