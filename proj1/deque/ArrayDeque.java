package deque;

import java.util.Iterator;

/*
 * add and remove must take constant time, except during resizing operations.
 * get and size must take constant time.
 * The starting size of your array should be 8.
 */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] array;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    @Override
    public void addFirst(T item) {
        array[nextFirst] = item;
        nextFirst--;
        size++;
        usageDealer();
    }

    @Override
    public void addLast(T item) {
        array[nextLast] = item;
        nextLast++;
        size++;
        usageDealer();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque(){
        for (T ele : this){
            System.out.println(ele + " ");
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        T item  = getFirst();
        nextFirst++;
        size--;
        usageDealer();
        return item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()){
            return null;
        }

        T item = getLast();
        nextLast--;
        size--;
        usageDealer();
        return item;
    }

    private T getFirst() {
        return array[nextFirst + 1];
    }

    private T getLast() {
        return array[nextLast - 1];
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[nextFirst + 1 + index];
    }

    /**
     * resize the capacity of the array
     * @param capacity
     */
    private void resize(int capacity) {
        T[] newArrayDeque = (T[]) new Object[capacity];
        int newNextFirst = (int) Math.round(capacity * 0.25);
        System.arraycopy(array, nextFirst + 1, newArrayDeque, newNextFirst + 1, size);

        array = newArrayDeque;
        nextFirst = newNextFirst;
        nextLast = nextFirst + size + 1;
    }

    /**
     * Deal with the special case to judge whether to resize
     */
    private void usageDealer() {
        if (nextFirst == -1) {
            // reach front, resize array deque length to 2x
            resize(array.length * 2);
        } else if (nextLast == array.length) {
            // reach end, resize array deque length to 2x
            resize(array.length * 2);
        } else if (size() > 16 && size() < (array.length / 4)) {
            // If Ratio Usage < 0.25 and size is larger than 10, half array deque length.
            resize(array.length / 2);
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int nextIndex;

        public ArrayDequeIterator() {
            nextIndex = 0;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public T next() {
            T item = get(nextIndex);
            nextIndex++;
            return item;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayDeque)) {
            return false;
        }
        ArrayDeque other = (ArrayDeque) obj;
        if (size != other.size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }
}
