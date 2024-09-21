package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void SimpleTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(2);
        arrayDeque.addFirst(1);
        arrayDeque.addLast(3);
        arrayDeque.addLast(4);

        assertEquals(4, arrayDeque.size());
        assertEquals(1, (int)arrayDeque.get(0));
        assertEquals(4, (int)arrayDeque.get(arrayDeque.size() - 1));
        assertEquals(3, (int)arrayDeque.get(2));
        assertEquals(1, (int)arrayDeque.removeFirst());
        assertEquals(4, (int)arrayDeque.removeLast());
        assertEquals(2, arrayDeque.size());
    }


    @Test
    public void NoResizeTest(){
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        for (int i = 0; i < 4; i++){
            arr.addLast(i);
            arr.addFirst(i);
            lld.addLast(i);
            lld.addFirst(i);
        }

        for (int i = 0; i < 8; i++){
            assertEquals(arr.get(i), lld.get(i));
        }

        for (int i = 0; i < 4; i++){
            assertEquals(lld.removeLast(), arr.removeLast());
            assertEquals(lld.removeFirst(), arr.removeFirst());
        }
    }

    @Test
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3);

        ad1.removeLast();
        ad1.removeFirst();
        ad1.removeLast();
        ad1.removeFirst();

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);
    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        ArrayDeque<String> ad1 = new ArrayDeque<>();
        ArrayDeque<Double> ad2 = new ArrayDeque<>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());

    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad1.removeLast(), 0.0);
        }


    }

    @Test
    public void RandomizeTest(){
        ArrayDeque<Integer> arr = new ArrayDeque<>();
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        for (int i = 0; i < 1000; i++){
            int operaterNumber = StdRandom.uniform(0, 5);
            if (operaterNumber == 0){
                arr.addFirst(i);
                lld.addFirst(i);
            } else if (operaterNumber == 1){
                arr.addLast(i);
                lld.addLast(i);
            } else if (operaterNumber == 2){
                assertEquals(arr.removeFirst(), lld.removeFirst());
            } else if (operaterNumber == 3){
                assertEquals(arr.removeLast(), lld.removeLast());
            } else if (operaterNumber == 4){
                assertEquals(arr.get(i), arr.get(i));
            }
        }
    }

    @Test
    public void iteratorTest(){
        ArrayDeque<Integer> arr = new ArrayDeque<>();

        for (int i = 0; i < 100; i++){
            arr.addLast(i);
        }

        int count = 0;

        for (int ele : arr){
            assertEquals(count, ele);
            count++;
        }
    }

    @Test
    public void equalsTest1(){
        ArrayDeque<Integer> arr1 = new ArrayDeque<>();
        ArrayDeque<Integer> arr2 = new ArrayDeque<>();

        for (int i = 0; i < 100; i++){
            arr1.addLast(i);
            arr2.addLast(i);
        }

        assertTrue(arr1.equals(arr2));
    }

    @Test
    public void equalsTest2(){
        ArrayDeque<Integer> arr1 = new ArrayDeque<>();
        ArrayDeque<Integer> arr2 = arr1;

        assertTrue(arr1.equals(arr2));

        for (int i = 0; i < 100; i++){
            arr1.addLast(i);
        }

        assertTrue(arr1.equals(arr2));
    }

    @Test
    public void equalsTest3(){
        ArrayDeque<Integer> arr1 = new ArrayDeque<>();
        ArrayDeque<Integer> arr2 = new ArrayDeque<>();
        arr1.addFirst(1);
        for (int i = 0; i < 100; i++){
            arr1.addLast(i);
            arr2.addLast(i);
        }
        assertFalse(arr1.equals(arr2));
    }

    @Test
    public void equalsTest4(){
        ArrayDeque<Integer> arr1 = new ArrayDeque<>();
        ArrayDeque<Integer> arr2 = null;
        for (int i = 0; i < 100; i++){
            arr1.addLast(i);
        }
        assertFalse(arr1.equals(arr2));
    }
}
