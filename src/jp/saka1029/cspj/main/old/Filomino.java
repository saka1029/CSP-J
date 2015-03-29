package jp.saka1029.cspj.main.old;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.PointSet;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Util;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.SolverMain;

public class Filomino extends SolverMain {

    Board board;
    List<Variable<PointSet>> variables;
    
    static boolean contains(List<Point> list, Point p, int max) {
        for (int i= 0; i < max; ++i)
            if (list.get(i).equals(p))
                return true;
        return false;
    }

    boolean noSameNumberInNeighbors(PointSet ps) {
        int size = ps.size();
        for (Point e : ps.neighbors()) {
            Integer nn = board.numbers.get(e);
            if (nn != null && size == nn)
                return false;
        }
        return true;
    }

    void shapes(int n, Point next, int i, List<Point> list, Set<PointSet> r) {
        if (!board.box.contains(next)) return;
        Integer nn = board.numbers.get(next);
        if (nn != null && nn != n) return;
        if (contains(list, next, i)) return;
        list.set(i, next);
        if (i + 1 >= n) {
            PointSet ps = new PointSet(list);
            if (noSameNumberInNeighbors(ps))
                r.add(ps);
        } else
            for (int j = 0; j <= i; ++j)
                for (Point o : list.get(j).neighbors())
                    shapes(n, o, i + 1, list, r);
    }

    Set<PointSet> shapes(Point origin, int n) {
        Set<PointSet> r = new HashSet<>();
        List<Point> list = Util.array(n);
        shapes(n, origin, 0, list, r);
        return r;
    }

    static boolean neighbors(PointSet a, PointSet b) {
        for (Point e : a.neighbors())
            if (b.contains(e))
                return true;
        return false;
    }
    
    File input;
    public Filomino input(String a) { input = new File(a); return this; }
    
    public Filomino() {
    	addOption("-i", true, true, a -> input(a));
    }

    @Override
    public void define() throws IOException {
        board = new Board(input);
        variables = new ArrayList<>();
        for (Entry<Point, Integer> e : board.numbers.entrySet()) {
            Point origin = e.getKey();
            int n = e.getValue();
            Domain<PointSet> d = Domain.of(shapes(origin, n));
            variables.add(problem.variable(String.format("v%d@%s", n, origin), d));
        }
        problem.forEachPairs(
            a -> {
                PointSet x = (PointSet)a[0];
                PointSet y = (PointSet)a[1];
                return x.equals(y) || !x.overlap(y) && !(x.size() == y.size() && neighbors(x, y));
            }, "notOverlapOrSame", variables);
    }

    void rest(Matrix<Integer> m, Point p, PointSet.Builder b) {
        if (!m.contains(p)) return;
        if (m.get(p) != 0) return;
        if (b.contains(p)) return;
        b.add(p);
        for (Point e : p.neighbors())
            rest(m, e, b);
    }

    Set<PointSet> rest(Matrix<Integer> m) {
        Set<PointSet> r = new TreeSet<>();
        for (int y = 0; y < m.height; ++y)
            for (int x = 0; x < m.width; ++x) {
                int n = m.get(x, y);
                if (n != 0) continue;
                PointSet.Builder b = new PointSet.Builder();
                rest(m, new Point(x, y), b);
                r.add(b.build());
            }
        return r;
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        if (n > 1000) {
            Log.info("answer count exeeds 100");
            return false;
        }
        int width = board.box.size.x, height = board.box.size.y;
        Matrix<Integer> m = new Matrix<>(width, height, 0);
        for (Variable<PointSet> v : variables) {
            PointSet set = result.get(v);
            int s = set.size();
            for (Point p : set)
                m.set(p.x, p.y, s);
        }
        boolean notAnswer = false;
        Set<PointSet> rest = rest(m);
        Log.info("answer %d:", n);
        Log.info("Filomino: rest = %s", rest);
        L: for (PointSet e : rest) {
            int size = e.size();
            if (size > 2) continue;
            for (Point p : e.neighbors())
                if (m.contains(p) && m.get(p) == size) {
                    Log.info("Filomino: not an answer");
                    notAnswer = true;
                    break L;
                }
        }
        if (!notAnswer) {
            Printer printer = new Printer();
            printer.draw(board.box);
            for (Entry<Point, Integer> e : board.numbers.entrySet())
                printer.draw(e.getKey(), e.getValue());
            for (int y = 0; y < height; ++y)
                for (int x = 0; x < width; ++x) {
                    int self = m.get(x, y);
    //                printer.draw(x, y, self);
                    if (m.contains(x + 1, y) && self != m.get(x + 1, y))
                        printer.draw(x + 1, y, 0, 1);
                    if (m.contains(x, y + 1) && self != m.get(x, y + 1))
                        printer.draw(x, y + 1, 1, 0);
                    if (self == 0)
                        printer.draw(x, y, "?");
                }
            Log.info(printer);
            printer.writeSVG(new File(input.getParent(), input.getName() + ".svg"));
        }
        return true;
    }

}
