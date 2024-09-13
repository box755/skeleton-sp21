package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    private double loadFactor;
    //key-value pairs.
    private int size;

    private int arraySize;
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }

    }

    private void resize(int bucketsSize){
        MyHashMap<K, V> temp = new MyHashMap<>(bucketsSize);
        for(K key : this){
            temp.put(key, this.get(key));
        }
        //在一個類中實力化的同一個類，可以存取Private成員
        this.buckets = temp.buckets;
        this.arraySize = bucketsSize;
    }

    public void clear(){
        buckets = createTable(arraySize);
        size = 0;
    }

    public boolean containsKey(K key){
        int keyHash = Math.floorMod(key.hashCode(), arraySize);
        for(Node node : buckets[keyHash]){
            if(node.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    public V get(K key){
        int keyHash = Math.floorMod(key.hashCode(), arraySize);
        for(Node node : buckets[keyHash]){
            if(node.key.equals(key)){
                return node.value;
            }
        }
        return null;
    }

    public int size(){
        return this.size;
    }

    public void put(K key, V value){
        if( (this.size + 1) / (arraySize*1.0) > loadFactor){
            resize(this.arraySize*2);
        }
        int keyHash = Math.floorMod(key.hashCode(), arraySize);
        for(Node node : buckets[keyHash]){
            if(node.key.equals(key)){
                node.value = value;
                return;
            }
        }
        size++;
        buckets[keyHash].add(createNode(key, value));
    }

    public V remove(K key){
        int keyHash = Math.floorMod(key.hashCode(), arraySize);
        V removedValue = get(key);
        //也可以在Node類別中，加入equals方法，再用buckets裡的物件的remove方法（會比對每個物件是否相同後刪除）。
        Iterator<Node> iterator = buckets[keyHash].iterator();
        while(iterator.hasNext()){
            Node node = iterator.next();
            if(node.key.equals(key)){
                iterator.remove();
                //iterator<> default void remove()會把當前指向的節點刪除
            }
        }
        size--;
        return removedValue;
    }


    public V remove(K key, V value){
        int keyHash = Math.floorMod(key.hashCode(), arraySize);
        V removedValue = get(key);
        if(removedValue == value){
            Iterator<Node> iterator = buckets[keyHash].iterator();
            while(iterator.hasNext()){
                Node node = iterator.next();
                if(node.key.equals(key)){
                    iterator.remove();
                    //iterator<> default void remove()會把當前指向的節點刪除
                }
            }
            return removedValue;
        }
        else{
            return null;
        }
    }

    public Iterator<K> iterator(){
        return new keyIterator();
    }


    private class keyIterator implements Iterator<K>{
        Stack<K> keys;
        public keyIterator(){
            keys = new Stack<>();
            for(Collection<Node> chain : buckets){
                for(Node node : chain){
                    keys.push(node.key);
                }
            }
        }

        public boolean hasNext(){
            return !keys.isEmpty();
        }

        public K next(){
            return keys.pop();
        }

    }

    public Set<K> keySet(){
        Set<K> keySet = new HashSet<>();
        for(K key : this){
            keySet.add(key);
        }
        return keySet;
    }


    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        size = 0;
        arraySize = 16;
        loadFactor = 0.75;
        buckets = createTable(arraySize);

    }

    public MyHashMap(int initialSize) {
        size = 0;
        arraySize = initialSize;
        loadFactor = 0.75;
        buckets = createTable(arraySize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        arraySize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(arraySize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }
    //Skip.

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        //這樣的collection可以儲存任何collection的範型子類
        for(int i = 0; i < arraySize; i++){
            table[i] = createBucket();
        }
        return table;
    }
    //Skip.

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

}
