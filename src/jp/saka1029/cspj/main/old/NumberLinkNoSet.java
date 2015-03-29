package jp.saka1029.cspj.main.old;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.old.Bind;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.SolverMain;

public class NumberLinkNoSet extends SolverMain {

    enum Direction { Left, Right, Up, Down; }

    static class Cell {
		final int number;
    	final Direction direction1;
        Cell(int number, Direction direction1) { this.number = number; this.direction1 = direction1; } 
        EnumSet<Direction> directions() { return EnumSet.of(direction1); }
        boolean contains(Direction d) { return direction1 == d; }
        boolean right(Cell c) {
        	if (number == c.number)
        		return contains(Direction.Right) && c.contains(Direction.Left)
        			&& !(contains(Direction.Up) && c.contains(Direction.Up))
        			&& !(contains(Direction.Down) && c.contains(Direction.Down));
        	else
        		return !contains(Direction.Right) && !c.contains(Direction.Left);
        }
        boolean below(Cell c) {
        	if (number == c.number)
        		return contains(Direction.Down) && c.contains(Direction.Up)
        			&& !(contains(Direction.Left) && c.contains(Direction.Left))
        			&& !(contains(Direction.Right) && c.contains(Direction.Right));
        	else
        		return !contains(Direction.Down) && !c.contains(Direction.Up);
        }
        @Override public int hashCode() { return number << 16 ^ direction1.hashCode(); }
        @Override
        public boolean equals(Object obj) {
        	return obj instanceof Cell && ((Cell)obj).number == number && ((Cell)obj).direction1 == direction1;
        }
    }
    
    static class CellEmpty extends Cell {
    	Direction direction2;
    	CellEmpty(int number, Direction direction1, Direction direction2) {
    		super(number, direction1);
    		this.direction2 = direction2;
    	}
        EnumSet<Direction> directions() { return EnumSet.of(direction1, direction2); }
        @Override boolean contains(Direction d) { return super.contains(d) || direction2 == d; }
        @Override public int hashCode() { return super.hashCode() << 8 ^ direction2.hashCode(); }
        @Override
        public boolean equals(Object obj) {
        	if (!(obj instanceof CellEmpty)) return false;
        	return super.equals(obj) && ((CellEmpty)obj).direction2 == direction2;
        }
    }

    Matrix<Variable<Cell>> variables;
    
    EnumSet<Direction> dirs(int x, int y) {
        EnumSet<Direction> r = EnumSet.noneOf(Direction.class);
        if (variables.contains(x - 1, y)) r.add(Direction.Left);
        if (variables.contains(x + 1, y)) r.add(Direction.Right);
        if (variables.contains(x, y - 1)) r.add(Direction.Up);
        if (variables.contains(x, y + 1)) r.add(Direction.Down);
        return r;
    }

    File input;
    public NumberLinkNoSet input(String a) { input = new File(a); return this; }
    
    public NumberLinkNoSet() {
    	addOption("-i", true, true, a -> input(a));
    }

    @Override
    public void define() throws IOException {
        Board b = new Board(input);
        Point size = b.box.size;
        Set<Integer> nums = new HashSet<>();
        nums.addAll(b.numbers.values());
        if (nums.size() * 2 != b.numbers.size())
        	throw new IllegalArgumentException("same number must appear twice");
        variables = new Matrix<>(size.x, size.y);
        for (int y = 0; y < size.y; ++y) {
            for (int x = 0; x < size.x; ++x) {
                EnumSet<Direction> dirs = dirs(x, y);
                Domain.Builder<Cell> builder = new Domain.Builder<>();
                Point pos = new Point(x, y);
                if (b.numbers.containsKey(pos)) {
                    int n = b.numbers.get(pos);
                    for (Direction e : dirs)
                        builder.add(new Cell(n, e));
                } else {
                    for (Direction in : dirs)
                        for (Direction out : dirs)
                            if (in.compareTo(out) < 0)
                                for (int n : nums)
                                    builder.add(new CellEmpty(n, in, out));
                }
                variables.set(x, y, problem.variable(
                	String.format("%d@%d", x, y), builder.build()));
            }
        }
        for (int y = 0; y < size.y; ++y)
            for (int x = 0; x < size.x; ++x) {
                if (variables.contains(x + 1, y))
                    problem.constraint(a -> ((Cell)a[0]).right((Cell)a[1]),
                    	"right", variables.get(x, y), variables.get(x + 1, y));
                if (variables.contains(x, y + 1))
                    problem.constraint(a -> ((Cell)a[0]).below((Cell)a[1]),
                    	"below", variables.get(x, y), variables.get(x, y + 1));
            }
        printReducedProblem();
    }
    
    void printReducedProblem() {
    	Log.info("*** reduced problem ***");
    	Bind bind = problem.bind();
        Printer printer = new Printer();
        printer.draw(0, 0, variables.width, variables.height);
        for (int y = 0; y < variables.height; ++y)
            for (int x = 0; x < variables.width; ++x) {
                Domain<Cell> v = bind.get(variables.get(x,  y));
                Set<Integer> n = new HashSet<>();
                Set<EnumSet<Direction>> d = new HashSet<>();
                for (Cell e : v) {
                	n.add(e.number);
                	d.add(e.directions());
                }
                int nn = n.iterator().next();
                EnumSet<Direction> dd = d.iterator().next();
                if (n.size() == 1)
                    printer.draw(x, y, dd.size() == 1 ? nn : "*" + nn);
                if (d.size() == 1 && dd.size() == 2)
                    printer.draw(x, y, PATH.get(dd));
            }
        Log.info(printer);
    }

    static final Map<EnumSet<Direction>, String> PATH = new HashMap<>();

    static {
        PATH.put(EnumSet.of(Direction.Left, Direction.Right), "━");
        PATH.put(EnumSet.of(Direction.Left, Direction.Up), "┛");
        PATH.put(EnumSet.of(Direction.Left, Direction.Down), "┓");
        PATH.put(EnumSet.of(Direction.Right, Direction.Up), "┗");
        PATH.put(EnumSet.of(Direction.Right, Direction.Down), "┏");
        PATH.put(EnumSet.of(Direction.Up, Direction.Down), "┃");
    }
    
    @Override
    public boolean answer(int n, Result result) {
        Printer printer = new Printer();
        printer.draw(0, 0, variables.width, variables.height);
        for (int y = 0; y < variables.height; ++y)
            for (int x = 0; x < variables.width; ++x) {
                Cell e = (Cell)result.get(variables.get(x,  y));
                Point p = new Point(x, y);
                if (e instanceof CellEmpty)
                    printer.draw(p, PATH.get(e.directions()));
                else
                    printer.draw(p, Integer.toString(e.number));
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
        new NumberLinkNoSet().parse(args).solve();
    }

}
