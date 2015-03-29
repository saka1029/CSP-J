package jp.saka1029.cspj.main.old;

import java.io.File;
import java.io.IOException;

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

public class Sudoku extends SolverMain {

    Matrix<Variable<Integer>> variables;

    File input;
    public Sudoku input(String a) { input = new File(a); return this; }
    
    public Sudoku() {
    	addOption("-i", true, true, a -> input(a));
    }
    
    @Override
    public void define() throws IOException {
        Board board = new Board(input);
        Point size = board.box.size;
        if (size.x != 9 || size.y != 9)
            throw new RuntimeException("size must be 9 x 9");
        Domain<Integer> digit = Domain.range(1, 9);
        variables = new Matrix<>(size.x, size.y);
        for (int y = 0; y < size.y; ++y)
            for (int x = 0; x < size.x; ++x) {
                Integer n = board.numbers.get(new Point(x, y));
                Variable<Integer> v = problem.variable(
                	String.format("%d@%d", x, y), n == null ? digit : Domain.of(n));
                variables.set(x, y, v);
            }
        for (int i = 0; i < size.y; ++i) {
            problem.allDifferent(variables.row(i));
            problem.allDifferent(variables.column(i));
        }
        for (int y = 0; y < size.y; y += 3)
            for (int x = 0; x < size.x; x += 3)
                problem.allDifferent(variables.submatrix(x, y, 3, 3).asList());
        Log.info("Sudoku: file=%s", input);
        Log.info(board);
        printReducedProblem(problem.bind());
    }

    public void printReducedProblem(Bind bind) {
    	Log.info("*** reduced problem ***");
        Printer printer = new Printer();
        int width = variables.width;
        int height = variables.height;
        printer.draw(0, 0, width, height);
        printer.draw(0, 3, width, 3);
        printer.draw(3, 0, 3, height);
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x) {
            	Domain<Integer> d = bind.get(variables.get(x, y));
            	if (d.size() == 1)
                    printer.draw(x, y, d.first());
            }
        Log.info(printer);
    }
    
    @Override
    public boolean answer(int n, Result result) {
        Printer printer = new Printer();
        int width = variables.width;
        int height = variables.height;
        printer.draw(0, 0, width, height);
        printer.draw(0, 3, width, 3);
        printer.draw(3, 0, 3, height);
        for (int y = 0; y < height; ++y)
            for (int x = 0; x < width; ++x)
                printer.draw(x, y, result.get(variables.get(x, y)));
        Log.info(printer);
        try {
            printer.writeSVG(new File(input.getParentFile(), input.getName() + ".svg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
