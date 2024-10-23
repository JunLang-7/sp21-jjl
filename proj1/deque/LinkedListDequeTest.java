package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

		assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
		lld1.addFirst("front");

		// The && operator is the same as "and" in Python.
		// It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

		lld1.addLast("middle");
		assertEquals(2, lld1.size());

		lld1.addLast("back");
		assertEquals(3, lld1.size());

		System.out.println("Printing out deque: ");
		lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
		// should be empty
		assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

		lld1.addFirst(10);
		// should not be empty
		assertFalse("lld1 should contain 1 item", lld1.isEmpty());

		lld1.removeFirst();
		// should be empty
		assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String>  lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double>  lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

//        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    public void getTest(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        for (int i = 0; i < 1000; i++) {
            lld1.addLast(i);
            lld2.addLast(i);
        }
        for (int i = 0; i < 1000; i++){
            int item1 = lld1.get(i);
            int item2 = lld2.getRecursive(i);
            assertEquals(item1, item2);
        }
        assertEquals(null, lld1.get(10001));
        assertEquals(null, lld2.getRecursive(10001));
    }

    @Test
    public void iteratorTest1() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }

        int count = 0;
        for (int ele : lld1) {
            assertEquals(count, ele);
            count++;
        }
    }

    @Test
    public void iteratorTestHasNext() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }
        for (int i = 0; i < 99; i++) {
            assertTrue(lld1.iterator().hasNext());
            lld1.iterator().next();
        }
    }

    @Test
    public void iteratorTestNext() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }
        int count = 0;
        Iterator<Integer> iter = lld1.iterator();
        while (iter.hasNext()) {
            assertEquals((Integer) count, iter.next());
            count++;
        }
    }

    @Test
    public void iteratorTestNextTimes() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }
        int count = 0;
        Iterator<Integer> iter = lld1.iterator();
        while (iter.hasNext()) {
            assertEquals((Integer) count, iter.next());
            count++;
            assertEquals((Integer) count, iter.next());
            count++;
            assertEquals((Integer) count, iter.next());
            count++;
            assertEquals((Integer) count, iter.next());
            count++;
        }
    }

    @Test
    public void iteratorTestIterable() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }
        int count = 0;
        for (Integer ele : lld1) {
            assertEquals((Integer) count, ele);
            count++;
        }
    }

    @Test
    public void equalsTest1(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
            lld2.addLast(i);
        }

        assertTrue(lld1.equals(lld2));

    }

    @Test
    public void equalsTest2(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        lld1.addLast(1);
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
            lld2.addLast(i);
        }
        assertFalse(lld1.equals(lld2));
    }

    @Test
    public void equalsTest3(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = null;

        assertFalse(lld1.equals(lld2));
    }

    @Test
    public void equalsTest4(){
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = lld1;
        assertTrue(lld1.equals(lld2));
        for (int i = 0; i < 100; i++) {
            lld1.addLast(i);
        }
        assertTrue(lld1.equals(lld2));
    }

    @Test
    public void equalsTest5(){
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        assertTrue(lld.equals(ad));
        for (int i = 0; i < 100; i++) {
            lld.addLast(i);
            ad.addLast(i);
        }
        assertTrue(lld.equals(ad));
        assertTrue(lld.equals(lld));
    }
}
