package jp.saka1029.cspj.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Box;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Shikaku extends SolverMain {

	static final Logger logger = Logger.getLogger(Shikaku.class.getName());
	
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
        logger.info("Shikaku: file=" + input);
        logger.info(board.toString());
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
//        constraint(mapPair("notOverlap", (a, b) -> !a.overlap(b), variables));
        constraint(mapPair((x, y) -> variable(null, "notOverlap", (a, b) -> !a.overlap(b), x, y), variables));
//        printReduced(problem.bind());
    }

    void printReduced(Bind bind) {
    	logger.info("*** reduced problem ***");
        Printer printer = new Printer();
        printer.draw(board.box);
        for (Variable<? extends Box> v : variables) {
        	Domain<? extends Box> d = bind.get(v);
        	if (d.size() == 1)
                printer.draw(d.first());
        }
        for (Entry<Point, Integer> e : board.numbers.entrySet())
            printer.draw(e.getKey(), e.getValue());
        logger.info(printer.toString());
    	
    }

    @Override
    public boolean answer(int n, Result result) {
        Printer printer = new Printer();
        printer.draw(board.box);
        for (Variable<? extends Box> v : variables)
            printer.draw(result.get(v));
        for (Entry<Point, Integer> e : board.numbers.entrySet())
            printer.draw(e.getKey(), e.getValue());
        logger.info(printer.toString());
        File svg = new File(input.getParentFile(), input.getName() + ".svg");
        try (Writer w = new OutputStreamWriter( new FileOutputStream(svg), "utf-8")) {
            w.write(printer.toSVG());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        new Shikaku().parse(args).solve();
    }
}
