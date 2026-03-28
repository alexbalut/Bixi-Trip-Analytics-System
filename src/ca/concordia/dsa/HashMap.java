package ca.concordia.dsa;

public class HashMap<K, V> {

    private Node<K, V>[] buckets;
    private int size;
    private int capacity;

    public HashMap() {
        capacity = 16;
        buckets = new Node[capacity];
        size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public void put(K key, V value) {
        if ((double) size / capacity >= 0.75) {
            resize();
        }
        int index = hash(key);
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        size++;
    }

    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public DynamicArray<K> getKeys() {
        DynamicArray<K> keys = new DynamicArray<>();
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                keys.add(current.key);
                current = current.next;
            }
        }
        return keys;
    }

    public DynamicArray<V> getValues() {
        DynamicArray<V> values = new DynamicArray<>();
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                values.add(current.value);
                current = current.next;
            }
        }
        return values;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        capacity = capacity * 2;
        Node<K, V>[] newBuckets = new Node[capacity];
        for (int i = 0; i < buckets.length; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                Node<K, V> next = current.next;
                int newIndex = Math.abs(current.key.hashCode()) % capacity;
                current.next = newBuckets[newIndex];
                newBuckets[newIndex] = current;
                current = next;
            }
        }
        buckets = newBuckets;


    }
}