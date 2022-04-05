package dev.codescreen;

import java.util.HashMap;
import java.util.Map;

public class LruMapImpl<K, V> implements LruMap<K, V> {

    private final Map<K, Node<K, V>> nodeMap;
    private final int capacity;
    private final Node<K, V> head;
    private final Node<K, V> tail;
    private int size = 0;

    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        Node<K, V> previous;

        private Node() {
            this(null, null);
        }

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public LruMapImpl(int capacity) {
        this.capacity = capacity;
        this.nodeMap = new HashMap<>(capacity);
        this.head = new Node<>();
        this.tail = new Node<>();
        head.next = tail;
        tail.previous = head;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = find(key);
        if (node != tail) {
            touchNode(node);
        }

        return node.value;
    }

    @Override
    public void remove(K key) {
        Node<K, V> node = nodeMap.remove(key);
        if (node != null) {
            doRemove(node);
            size--;
        }
    }

    @Override
    public void put(K key, V value) {
        Node<K, V> node = find(key);
        if (node != tail) {
            node.value = value;
            touchNode(node);
        } else {
            ensureCapacity();
            node = new Node<>(key, value);
            addToHead(node);
            nodeMap.put(key, node);
            size++;
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size > 0 && size == capacity) {
            Node<K, V> node = tail.previous;
            doRemove(node);
            nodeMap.remove(node.key);
            size--;
        }
    }

    private void touchNode(Node<K, V> node) {
        doRemove(node);
        addToHead(node);
    }

    private void doRemove(Node<K, V> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    private void addToHead(Node<K, V> node) {
        node.next = head.next;
        node.previous = head;
        head.next.previous = node;
        head.next = node;
    }

    private Node<K, V> find(K key) {
        Node<K, V> item = nodeMap.get(key);
        return item != null ? item : tail;
    }
}