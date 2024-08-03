package deque;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    private LinkedListDeque<Integer> deque;

    @Before
    public void setUp() {
        deque = new LinkedListDeque<>();
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
    public void testGetRecursive() {
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(1, (int) deque.getRecursive(0));
        assertEquals(2, (int) deque.getRecursive(1));
    }

    @Test
    public void testEquals() {
        LinkedListDeque<Integer> deque1 = new LinkedListDeque<>();
        deque1.addLast(1);
        deque1.addLast(2);

        LinkedListDeque<Integer> deque2 = new LinkedListDeque<>();
        deque2.addLast(1);
        deque2.addLast(2);

        assertTrue(deque1.equals(deque2));

        deque2.addLast(3);
        assertFalse(deque1.equals(deque2));
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
    public void testCopyConstructor() {
        deque.addLast(1);
        deque.addLast(2);
        LinkedListDeque<Integer> dequeCopy = new LinkedListDeque<>(deque);

        assertEquals(deque.size(), dequeCopy.size());
        assertTrue(deque.equals(dequeCopy));
    }
}
