package bstmap;
import edu.princeton.cs.algs4.BST;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>, Iterable<K>{
//內部類（BSTNode）用private外部類還是可以存取，JAVA的規定。
    private class BSTNode{
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;
        public BSTNode(K key, V value, int size){
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    private BSTNode root;

    public int size(){
        return size(root);
    }

    private int size(BSTNode node){
        if(node == null){
            return 0;
        }
        return node.size;
    }

   public void clear(){
        root = null;
    }

    //不能->用node的get方法找node物件，如果找到就會回傳true。因為如果key對應到的value是null，會回傳null。
    public boolean containsKey(K key) {
        if(root == null){
            return false;
        }
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode node, K key){
        if(node == null){
            return false;
        }
        int com = key.compareTo(node.key);
        if(com > 0){
            return containsKey(node.right, key);
        }
        else if(com < 0){
            return containsKey(node.left, key);
        }
        return true;
    }

    public V get(K key){
        if(key == null){
            throw new IllegalArgumentException("Get() call's key can't be null!!!!!!");
        }
        return get(root, key);
    }

    private V get(BSTNode node, K key){
        if(node == null){
            return null;
        }
        int com = key.compareTo(node.key);
        if(com > 0){
            return get(node.right, key);
        }
        else if(com < 0){
            return get(node.left, key);
        }
        else{
            return node.value;
        }

    }

    public void put(K key, V value){
        if(key == null){
            throw new IllegalArgumentException("Put() call's key can't be null!!!!!!");
        }
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode node, K key, V value){
        if(node == null){
            return new BSTNode(key, value, 1);
        }
        int com = key.compareTo(node.key);
        if(com > 0){
            node.right = put(node.right , key, value);
        }
        else if(com < 0){
            node.left = put(node.left , key, value);
        }
        else{
            node.value = value;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    //用遞迴方式，照key的由小到大的順序，把keys加到指定的set中。
    private void addToSetInOrder(BSTNode node, Set<K> set){
        if(node == null){
            return;
        }
        addToSetInOrder(node.left, set);
        set.add(node.key);
        addToSetInOrder(node.right, set);
    }
    //呼叫遞迴函式，把從root節點開始的所有key加到set中，再回傳。
    public Set<K> keySet(){
        Set<K> keySet = new HashSet<>();
        addToSetInOrder(root, keySet);
        return keySet;
    }

    public V remove(K key){
        if(key == null){
            throw new IllegalArgumentException("Remove() call's key can't be null!!!!!!");
        }
        V valueRemoved = get(key);
        if(valueRemoved == null){
            return null;
        }
        delete(key);
        return valueRemoved;
    }

    public V remove(K key, V value){
        if(key == null && value == null){
            throw new IllegalArgumentException("Remove() call's key or value can't be null!!!!!!");
        }
        V valueRemoved = get(key);
        if(valueRemoved == null){
            return null;
        }
        if(!valueRemoved.equals(value)){
            return null;
        }
        delete(key);
        return valueRemoved;
    }

    public Iterator<K> iterator() {
        return new keyIterator();
    }

    private class keyIterator implements Iterator<K>{
        private Stack<K> keys;
        public keyIterator(){
            keys = new Stack<>();
            pushKeys(root, keys);
        }

        private void pushKeys(BSTNode node, Stack<K> keys){
            if(node == null){
                return;
            }
            pushKeys(node.left, keys);
            keys.push(node.key);
            pushKeys(node.right, keys);
        }

        public boolean hasNext() {
            if(keys.isEmpty()){
                return false;
            }
            return true;
        }

        public K next(){
            K key = keys.pop();
            return key;
        }
    }

    private void delete(K key){
        if(key == null){
            throw new IllegalArgumentException("Delete() call's key can't be null!!!!!!");
        }
        root = delete(root, key);
    }


    private BSTNode delete(BSTNode node, K key){
        if (node == null) {
            return null; // 如果節點為 null，直接返回 null
        }
        int com = key.compareTo(node.key);
        //如果key比當前節點key大
        if(com > 0){
            node.right = delete(node.right, key);
        }
        else if(com < 0){
            node.left = delete(node.left, key);
        }
        //當找到key時
        else{
            //如果有一個子類的狀況
            if(node.left == null){
                return node.right;
            }
            if(node.right == null){
                return node.left;
            }
            //如果有兩個子類
            //找到繼承節點（右邊的最靠左節點）
            BSTNode suc = getSuc(node);
            //設定鍵值對後刪除該節點
            K keyRemoved = suc.key;
            V valueRemoved = suc.value;
            node.key = keyRemoved;
            node.value = valueRemoved;
            node.right = delete(node.right, keyRemoved);

        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    private BSTNode getSuc(BSTNode node) {
        BSTNode cur = node.right;
        while (cur != null && cur.left != null){
            cur = cur.left;
        }
        return cur;
    }

    public void printInOrder(){
        printInOrder(root);
    }

    private void printInOrder(BSTNode node){
        if(node == null){
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key + " " + node.value);
        printInOrder(node.right);
    }
}
