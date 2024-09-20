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
        Comparator<Double> cmpDouble = MaxArrayDeque.getDoubleComparator();
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
        Comparator<Long> cmpLong = MaxArrayDeque.getLongComparator();
        MaxArrayDeque<Long> longMAD = new MaxArrayDeque<>(cmpLong);
        for (long i = 1000000; i <= 10000000; i++){
            longMAD.addLast(i);
        }
        assertEquals((long) 1e7, (long)longMAD.max());
    }

    @Test
    public void stringMaxArrayDequeTest() {
        Comparator<String> cmpString = MaxArrayDeque.getStringComparator();
        MaxArrayDeque<String> stringMaxArrayDeque = new MaxArrayDeque<>(cmpString);
        for (char i = 'a'; i <= 'z'; i++){
            stringMaxArrayDeque.addLast(String.valueOf(i));
        }
        assertEquals("z", stringMaxArrayDeque.max());
    }

    @Test
    public void MaxArrayDequeTestWithComparator() {
        Comparator<Integer> cmpInt = MaxArrayDeque.getIntComparator();
        Comparator<Integer> cmpIntAbs = MaxArrayDeque.getAbsIntComparator();
        MaxArrayDeque<Integer> intMAD = new MaxArrayDeque<>(cmpInt);
        for (int i = 0; i < 100; i++){
            intMAD.addLast(i * (int) Math.pow(-1, i));
        }
        assertEquals(-99, (int)intMAD.max(cmpIntAbs));
    }
}
