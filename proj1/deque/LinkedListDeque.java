package deque;

public class LinkedListDeque<Item> {

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

    public void addFirst(Item item){
        sentinel.next = new IntNode(item, sentinel, sentinel.next);
        sentinel.next.next.prv = sentinel.next;
        this.size += 1;
    }

    public void addLast(Item item){
        sentinel.prv.next = new IntNode(item, sentinel.prv, sentinel);
        sentinel.prv = sentinel.prv.next;
        this.size += 1;
    }

    public boolean isEmpty(){
        if(this.size == 0){
            return true;
        }
        return false;
    }

    public int size(){
        return this.size;
    }

    public void printDeque(){
        IntNode p = sentinel.next;
        while( p !=  sentinel){
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

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

    public Item get(int index){
        IntNode p = sentinel.next;
        int current = 0;
        while(current < index){
            if(p == null){
                return null;
            }
            p = p.next;
            current += 1;
        }
        return p.item;
    }





    
}
