package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {
    Comparator<Item> comparator;
    public MaxArrayDeque(Comparator<Item> c){
        comparator = c;
    }
    //max不能直接訪問父類別的items 但不代表沒有繼承items 還是可以用父類別的方法間接訪問items
    public Item max(){
        if(this.isEmpty()){
            return null;
        }
        Item max = get(0);
        for(int i=0; i<size(); i++){//用建構器的comparator比較Deque的元素 這個comparator繼承自Comparator接口，所以有一個compare方法
            if(comparator.compare(max, get(i)) < 0){
                max = get(i);
            }
        }
        return max;
    }

    public Item max(Comparator<Item> c){
        if(this.isEmpty()){
            return null;
        }
        Item max = get(0);
        for(int i=0; i<size(); i++){
            if(c.compare(max, get(i)) < 0){
                max = get(i);
            }
        }
        return max;
    }
}
