package jp.saka1029.cspj.geometry;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

public class Mat<T> extends AbstractList<T> {

    private final Object[] elements;
    private final int baseRows, baseCols;
    public final int rows, cols;
    private final int startRow, startCol;
    
    private Mat(Object[] elements,
            int baseRows, int baseCols,
            int startRow, int startCol,
            int rows, int cols
            ) {
        if (elements == null) throw new NullPointerException("elements");
        if (baseRows < 0) throw new IllegalArgumentException("baseRows");
        if (baseCols < 0) throw new IllegalArgumentException("baseCols");
        if (elements.length != baseRows * baseCols) throw new IllegalArgumentException("elements.length != baseRows * baseCols");
        if (startRow < 0) throw new IllegalArgumentException("startRow");
        if (startCol < 0) throw new IllegalArgumentException("startCol");
        if (rows < 0) throw new IllegalArgumentException("rows");
        if (cols < 0) throw new IllegalArgumentException("cols");
        if (startRow + rows > baseRows) throw new IllegalArgumentException("startRow + rows > baseRows");
        if (startCol + cols > baseCols) throw new IllegalArgumentException("startCol + cols > baseCols");
        this.elements = elements;
        this.baseRows = baseRows;
        this.baseCols = baseCols;
        this.startRow = startRow;
        this.startCol = startCol;
        this.rows = rows;
        this.cols = cols;
    }

    public Mat(int rows, int cols) {
        this(new Object[rows * cols], rows, cols, 0, 0, rows, cols);
    }
    
    public Mat(int rows, int cols, BiFunction<Integer, Integer, T> initializer) {
        this(rows, cols);
        for (int r = 0; r < rows; ++r)
            for (int c = 0; c < cols; ++c)
                set(r, c, initializer.apply(r, c));
    }
    
    private int index(int row, int col) {
        if (row < 0 || row >= rows) throw new IndexOutOfBoundsException("row");
        if (col < 0 || col >= cols) throw new IndexOutOfBoundsException("col");
        return (startRow + row) * baseCols + startCol + col;
    }

    @SuppressWarnings("unchecked")
    public T get(int row, int col) {
        return (T)elements[index(row, col)];
    }

    public T set(int row, int col, T element) {
        elements[index(row, col)] = element;
        return element;
    }

    public List<T> row(final int row) {
        return new AbstractList<T>() {

            @Override
            public T get(int index) {
                return Mat.this.get(row, index);
            }

            @Override
            public T set(int index, T element) {
                return Mat.this.set(row, index, element);
            }

            @Override
            public int size() {
                return cols;
            }
        };
    }
    
    public List<T> col(final int col) {
        return new AbstractList<T>() {

            @Override
            public T get(int index) {
                return Mat.this.get(index, col);
            }

            @Override
            public T set(int index, T element) {
                return Mat.this.set(index, col, element);
            }

            @Override
            public int size() {
                return rows;
            }
        };
    }
    
    public Mat<T> slice(int startRow, int startCol, int rows, int cols) {
        return new Mat<>(elements, baseRows, baseCols, startRow, startCol, rows, cols);
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int row = 0;
            int col = 0;
            
            @Override
            public boolean hasNext() {
                return row < rows;
            }

            @Override
            public T next() {
                T r = get(row, col);
                if (++col >= cols) {
                    col = 0;
                    ++row;
                }
                return r;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int r = 0; r < rows; ++r)
            sb.append(row(r));
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public T get(int index) {
        return get(index / cols, index % cols);
    }

    @Override
    public T set(int index, T element) {
        return set(index /cols, index % cols, element);
    }

    @Override
    public int size() {
        return rows * cols;
    }

}
