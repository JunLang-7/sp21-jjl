package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void intMaxArrayDequeTest() {
        // lambda expression
        Comparator<Integer> cmpInt = (o1, o2) -> o1 - o2;
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
        Comparator<Double> cmpDouble = getDoubleComparator();
        MaxArrayDeque<Double> doubleMAD = new MaxArrayDeque<>(cmpDouble);
        for (int i = 0; i <= 100; i++){
            doubleMAD.addFirst(i / 2.0);
        }
        assertEquals(50.0, doubleMAD.max(), 1e-7);
        doubleMAD.addLast(100.0);
        doubleMAD.addLast(100.0);
        assertEquals(100.0, doubleMAD.max(), 1e-7);
    }

    @Test
    public void longMaxArrayDequeTest() {
        Comparator<Long> cmpLong = getLongComparator();
        MaxArrayDeque<Long> longMAD = new MaxArrayDeque<>(cmpLong);
        for (long i = 1000000; i <= 10000000; i++){
            longMAD.addLast(i);
        }
        assertEquals((long) 1e7, (long)longMAD.max());
    }

    @Test
    public void stringMaxArrayDequeTest() {
        Comparator<String> cmpString = getStringComparator();
        MaxArrayDeque<String> stringMaxArrayDeque = new MaxArrayDeque<>(cmpString);
        for (char i = 'a'; i <= 'z'; i++){
            stringMaxArrayDeque.addLast(String.valueOf(i));
        }
        assertEquals("z", stringMaxArrayDeque.max());
    }

    @Test
    public void MaxArrayDequeTestWithComparator() {
        Comparator<Integer> cmpInt = getIntComparator();
        Comparator<Integer> cmpIntAbs = getAbsIntComparator();
        MaxArrayDeque<Integer> intMAD = new MaxArrayDeque<>(cmpInt);
        for (int i = 0; i < 100; i++){
            intMAD.addLast(i * (int) Math.pow(-1, i));
        }
        assertEquals(-99, (int)intMAD.max(cmpIntAbs));
    }

    private static class IntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    public static Comparator<Integer> getIntComparator(){
        return new IntComparator();
    }

    private static class AbsIntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return Math.abs(o1 - o2);
        }
    }

    public static Comparator<Integer> getAbsIntComparator(){
        return new AbsIntComparator();
    }

    private static class DoubleComparator implements Comparator<Double>{
        @Override
        public int compare(Double o1, Double o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<Double> getDoubleComparator(){
        return new DoubleComparator();
    }

    private static class LongComparator implements Comparator<Long>{
        @Override
        public int compare(Long o1, Long o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<Long> getLongComparator(){
        return new LongComparator();
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<String> getStringComparator(){
        return new StringComparator();
    }
}
