package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{

    private class IntNode{
        public T item;
        public IntNode prv;
        public IntNode next;
        public IntNode(T item, IntNode prv, IntNode next){
            this.item = item;
            this.prv = prv;
            this.next = next;
        }
    }

    private IntNode sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new IntNode(null, null ,null);//不能sentinel = new IntNode(item, sentinel, sentinel)因為sentinel的位置還沒初始化
        sentinel.next = sentinel;
        sentinel.prv = sentinel;
        this.size = 0;
    }

//    public LinkedListDeque(LinkedListDeque<T> other){
//        this();
//        int current = 0;
//        while(current < other.size()){
//            this.addLast(other.get(current));
//            current += 1;
//        }
//    }
//
//    public LinkedListDeque(T item){
//        this();
//        addLast(item);
//    }

    @Override
    public void addFirst(T item){
        sentinel.next = new IntNode(item, sentinel, sentinel.next);
        sentinel.next.next.prv = sentinel.next;
        this.size += 1;
    }

    @Override
    public void addLast(T item){
        sentinel.prv.next = new IntNode(item, sentinel.prv, sentinel);
        sentinel.prv = sentinel.prv.next;
        this.size += 1;
    }

    @Override
    public int size(){
        return this.size;
    }

    @Override
    public void printDeque(){
        IntNode p = sentinel.next;
        while( p !=  sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst(){
        if(sentinel.next == sentinel){
            return null;
        }
        T p = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prv = sentinel;
        size -= 1;
        return p;
    }

    @Override
    public T removeLast(){
        if(sentinel.next == sentinel){//檢查list是否為空
            return null;
        }
        T p = sentinel.prv.item;
        sentinel.prv = sentinel.prv.prv;
        sentinel.prv.next = sentinel;
        size -= 1;
        return p;
    }

    @Override
    public T get(int index){
        IntNode p = sentinel.next;
        int current = 0;
        while(current < index){
            if(p.item == null){
                return null;
            }
            p = p.next;
            current += 1;
        }
        return p.item;
    }
    //getRecursive必須要helper method
    public T getRecursive(int index){
        if(index < size && index >= 0){
            return getRecursiveHelper(index, sentinel.next);
        }
        return null;
    }

    private T getRecursiveHelper(int index, IntNode p){
        if(index == 0){
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }


    public Iterator<T> iterator(){
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        int curpos;
        LinkedListDequeIterator(){
            curpos = 0;
        }

        @Override
        public boolean hasNext() {
            if(curpos >= size){
                return false;
            }
            return true;
        }

        @Override
        public T next(){
            T curItem = get(curpos);
            curpos ++;
            return curItem;
        }

    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof LinkedListDeque)){
            return false;
        }

        LinkedListDeque<T> compared = (LinkedListDeque<T>)o;

        if(compared.size() != this.size){
            return false;
        }

        for(int i=0; i<this.size; i++){
            if(get(i) != compared.get(i)){
                return false;
            }
        }
        return true;
    }




    
}
