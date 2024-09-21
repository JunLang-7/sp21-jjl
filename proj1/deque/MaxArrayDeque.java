package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    /**
     * Constructs the object with a comparator
     * @param cmp
     */
    public MaxArrayDeque(Comparator<T> cmp) {
        super();
        comparator = cmp;
    }

    /**
     * Returns the max element with the default comparator
     * which just returns the max element
     * @return
     */
    public T max(){
        return max(comparator);
    }

    /**
     * return the max element with the comparator c
     * @param cmp
     * @return
     */
    public T max(Comparator<T> cmp) {
        if (isEmpty()){
            return null;
        }
        int maxIndex = 0;
        for (int i = 1; i < this.size(); i++) {
            if (cmp.compare(this.get(i), this.get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return this.get(maxIndex);
    }
}
