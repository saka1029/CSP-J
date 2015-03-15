package jp.saka1029.cspj.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.old.Constant;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Expression;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Akari extends SolverMain {

	/**
	 * 生成するインスタンスを制約しているので、hashCodeやequalsの実装を省略している。
	 */
	static class Cell {

		private static final Number[] NUMBERS = {
			new Number(0), new Number(1), new Number(2), new Number(3), new Number(4)
		};
		/**
		 * 数字のあるブロックの値
		 */
		public static final Number number(int n) { return NUMBERS[n]; }
		/**
		 * 数字のないブロックの値
		 */
		public static final Cell BLOCK = new Cell(false, false, false, false);
		/**
		 * ライトが点いているセルの値
		 */
		public static final Cell ON = new Cell(true, true, true, true);
		/**
		 * ライトが点くか光の通り道になるセルの値の集合
		 */
		public static final Cell[] VACANTS = {
			ON,
			new Cell(true, false, true, false),
			new Cell(true, false, false, true),
			new Cell(true, false, false, false),
			new Cell(false, true, true, false),
			new Cell(false, true, false, true),
			new Cell(false, true, false, false),
			new Cell(false, false, true, false),
			new Cell(false, false, false, true),
		};

		public final boolean up, down, left, right;

		public boolean isOn() { return up && down && left && right; }
		public boolean isBlock() { return !up && !down && !left && !right; }
		public boolean isNumber() { return false; }

		private Cell(boolean up, boolean down, boolean left, boolean right) {
			this.up = up; this.down = down; this.left = left; this.right = right;
		}

		public boolean isValidLightCount(Cell top, Cell bottom, Cell left, Cell right) { return true; }

		public boolean isValidDown(Cell that) {
			boolean thisUp = this.isOn() ? false : this.isBlock() ? that.up : this.up;
			boolean thatDown = that.isOn() ? false : that.isBlock() ? this.down : that.down;
			return thisUp == that.up && this.down == thatDown;
		}

		public boolean isValidRight(Cell that) {
			boolean thisLeft = this.isOn() ? false : this.isBlock() ? that.left : this.left;
			boolean thatRight = that.isOn() ? false : that.isBlock() ? this.right : that.right;
			return thisLeft == that.left && this.right == thatRight;
		}

		@Override public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Cell[");
			if (up) sb.append("U");
			if (down) sb.append("D");
			if (left) sb.append("L");
			if (right) sb.append("R");
			sb.append("]");
			return sb.toString();
		}

	}
	
	static class Number extends Cell {

		public final int n;

		private Number(int n) { super(false, false, false, false); this.n = n; }

		@Override public boolean isNumber() { return true; }

		private int i(boolean b) { return b ? 1 : 0; }
		
		@Override public boolean isValidLightCount(Cell top, Cell bottom, Cell left, Cell right) {
			return i(top.isOn()) + i(bottom.isOn()) + i(left.isOn()) + i(right.isOn()) == n;
		}

		@Override public String toString() { return String.format("Number[%d]", n); }
	}
	
	File input;
	public Akari input(String input) { this.input = new File(input); return this; }
	
	public Akari() {
		addOption("-i", true, true, x -> input(x));
	}
	
	Board board;
	Matrix<Expression<Cell>> expressions;
	
	@Override
	public void define() throws IOException {
		Domain<Cell> vacants = Domain.of(Cell.VACANTS);
		Constant<Cell> block = problem.constant(Cell.BLOCK);
		board = new Board(input);
		Point size = board.box.size;
		expressions = new Matrix<>(size.x, size.y);
		for (int y = 0; y < size.y; ++y)
			for (int x = 0; x < size.x; ++x) {
				Expression<Cell> e;
				Integer n = board.numbers.get(new Point(x, y));
				if (n == null)
					e = problem.variable(String.format("%d@%d", x, y), vacants);
				else if (n == 9)
					e = block;
				else
					e = problem.constant(Cell.number(n));
				expressions.set(x, y, e);
			}
		Log.info(board);
		for (int y = -1; y < size.y; ++y)
            for (int x = -1; x < size.x; ++x) {
                problem.constraint(a -> ((Cell)a[0]).isValidDown((Cell)a[1]), "isValidDown",
                    expressions.get(x, y, block), expressions.get(x, y + 1, block));
                problem.constraint(a -> ((Cell)a[0]).isValidRight((Cell)a[1]), "isValidRight",
                    expressions.get(x, y, block), expressions.get(x + 1, y, block));
            }
		for (int y = 0; y < size.y; ++y)
            for (int x = 0; x < size.x; ++x) {
            	Integer n = board.numbers.get(new Point(x, y));
            	if (n == null || n == 9) continue;
                List<Expression<Cell>> args = new ArrayList<>();
                args.add(expressions.get(x, y));
                    args.add(expressions.get(x - 1, y, block));
                    args.add(expressions.get(x + 1, y, block));
                    args.add(expressions.get(x, y - 1, block));
                    args.add(expressions.get(x, y + 1, block));
                    problem.constraint(
                        a -> ((Cell)a[0]).isValidLightCount((Cell)a[1], (Cell)a[2], (Cell)a[3], (Cell)a[4]),
                        "isValidLightCount", args);
            }
	}

	Cell get(Result result, Expression<Cell> e) {
		if (e instanceof Constant<?>)
			return e.domain.first();
		else if (e instanceof Variable<?>)
			return (Cell)result.get((Variable<Cell>)e);
		else
			throw new IllegalArgumentException("unknown expression: " + e);
	}

	@Override
	public boolean answer(int no, Result result) throws IOException {
		Printer printer = new Printer();
		printer.draw(board.box);
		Point size = board.box.size;
		for (int y = 0; y < size.y; ++y)
			for (int x = 0; x < size.x; ++x) {
				Cell c = result.get(expressions.get(x, y));
				if (c.isNumber())
					printer.draw(x, y, ((Number)c).n);
				else if (c.isBlock())
					printer.draw(x, y, "#");
				else if (c.isOn())
					printer.draw(x, y, "*");
                else {
                    String s = "";
                    if ((c.up || c.down) && (c.left || c.right)) s = "+";
                    else if (c.up || c.down) s = "|";
                    else if (c.left || c.right) s = "-";
                    printer.draw(x, y, s);
                }
			}
		Log.info(printer);
//		try {
//			printer.writeSVG(new File(input.getParentFile(), input.getName() + "." + solver.getClass().getSimpleName() + ".svg"));
//		} catch (IOException e) {
//			Log.info(e);
//		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		new Akari().parse(args).solve();
	}

}
