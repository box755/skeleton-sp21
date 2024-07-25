package deque;

import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {

    
    @Test
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        deque.ArrayDeque<Integer> al1 = new deque.ArrayDeque<Integer>();
        // should be empty
        al1.addLast(10);
        assertEquals(10, (int)al1.getLast() );
        al1.removeLast();
        assertEquals(null, al1.getLast() );
    }

    @Test
    public void removeEmptyTest() {
        deque.ArrayDeque<Integer> al1 = new deque.ArrayDeque<>();
        al1.addLast(3);

        al1.removeLast();
        al1.removeLast();
        al1.removeLast();
        al1.removeLast();

        int size = al1.size;

        assertEquals( 0, size);
        assertEquals(null, al1.getLast());
    }

    @Test
    public void multipleParamTest() {
        deque.ArrayDeque<String>  al1 = new deque.ArrayDeque<String>();
        deque.ArrayDeque<Double>  al2 = new deque.ArrayDeque<Double>();
        deque.ArrayDeque<Boolean> al3 = new deque.ArrayDeque<Boolean>();

        al1.addLast("string");
        al2.addLast(3.14159);
        al3.addLast(true);

        String s = al1.removeLast();
        double d = al2.removeLast();
        boolean b = al3.removeLast();
    }

    @Test
    /* check if null is return when removing from an empty deque.LinkedListDeque. */
    public void emptyNullReturnTest() {
        deque.ArrayDeque<String>  al1 = new deque.ArrayDeque<String>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, al1.removeLast());
        al1.addLast("test");
        assertEquals("Should return null when removeLast is called on an empty Deque,", "test", al1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        deque.ArrayDeque<String>  al1 = new deque.ArrayDeque<String>();
        for (int i = 0; i < 1000000; i++) {
            al1.addLast(Integer.toString(i));
            assertEquals("Should have the same value", Integer.toString(i), al1.removeLast());
        }


    }
}
