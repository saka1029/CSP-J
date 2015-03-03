package jp.saka1029.cspj.geometry;

import java.util.HashSet;
import java.util.Set;

import jp.saka1029.cspj.problem.StringFormatter;

public class TicTacToe {
    
    static final int MASK = 0x3ffff;
    static final int MAX = 3;
    static final int SIZE = MAX * MAX;

    int tic;
    Set<TicTacToe> next = new HashSet<>();
    
    private TicTacToe() {}
    private TicTacToe(int tic) { this.tic = tic; }
    
    public int get(int x, int y) {
        return get(tic, x, y);
    }
    
    public Iterable<TicTacToe> next() {
        return next;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TicTacToe))
            return false;
        TicTacToe o = (TicTacToe)obj;
        return tic == o.tic;
    }
    
    @Override
    public int hashCode() {
        return tic;
    }
    
    @Override
    public String toString() {
        return string(tic);
    }

    static void tree(TicTacToe root, int index) {
        if (index >= SIZE) return;
        int tic = root.tic;
        for (int y = 0; y < MAX; ++y)
            for (int x = 0; x < MAX; ++x)
                if (get(tic, x, y) == 0) {
                    TicTacToe c = new TicTacToe(set(tic, x, y, index % 2 + 1));
                    root.next.add(c);
                    tree(c, index + 1);
                }
    }

    public static TicTacToe tree() {
        TicTacToe r = new TicTacToe(0);
        tree(r, 0);
        return r;
    }
    
    static int index(int tic, int x, int y) {
        if (tic < 0 || tic > MASK) throw new IllegalArgumentException("tic");
        if (x < 0 || x >= MAX) throw new IllegalArgumentException("x");
        if (y < 0 || y >= MAX) throw new IllegalArgumentException("y");
        return y * MAX + x;
    }

    public static int get(int tic, int x, int y) {
        int i = index(tic, x, y);
        return (tic >> (i * 2)) & 3;
    }
    
    public static int set(int tic, int x, int y, int element) {
        int i = index(tic, x, y);
        int s = i * 2;
        return (tic & (MASK ^ (3 << s))) | (element << s);
    }
 
    static int winner(int a, int b, int c) {
        return a != 0 && a == b && a == c ? a : 0;
    }

    public static int winner(int tic) {
        for (int i = 0; i < MAX; ++i) {
            int w = winner(get(tic, i, 0), get(tic, i, 1), get(tic, i, 2));
            if (w != 0) return w;
            int x = winner(get(tic, 0, i), get(tic, 1, i), get(tic, 2, i));
            if (x != 0) return x;
        }
        int y = winner(get(tic, 0, 2), get(tic, 1, 1), get(tic, 2, 0));
        if (y != 0) return y;
        int z = winner(get(tic, 0, 0), get(tic, 1, 1), get(tic, 2, 2));
        if (z != 0) return z;
        return 0;
    }

    static String str(int element) {
        switch (element) {
            case 1: return "x";
            case 2: return "o";
            default : return "-";
        }
    }

    public static String string(int tic) {
        StringFormatter f = new StringFormatter();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x)
                f.append(str(get(tic, x, y)));
            f.append("%n");
        }
        return f.toString();
    }
    
}
