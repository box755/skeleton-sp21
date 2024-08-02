package deque;

import java.util.Comparator;

public class Dog implements Comparable<Dog>{

    int size;
    String name;
    public Dog(int s, String n){
        size = s;
        name = n;
    }

    @Override
    public int compareTo(Dog o) {
        return this.size - o.size;
    }

    //實作Dog類的comparator
    private static class nameComparator implements Comparator<Dog>{
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    public static Comparator<Dog> getNameComparator(){
        return new nameComparator();
    }

    private static class sizeComparator implements Comparator<Dog>{
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<Dog> getSizeComparator(){
        return new nameComparator();
    }

}
