package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{

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

    private static class IntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    public static Comparator<Integer> getIntComparator(){
        return new IntComparator();
    }

    private static class AbsIntComparator implements Comparator<Integer>{
        @Override
        public int compare(Integer o1, Integer o2) {
            return Math.abs(o1 - o2);
        }
    }

    public static Comparator<Integer> getAbsIntComparator(){
        return new AbsIntComparator();
    }

    private static class DoubleComparator implements Comparator<Double>{
        @Override
        public int compare(Double o1, Double o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<Double> getDoubleComparator(){
        return new DoubleComparator();
    }

    private static class LongComparator implements Comparator<Long>{
        @Override
        public int compare(Long o1, Long o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<Long> getLongComparator(){
        return new LongComparator();
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    public static Comparator<String> getStringComparator(){
        return new StringComparator();
    }
}
