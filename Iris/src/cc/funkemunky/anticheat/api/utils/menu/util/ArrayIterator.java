package cc.funkemunky.anticheat.api.utils.menu.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {

    private final T[] elementArray;
    private int currentIndex;

    public ArrayIterator(T[] elementArray) {
        this.elementArray = elementArray;
    }

    @Override
    public boolean hasNext() {
        return elementArray.length > currentIndex;
    }

    @Override
    public T next() {
        return elementArray[currentIndex++];
    }
}
