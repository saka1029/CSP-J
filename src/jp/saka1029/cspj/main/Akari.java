package jp.saka1029.cspj.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.geometry.Board;
import jp.saka1029.cspj.geometry.Matrix;
import jp.saka1029.cspj.geometry.Point;
import jp.saka1029.cspj.geometry.Printer;
import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Akari extends SolverMain {

	static final Logger logger = Logger.getLogger(Akari.class.getName());
	
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
	Matrix<Variable<Cell>> variables;
	
	@Override
	public void define() throws IOException {
		Domain<Cell> vacants = Domain.of(Cell.VACANTS);
		Variable<Cell> block = problem.constant(Cell.BLOCK);
		board = new Board(input);
		Point size = board.box.size;
		variables = new Matrix<>(size.x, size.y);
		for (int y = 0; y < size.y; ++y)
			for (int x = 0; x < size.x; ++x) {
				Variable<Cell> e;
				Integer n = board.numbers.get(new Point(x, y));
				if (n == null)
					e = problem.variable(String.format("%d@%d", x, y), vacants);
				else if (n == 9)
					e = block;
				else
					e = problem.constant(Cell.number(n));
				variables.set(x, y, e);
			}
		logger.info(board.toString());
		for (int y = -1; y < size.y; ++y)
            for (int x = -1; x < size.x; ++x) {
                constraint("isValidDown", (a, b) -> a.isValidDown(b),
                    variables.get(x, y, block), variables.get(x, y + 1, block));
                constraint("isValidRight", (a, b) -> a.isValidRight(b),
                    variables.get(x, y, block), variables.get(x + 1, y, block));
            }
		for (int y = 0; y < size.y; ++y)
            for (int x = 0; x < size.x; ++x) {
            	Integer n = board.numbers.get(new Point(x, y));
            	if (n == null || n == 9) continue;
                Variable<Cell> center = variables.get(x, y);
                Variable<Cell> left = variables.get(x - 1, y, block);
                Variable<Cell> right = variables.get(x + 1, y, block);
                Variable<Cell> up = variables.get(x, y - 1, block);
                Variable<Cell> down = variables.get(x, y + 1, block);
                    constraint("isValidLightCount",
                    	(c, l, r, u, d) -> c.isValidLightCount(l, r, u, d),
                    	center, left, right, up, down);
            }
	}

	@Override
	public boolean answer(int no, Result result) throws IOException {
		Printer printer = new Printer();
		printer.draw(board.box);
		Point size = board.box.size;
		for (int y = 0; y < size.y; ++y)
			for (int x = 0; x < size.x; ++x) {
				Cell c = result.get(variables.get(x, y));
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
		logger.info(printer.toString());
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
