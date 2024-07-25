package deque;

public class ArrayDeque<Item> {
    Item[] items;
    int size;

    public ArrayDeque(){
        items = (Item[]) new Object[8];
        size = 0;
    }

    public ArrayDeque(ArrayDeque other){
        this();
        for(int i=0; i<other.size; i++) {
            this.items[i] = (Item)other.get(i);
            size += 1;
        }
    }

    public void resize(int capacity){
        Item[] a = (Item[]) new Object[capacity];
        for(int i=0; i<size; i++){
            a[i] = items[i];
        }
        items = a;
    }

    public Item getLast(){
        if(size == 0){
            return null;
        }
        return this.items[size - 1];
    }

    public Item get(int index){
        return this.items[index];
    }

    public void addLast(Item item){
        if(size == items.length){
            resize(size * 2);
        }
        items[size] = item;
        size += 1;
    }

    public Item removeLast(){
        if(size == 0){
            return null;
        }
        if(size < items.length/4 && size > 4){
            resize(size);
        }
        Item p = items[size - 1];
        items[size - 1] = null;
        size -= 1;
        return p;
    }
}
