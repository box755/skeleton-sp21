package tester;
import static org.junit.Assert.*;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.ArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void randomizeTest(){
        StudentArrayDeque<Integer> buggyDeque = new StudentArrayDeque<>();
        ArrayDeque<Integer> correctDeque = new ArrayDeque<>();

        int N = 100;

        for(int i=0; i<N; i++){
            int operationNum = StdRandom.uniform(0, 4);
            //addFirst
            if(operationNum == 0){
                int number = StdRandom.uniform(0, 100);
                correctDeque.addFirst(number);
                buggyDeque.addFirst((number));
                System.out.println("addFirst(" + number + ")");
                Integer buggySize = buggyDeque.size();
                Integer correctSize = correctDeque.size();
                assertEquals("The size after removeFirst should be " + correctSize + " rather than " + buggySize ,buggySize, correctSize);
            }
            //addLast
            else if(operationNum == 1){
                int number = StdRandom.uniform(0, 100);
                correctDeque.addLast(number);
                buggyDeque.addLast((number));
                System.out.println("addLast(" + number + ")");
                Integer buggySize = buggyDeque.size();
                Integer correctSize = correctDeque.size();
                assertEquals("The size after removeLast should be " + correctSize + " rather than " + buggySize ,buggySize, correctSize);
            }
            //remove First
            else if(operationNum == 2){
                if(correctDeque.isEmpty() || buggyDeque.isEmpty()){
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeFirst();
                Integer removeCorrect = correctDeque.removeFirst();
                assertEquals(removedBuggy, removeCorrect);
                System.out.println("removeFirst() " + removeCorrect);
            }
            //remove Last
            else if(operationNum == 3){
                if(correctDeque.isEmpty() || buggyDeque.isEmpty()){
                    continue;
                }
                Integer removedBuggy = buggyDeque.removeLast();
                Integer removeCorrect = correctDeque.removeLast();
                assertEquals(removedBuggy, removeCorrect);
                System.out.println("removeLast() " + removeCorrect);

            }
        }


    }
}
