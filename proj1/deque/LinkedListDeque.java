package deque;

import java.util.Iterator;

/*
 * add and remove operations must not involve any looping or recursion.
 *  A single such operation must take “constant time”,
 *  i.e. execution time should not depend on the size of the deque.
 *  This means that you cannot use loops that go over all/most elements of the deque.
 * get must use iteration, not recursion.
 * size must take constant time.
 */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private Node next;
        private Node prev;
        private T item;

        /**
         * Construct the Node
         * @param i the item of Node
         * @param n the next of Node
         */
        Node(T i, Node n) {
            item = i;
            next = n;
        }
    }
    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node firstNode = sentinel.next;
        firstNode.prev = new Node(item, firstNode);
        sentinel.next = firstNode.prev;
        firstNode.prev.prev = sentinel;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node lastNode = sentinel.prev;
        lastNode.next = new Node(item, sentinel);
        sentinel.prev = lastNode.next;
        lastNode.next.prev = lastNode;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p.next != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T item = sentinel.next.item;
        Node temp = sentinel.next;
        sentinel.next = temp.next;
        temp.next.prev = sentinel;
        size--;
        return item;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T item = sentinel.prev.item;
        Node temp = sentinel.prev;
        sentinel.prev = temp.prev;
        temp.prev.next = sentinel;
        size--;
        return item;
    }

    @Override
    public T get(int index) {
        Node p = sentinel.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
            if (p == sentinel) {
                return null;
            }
        }
        return p.item;
    }

    /**
     * Same as get, but uses recursion
     * @param index
     */
    public T getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next);
    }

    /**
     * Helper Function: returns the next recursively
     * until it reaches the index or reaches the end
     * @param index
     * @param p
     */
    private T getRecursiveHelper(int index, Node p) {
        if (p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node current;

        LinkedListIterator() {
            current = sentinel.next;
        }

        public boolean hasNext() {
            return current != sentinel;
        }

        public T next() {
            T tmp = current.item;
            current = current.next;
            return tmp;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Deque)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        Deque other = (Deque) obj;
        if (size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }
}
