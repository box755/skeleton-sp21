package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Deque<Item>, Iterable<Item>{

    private class IntNode{
        public Item item;
        public IntNode prv;
        public IntNode next;
        public IntNode(Item item, IntNode prv, IntNode next){
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

    public LinkedListDeque(LinkedListDeque other){
        this();
        int current = 0;
        while(current < other.size()){
            this.addLast((Item)other.get(current));//這裡要轉型，因為我們給的參數other的型別是LinkedListDeque，但這個型別並沒有泛型，所以我們要給它。
            current += 1;
            size += 1;
        }
    }

    public LinkedListDeque(Item item){
        this();
        addLast(item);
    }

    @Override
    public void addFirst(Item item){
        sentinel.next = new IntNode(item, sentinel, sentinel.next);
        sentinel.next.next.prv = sentinel.next;
        this.size += 1;
    }

    @Override
    public void addLast(Item item){
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
    public Item removeFirst(){
        if(sentinel.next == sentinel){
            return null;
        }
        Item p = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prv = sentinel;
        size -= 1;
        return p;
    }

    @Override
    public Item removeLast(){
        if(sentinel.next == sentinel){//檢查list是否為空
            return null;
        }
        Item p = sentinel.prv.item;
        sentinel.prv = sentinel.prv.prv;
        sentinel.prv.next = sentinel;
        size -= 1;
        return p;
    }

    @Override
    public Item get(int index){
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
    private Item getRecursive(int index){
        if(index < size && index >= 0){
            return getRecursiveHelper(index, sentinel.next);
        }
        return null;
    }

    public Item getRecursiveHelper(int index, IntNode p){
        if(index == 0){
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }


    public Iterator<Item> iterator(){
        return null;
    }

    private class LinkedListDequeIterator implements Iterator<Item>{
        int curpos;
        LinkedListDequeIterator(){
            curpos = 0;
        }

        @Override
        public boolean hasNext() {
            if(curpos > size){
                return false;
            }
            return true;
        }

        @Override
        public Item next(){
            Item curItem = get(curpos);
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

        LinkedListDeque compared = (LinkedListDeque)o;

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
