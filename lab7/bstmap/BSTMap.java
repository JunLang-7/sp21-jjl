package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class Node {
        public final K key;
        public V value;
        public Node left;
        public Node right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(key, root);
    }

    private boolean containsKeyHelper(K key, Node node) {
        if (node == null) {
            return false;
        }
        if (key.compareTo(node.key) < 0) {
            return containsKeyHelper(key, node.left);
        }
        else if (key.compareTo(node.key) > 0) {
            return containsKeyHelper(key, node.right);
        }
        return true;
    }

    @Override
    public V get(K key) {
        return getKeyHelper(key, root);
    }

    private V getKeyHelper(K key, Node node) {
        if (node == null) {
            return null;
        }
        if (key.compareTo(node.key) < 0) {
            return getKeyHelper(key, node.left);
        }
        else if (key.compareTo(node.key) > 0) {
            return getKeyHelper(key, node.right);
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
        size++;
    }

    private Node putHelper(K key, V value, Node node) {
        if (node == null) {
            return new Node(key, value);
        }
        if (key.compareTo(node.key) < 0) {
            node.left = putHelper(key, value, node.left);
        }
        else if (key.compareTo(node.key) > 0) {
            node.right = putHelper(key, value, node.right);
        }
        else {
            node.value = value;
        }
        return node;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)) {
            V targetValue = get(key);
            root = remove(key, root);
            size--;
            return targetValue;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key)) {
            V targetValue = get(key);
            root = remove(key, root);
            size--;
            return targetValue;
        }
        return null;
    }

    private Node remove(K key, Node node) {
        if (node == null) {
            return null;
        }
        if (key.compareTo(node.key) < 0) {
            node.left = remove(key, node.left);
        }
        else if (key.compareTo(node.key) > 0) {
            node.right = remove(key, node.right);
        }
        else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node origin = node;
            node = getMaxChild(node.left);
            node.right = origin.right;
            node.left = remove(node.key, origin.left);
        }
        return node;
    }

    private Node getMaxChild(Node node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>();
        addKeys(root, keySet);
        return keySet;
    }

    private void addKeys(Node node, Set<K> keySet) {
        if (node == null) {
            return;
        }
        addKeys(node.left, keySet);
        keySet.add(node.key);
        addKeys(node.right, keySet);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        printPreOrder(root);
    }

    private void printPreOrder(Node node) {
        if (node == null) {
            return;
        }
        printPreOrder(node.left);
        System.out.println(node.key.toString() + " -> " + node.value.toString());
        printPreOrder(node.right);
    }
}
