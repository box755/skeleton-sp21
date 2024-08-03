package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.ArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizeTest() {
        StudentArrayDeque<Integer> buggyDeque = new StudentArrayDeque<>();
        ArrayDeque<Integer> correctDeque = new ArrayDeque<>();

        int N = 200;

        for (int i = 0; i < N; i++) {
            int operationNum = StdRandom.uniform(0, 4);
            // addFirst
            if (operationNum == 0) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addFirst(number);
                buggyDeque.addFirst(number);
                assertEquals("addFirst(" + number + ")", correctDeque.size(), buggyDeque.size());
            }
            // addLast
            else if (operationNum == 1) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addLast(number);
                buggyDeque.addLast(number);
                assertEquals("addLast(" + number + ")", correctDeque.size(), buggyDeque.size());
            }
            // removeFirst
            else if (operationNum == 2) {
                if (correctDeque.isEmpty() || buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeFirst();
                Integer removedCorrect = correctDeque.removeFirst();
                assertEquals("removeFirst()", removedCorrect, removedBuggy);
            }
            // removeLast
            else if (operationNum == 3) {
                if (buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeLast();
                Integer removedCorrect = correctDeque.removeLast();
                assertEquals("removeLast()", removedCorrect, removedBuggy);
            }
        }
    }
}