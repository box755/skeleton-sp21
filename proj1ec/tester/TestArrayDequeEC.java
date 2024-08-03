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

        StringBuilder sequence = new StringBuilder();

        int N = 200;

        for (int i = 0; i < N; i++) {
            int operationNum = StdRandom.uniform(0, 4);
            // addFirst
            if (operationNum == 0) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addFirst(number);
                buggyDeque.addFirst(number);
                sequence.append("addFirst(").append(number).append(")\n");
                assertEquals(sequence.toString(), correctDeque.size(), buggyDeque.size());
            }
            // addLast
            else if (operationNum == 1) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addLast(number);
                buggyDeque.addLast(number);
                sequence.append("addLast(").append(number).append(")\n");
                assertEquals(sequence.toString(), correctDeque.size(), buggyDeque.size());
            }
            // removeFirst
            else if (operationNum == 2) {
                if (correctDeque.isEmpty() || buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedCorrect = correctDeque.removeFirst();
                Integer removedBuggy = buggyDeque.removeFirst();
                sequence.append("removeFirst()\n");
                assertEquals(sequence.toString(), removedCorrect, removedBuggy);
            }
            // removeLast
            else if (operationNum == 3) {
                if (correctDeque.isEmpty() || buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedCorrect = correctDeque.removeLast();
                Integer removedBuggy = buggyDeque.removeLast();
                sequence.append("removeLast()\n");
                assertEquals(sequence.toString(), removedCorrect, removedBuggy);
            }
        }
    }
}