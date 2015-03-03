package jp.saka1029.cspj.geometry;

import java.util.ArrayList;
import java.util.List;

public class Matrix<T> {

    private final List<List<T>> matrix = new ArrayList<>();
    
    public final int width, height;
    
    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
        for (int y = 0; y < height; ++y) {
            List<T> line = new ArrayList<>();
            matrix.add(line);
            for (int x = 0; x < width; ++x)
                line.add(null);
        }
    }
    
    public Matrix(int width, int height, T fill) {
        this(width, height);
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                set(x, y, fill);
    }

    public T get(int x, int y) {
        return matrix.get(y).get(x);
    }
    
    public T get(Point p) {
        return get(p.x, p.y);
    }
    
    public T get(int x, int y, T ifAbsent) {
    	return contains(x, y) ? get(x, y) : ifAbsent;
    }

    public T get(Point p, T ifAbsent) {
    	return get(p.x, p.y, ifAbsent);
    }

    public void set(int x, int y, T element) {
        matrix.get(y).set(x, element);
    }
    
    public void set(Point p, T element) {
        set(p.x, p.y, element);
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public List<T> asList() {
        List<T> r = new ArrayList<>();
        for (List<T> row : matrix)
            for (T e : row)
                r.add(e);
        return r;
    }
    
    public List<T> row(int index) {
        return new ArrayList<>(matrix.get(index));
    }
    
    public List<T> column(int index) {
        List<T> r = new ArrayList<>();
        for (int y = 0; y < height; ++y)
            r.add(get(index, y));
        return r;
    }
    
    public List<T> diagonal(int index) {
        if (width != height)
            throw new RuntimeException("width must eqaul to height");
        if (index != 0 && index != 1)
            throw new IllegalArgumentException("index must be 0(main) or 1(sub)");
        List<T> r = new ArrayList<>();
        for (int i = 0; i < height; ++i)
            r.add(get(index == 0 ? i : width - i - 1, i));
        return r;
    }
    
    public Matrix<T> submatrix(int left, int top, int width, int height) {
        Matrix<T> r = new Matrix<T>(width, height);
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                r.set(x, y, get(x + left, y + top));
        return r;
    }
}
