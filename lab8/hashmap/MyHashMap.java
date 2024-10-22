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

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private final int DEFAULT_SIZE = 16;
    private final double DEFAULT_LOAD_FACTOR = 0.75;
    /** The bucket size of MyHashMap */
    private int bucketSize;
    /** The load factor of MuHashMap */
    private final double loadFactor;
    /** The element size of MyHashMap */
    private int eleSize = 0;

    /** Constructors */
    public MyHashMap() {
        this.bucketSize = DEFAULT_SIZE;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        buckets = createTable(bucketSize);
    }

    public MyHashMap(int initialSize) {
        this.bucketSize = initialSize;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        buckets = createTable(bucketSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(bucketSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

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
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(DEFAULT_SIZE);
        bucketSize = DEFAULT_SIZE;
        eleSize = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int bucketIndex = getIndex(key);
        Node node = getNode(key, bucketIndex);
        return node != null ? node.value : null;
    }

    private Node getNode(K key, int bucketIndex) {
        for (Node node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return eleSize;
    }

    @Override
    public void put(K key, V value) {
        assert key != null;
        int bucketIndex = getIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node != null) {
            node.value = value;
            return;
        }
        node = createNode(key, value);
        buckets[bucketIndex].add(node);
        eleSize++;

        if ((double) eleSize / bucketSize >= loadFactor) {
            resize();
        }
    }

    private void resize() {
        Collection<Node>[] temp = buckets;
        bucketSize *= 2;
        buckets = createTable(bucketSize);
        for (Collection<Node> bucket : temp) {
            for (Node node : bucket) {
                int bucketIndex = getIndex(node.key);
                buckets[bucketIndex].add(node);
            }
        }
    }

    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), bucketSize);
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    keySet.add(node.key);
                }
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        int bucketIndex = getIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node == null) {
            return null;
        }
        buckets[bucketIndex].remove(node);
        eleSize--;
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }
}
