package deque;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/** Performs some basic linked list tests. */
public class LinkedListDequeTest {

    @Test
    public void testAddFirst() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(1);
        assertEquals(1, (int) deque.get(0));
        deque.addFirst(2);
        assertEquals(2, (int) deque.get(0));
    }

    @Test
    public void testAddLast() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        assertEquals(1, (int) deque.get(0));
        deque.addLast(2);
        assertEquals(2, (int) deque.get(1));
    }

    @Test
    public void testRemoveFirst() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        assertEquals(2, (int) deque.removeFirst());
        assertEquals(1, (int) deque.removeFirst());
        assertNull(deque.removeFirst());
    }

    @Test
    public void testRemoveLast() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        assertEquals(2, (int) deque.removeLast());
        assertEquals(1, (int) deque.removeLast());
        assertNull(deque.removeLast());
    }

    @Test
    public void testSize() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        assertEquals(0, deque.size());
        deque.addFirst(1);
        assertEquals(1, deque.size());
        deque.addLast(2);
        assertEquals(2, deque.size());
    }

    @Test
    public void testGet() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        deque.addLast(1);
        deque.addLast(2);
        deque.addLast(3);
        assertEquals(1, (int) deque.get(0));
        assertEquals(2, (int) deque.get(1));
        assertEquals(3, (int) deque.get(2));
        assertNull(deque.get(3));
    }

    @Test
    public void testEquals() {
        LinkedListDeque<Integer> deque1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> deque2 = new LinkedListDeque<>();
        assertEquals(deque1, deque2);
        deque1.addLast(1);
        deque2.addLast(1);
        assertEquals(deque1, deque2);
        deque1.addLast(2);
        assertNotEquals(deque1, deque2);
        deque2.addLast(2);
        assertEquals(deque1, deque2);
    }


}
