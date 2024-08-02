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
        StringBuilder failureSequence = new StringBuilder();

        for (int i = 0; i < N; i++) {
            int operationNum = StdRandom.uniform(0, 4);
            // addFirst
            if (operationNum == 0) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addFirst(number);
                buggyDeque.addFirst(number);
                failureSequence.append("addFirst(").append(number).append(")\n");
                Integer buggySize = buggyDeque.size();
                Integer correctSize = correctDeque.size();
                assertEquals("Failure sequence: " + failureSequence.toString(), correctSize, buggySize);
            }
            // addLast
            else if (operationNum == 1) {
                int number = StdRandom.uniform(0, 100);
                correctDeque.addLast(number);
                buggyDeque.addLast(number);
                failureSequence.append("addLast(").append(number).append(")\n");
                Integer buggySize = buggyDeque.size();
                Integer correctSize = correctDeque.size();
                assertEquals("Failure sequence: " + failureSequence.toString(), correctSize, buggySize);
            }
            // removeFirst
            else if (operationNum == 2) {
                if (correctDeque.isEmpty() || buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeFirst();
                Integer removeCorrect = correctDeque.removeFirst();
                failureSequence.append("removeFirst()\n");
                assertEquals("Failure sequence: " + failureSequence.toString(), removeCorrect, removedBuggy);
            }
            // removeLast
            else if (operationNum == 3) {
                if (correctDeque.isEmpty() || buggyDeque.isEmpty()) {
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeLast();
                Integer removeCorrect = correctDeque.removeLast();
                failureSequence.append("removeLast()\n");
                assertEquals("Failure sequence: " + failureSequence.toString(), removeCorrect, removedBuggy);
            }
        }
    }
}