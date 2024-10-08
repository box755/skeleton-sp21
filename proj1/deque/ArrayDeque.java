package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    private T[] items;
    private int size;

    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
    }

//    public ArrayDeque(ArrayDeque other){
//        this();
//        for(int i=0; i<other.size; i++) {
//            this.items[i] = (T)other.get(i);
//            size += 1;
//        }
//    }

    private void resize(int capacity){
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }

//    public T getLast(){
//        if(size == 0){
//            return null;
//        }
//        return this.items[size - 1];
//    }

    @Override
    public T get(int index){
        return this.items[index];
    }

    @Override
    public void addFirst(T item){
        if(size == items.length){
            T[] newArray = (T[]) new Object[size*2];
            System.arraycopy(items, 0, newArray, 1, items.length-1);
            newArray[0] = item;
            items = newArray;
            size++;
            return;
        }
        T[] newArray = (T[]) new Object[items.length];
        System.arraycopy(items, 0, newArray, 1, items.length-1);
        newArray[0] = item;
        items = newArray;
        size++;
    }

    // 如果新加入的元素讓陣列超過大小 resize 然後再加入最後一個元素
    @Override
    public void addLast(T item){
        if(size == items.length){
            resize(size * 2);
        }
        items[size] = item;
        size += 1;
    }

    //如果現現在陣列大小太大 resize成size大小 然後刪除元素再回傳
    @Override
    public T removeLast(){
        if(size == 0){
            return null;
        }
        if(size < items.length/4 && size > 4){
            resize(size);
        }
        T p = items[size - 1];
        items[size - 1] = null;
        size -= 1;
        return p;
    }

    //檢查陣列大小是否太大 然後複製一個新的陣列 把舊的除了第一個複製過去 再把最後一個設成null
    @Override
    public T removeFirst(){
        if(size == 0){
            return null;
        }
        if(size < items.length/4 && size > 4){
            resize(size);
        }
        T item = items[0];

        T[] newArray = (T[]) new Object[items.length];
        System.arraycopy(items, 1, newArray,0,  items.length - 1);
        items = newArray;
        items[size - 1] = null;
        size --;
        return item;
    }

    @Override
    public int size(){
        return this.size;
    }


    @Override
    public void printDeque(){
        int index = 0;
        while(items[index] != null){
            System.out.print(items[index] + " ");
            index ++;
        }

    }

    //iterator方法implement 因為ArrayDeque有immplement Iterable類別
    @Override
    public Iterator<T> iterator(){
        return new ArrayDequeIterator();
    }


    //ipmlement Iterator interface的hasNext和Next方法
    private class ArrayDequeIterator implements Iterator<T>{
        int curpos;
        ArrayDequeIterator(){
            curpos = 0;
        }
        @Override
        public boolean hasNext(){
            return curpos < size;
        }
        @Override
        public T next(){
            T curItem = items[curpos];
            curpos ++;
            return curItem;
        }

    }

    //覆寫equals方法
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof ArrayDeque)){
            return false;
        }
        ArrayDeque compared = (ArrayDeque)o;
        if(compared.size() != this.size){
            return false;
        }
        for(int i=0; i<this.size; i++){
            if(items[i] != compared.get(i)){
                return false;
            }
        }
        return true;

    }



}
