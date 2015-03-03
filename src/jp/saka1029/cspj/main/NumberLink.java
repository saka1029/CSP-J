package jp.saka1029.cspj.main;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.ConstraintFunction;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class NumberLink extends SolverMain {

    enum Dir { Left, Right, Above, Below}

    static class Cell {

        final int n;
        final EnumSet<Dir> dir;
        
        Cell(int n, EnumSet<Dir> dir) {
            this.n = n;
            this.dir = dir;
        }

        Cell(int n, Dir in) {
            this(n, EnumSet.of(in)); 
        }
        
        Cell(int n, Dir in, Dir out) {
            this(n, EnumSet.of(in, out));
        }
        
        boolean right(Cell r) {
            return dir.contains(Dir.Right)
                ? r.dir.contains(Dir.Left) && n == r.n
                : !r.dir.contains(Dir.Left) && n != r.n;
        }

        static ConstraintFunction<Boolean> RIGHT = a -> ((Cell)a[0]).right((Cell)a[1]);
        
        boolean below(Cell r) {
            return dir.contains(Dir.Below)
                ? r.dir.contains(Dir.Above) && n == r.n
                : !r.dir.contains(Dir.Above) && n != r.n;
        }

        static ConstraintFunction<Boolean> BELOW = a -> ((Cell)a[0]).below((Cell)a[1]);

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Cell))
                return false;
            Cell o = (Cell)obj;
            return n == o.n && dir.equals(o.dir);
        }
        
        @Override
        public int hashCode() {
            return n << 16 ^ dir.hashCode();
        }
        
        @Override
        public String toString() {
            return String.format("{%s %s}", n, dir);
        }
    }

    Matrix<Variable<Cell>> variables;
    
    EnumSet<Dir> dirs(int x, int y) {
        EnumSet<Dir> r = EnumSet.noneOf(Dir.class);
        if (variables.contains(x - 1, y)) r.add(Dir.Left);
        if (variables.contains(x + 1, y)) r.add(Dir.Right);
        if (variables.contains(x, y - 1)) r.add(Dir.Above);
        if (variables.contains(x, y + 1)) r.add(Dir.Below);
        return r;
    }

    File input;
    public NumberLink input(String a) { input = new File(a); return this; }
    
    public NumberLink() {
    	addOption("-i", true, true, a -> input(a));
    }

    String name(int x, int y) {
        return String.format("v_%d_%d", x, y);
    }

    @Override
    public void define() throws IOException {
        Board b = new Board(input);
        Point size = b.box.size;
        int maxNumber = b.numbers.size();
        variables = new Matrix<>(size.x, size.y);
        for (int y = 0; y < size.y; ++y) {
            for (int x = 0; x < size.x; ++x) {
                EnumSet<Dir> dirs = dirs(x, y);
                Domain.Builder<Cell> builder = new Domain.Builder<>();
                Point pos = new Point(x, y);
                if (b.numbers.containsKey(pos)) {
                    int n = b.numbers.get(pos);
                    for (Dir e : dirs)
                        builder.add(new Cell(n, e));
                } else {
                    for (Dir in : dirs)
                        for (Dir out : dirs)
                            if (in.compareTo(out) < 0)
                                for (int n = 1; n <= maxNumber; ++n)
                                    builder.add(new Cell(n, in, out));
                }
                variables.set(x, y, problem.variable(name(x, y), builder.build()));
            }
        }
        for (int y = 0; y < size.y; ++y)
            for (int x = 0; x < size.x; ++x) {
                if (variables.contains(x + 1, y))
                    problem.constraint(Cell.RIGHT, "right", variables.get(x, y), variables.get(x + 1, y));
                if (variables.contains(x, y + 1))
                    problem.constraint(Cell.BELOW, "below", variables.get(x, y), variables.get(x, y + 1));
            }
    }

    static final Map<EnumSet<Dir>, String> PATH = new HashMap<>();

    static {
        PATH.put(EnumSet.of(Dir.Left, Dir.Right), "━");
        PATH.put(EnumSet.of(Dir.Left, Dir.Above), "┛");
        PATH.put(EnumSet.of(Dir.Left, Dir.Below), "┓");
        PATH.put(EnumSet.of(Dir.Right, Dir.Above), "┗");
        PATH.put(EnumSet.of(Dir.Right, Dir.Below), "┏");
        PATH.put(EnumSet.of(Dir.Above, Dir.Below), "┃");
    }
    
    @Override
    public boolean answer(int n, Result result) {
        Printer printer = new Printer();
        printer.draw(0, 0, variables.width, variables.height);
        for (int y = 0; y < variables.height; ++y)
            for (int x = 0; x < variables.width; ++x) {
                Cell e = (Cell)result.get(variables.get(x,  y));
                Point p = new Point(x, y);
                if (e.dir.size() == 1)
                    printer.draw(p, Integer.toString(e.n));
                else
                    printer.draw(p, PATH.get(e.dir));
            }
        Log.info(printer);
        File svg = new File(input.getParentFile(), input.getName() + ".svg");
        try {
            printer.writeSVG(svg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public static void main(String[] args) throws Exception {
        new NumberLink().parse(args).solve();
    }

}
