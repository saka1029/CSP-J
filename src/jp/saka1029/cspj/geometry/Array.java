package jp.saka1029.cspj.geometry;

import java.util.AbstractList;
import java.util.Collection;
import java.util.function.Function;

public class Array<T> extends AbstractList<T> {

    private final Object[] elements;
    private final int start;
    private final int end;
    
    private Array(Object[] elements, int start, int end) {
        if (start < 0 || start > end) throw new IllegalArgumentException("start");
        if (end < 0 || end > elements.length) throw new IllegalArgumentException("end");
        this.elements = elements;
        this.start = start;
        this.end = end;
    }

    public Array(int size) {
        this(new Object[size], 0, size);
    }
    
    @SafeVarargs
    public static <T> Array<T> of(T... elements) {
        int size = elements.length;
        Array<T> r = new Array<>(size);
        for (int i = 0; i < size; ++i)
            r.set(i, elements[i]);
        return r;
    }

    public static <T> Array<T> of(Collection<? extends T> elements) {
        Array<T> r = new Array<>(elements.size());
        int i = 0;
        for (T e : elements)
            r.set(i++, e);
        return r;
    }

    public static <T> Array<T> of(int size, Function<Integer, T> initializer) {
        Array<T> r = new Array<>(size);
        for (int i = 0; i < size; ++i)
            r.set(i, initializer.apply(i));
        return r;
    }
    
    @Override
    public int size() { return end - start; }
    
    private int index(int index) {
        int i = start + index;
        if (index < 0 || i >= end)
            throw new IllegalArgumentException("index");
        return i;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) { return (T)elements[index(index)]; }
    
    @Override
    public T set(int index, T element) { elements[index(index)] = element; return element; }
    
    public Array<T> slice(int start, int size) {
        int s = this.start + start;
        return new Array<>(elements, s, s + size);
    }
}
