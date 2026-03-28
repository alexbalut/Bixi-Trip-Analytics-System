package ca.concordia.dsa;

import java.util.Iterator;
import java.util.NoSuchElementException;

//Dynamic array is a resizable array. Custom implementation of ArrayList

public class DynamicArray<T> implements Iterable<T> {

    private Object[] data;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    public DynamicArray() {
        data = new Object[INITIAL_CAPACITY];
        size = 0;
    }

    // Add element to end O(1)
    public void add(T element) {
        if (size == data.length) {
            resize();
        }
        data[size] = element;
        size++;
    }

    // getter O(1)
    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        return (T) data[index];
    }

    //setter O(1)
    public void set(int index, T element) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        data[index] = element;
    }

    // Returns size of array O(1)
    public int size() {
        return size;
    }

    //Checks if empty O(1)
    public boolean isEmpty() {
        return size == 0;
    }

    //Clears array O(1)
    public void clear() {
        data = new Object[INITIAL_CAPACITY];
        size = 0;
    }

    // creates new array double the size and copy everything to new array O(n)
    private void resize() {
        Object[] newData = new Object[data.length * 2];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }

    // Makes DynamicArray usable in for-each loops
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) data[cursor++];
            }
        };
    }

}
