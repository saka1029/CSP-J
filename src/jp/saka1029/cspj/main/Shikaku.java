package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Box;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Shikaku extends SolverMain {

	File input;
	public Shikaku input(String f) { input = new File(f); return this; }
	
	public Shikaku() {
		addOption("-i", true, true, a -> input(a));
	}

    Board board;
    List<Variable<Box>> variables;
    
    @Override
    public void define() throws IOException {
        board = new Board(input);
        Log.info("Shikaku: file=%s", input);
        Log.info(board);
        variables = new ArrayList<>();
        for (Entry<Point, Integer> e : board.numbers.entrySet()) {
            Point p = e.getKey();
            int n = e.getValue();
            Domain.Builder<Box> builder = new Domain.Builder<>();
            for (int h = 1; h <= n; ++h) {
                if (n % h != 0) continue;
                int w = n / h;
                for (int y = p.y - h + 1; y <= p.y; ++y)
                    L: for (int x = p.x - w + 1; x <= p.x; ++x) {
                        Box b = new Box(x, y, w, h);
                        if (!board.box.contains(b)) continue;
                        for (Point q : board.numbers.keySet())
                            if (!q.equals(p) && b.contains(q))
                                continue L;
                        builder.add(b);
                    }
            }
            variables.add(problem.variable(String.format("%d@%s", n, p), builder.build()));
        }
        problem.forEachPairs(a -> !((Box)a[0]).overlap((Box)a[1]), "notOverlap", variables);
    }

    @Override
    public boolean answer(int n, Result result) {
        Printer printer = new Printer();
        printer.draw(board.box);
        for (Variable<Box> v : variables)
            printer.draw((Box)result.get(v));
        for (Entry<Point, Integer> e : board.numbers.entrySet())
            printer.draw(e.getKey(), e.getValue());
        Log.info(printer);
        File svg = new File(input.getParentFile(), input.getName() + ".svg");
        try (Writer w = new OutputStreamWriter( new FileOutputStream(svg), "utf-8")) {
            w.write(printer.toSVG());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        new Shikaku().parse(args).solve();
    }
}
