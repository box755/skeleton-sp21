package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {
    @Test
    public void dogTest(){
        Dog dog1 = new Dog(10, "adog");
        Dog dog2 = new Dog(20, "bdog");
        Dog dog3 = new Dog(30, "cdog");

        Comparator<Dog> nameComparator = Dog.getNameComparator();
        Comparator<Dog> sizeComparator = Dog.getSizeComparator();
        MaxArrayDeque<Dog> arrayDeque = new MaxArrayDeque<>(sizeComparator);
        arrayDeque.addFirst(dog1);
        arrayDeque.addFirst(dog2);
        arrayDeque.addFirst(dog3);
        Dog expect = dog3;
        Dog actual = arrayDeque.max(nameComparator);
        assertEquals(expect, actual);

        Dog Sizeexpect = dog3;
        Dog Sizeactual = arrayDeque.max();
        assertEquals(Sizeexpect, Sizeactual);



    }
}
