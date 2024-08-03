package deque;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArrayDequeTest {
    private ArrayDeque<Integer> deque;

    @Before
    public void setUp() {
        deque = new ArrayDeque<>();
    }

    @Test
    public void testAddFirst() {
        deque.addFirst(1);
        assertEquals(1, (int) deque.get(0));
        assertEquals(1, deque.size());
    }

    @Test
    public void testAddLast() {
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(1, (int) deque.get(0));
        assertEquals(2, (int) deque.get(1));
        assertEquals(2, deque.size());
    }

    @Test
    public void testRemoveFirst() {
        deque.addLast(1);
        deque.addLast(2);
        int removedItem = deque.removeFirst();
        assertEquals(1, removedItem);
        assertEquals(1, deque.size());
        assertEquals(2, (int) deque.get(0));
    }

    @Test
    public void testRemoveLast() {
        deque.addLast(1);
        deque.addLast(2);
        int removedItem = deque.removeLast();
        assertEquals(2, removedItem);
        assertEquals(1, deque.size());
        assertEquals(1, (int) deque.get(0));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(deque.isEmpty());
        deque.addLast(1);
        assertFalse(deque.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, deque.size());
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(2, deque.size());
    }

    @Test
    public void testGet() {
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(1, (int) deque.get(0));
        assertEquals(2, (int) deque.get(1));
    }

    @Test
    public void testResize() {
        for (int i = 0; i < 20; i++) {
            deque.addLast(i);
        }
        assertEquals(20, deque.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, (int) deque.get(i));
        }
    }

    @Test
    public void testIterator() {
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);

        int sum = 0;
        for (int i : deque) {
            sum += i;
        }
        assertEquals(6, sum);
    }

    @Test
    public void testEquals() {
        ArrayDeque<Integer> deque1 = new ArrayDeque<>();
        deque1.addLast(1);
        deque1.addLast(2);

        ArrayDeque<Integer> deque2 = new ArrayDeque<>();
        deque2.addLast(1);
        deque2.addLast(2);

        assertTrue(deque1.equals(deque2));

        deque2.addLast(3);
        assertFalse(deque1.equals(deque2));
    }

    @Test
    public void testCopyConstructor() {
        deque.addLast(1);
        deque.addLast(2);
        ArrayDeque<Integer> dequeCopy = new ArrayDeque<>(deque);

        assertEquals(deque.size(), dequeCopy.size());
        assertTrue(deque.equals(dequeCopy));
    }



}
