package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.solver.basic.Vars;

import org.junit.Test;

public class TestVars {

	static final Logger logger = Logger.getLogger(TestVars.class.getName());
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n"); }

	@Test
	public void testCombination() {
		List<List<Integer>> r = Vars.of(3, Domain.range(0,  5))
            .allDifferent()
            .forAllNeighbors((x, y) -> x < y)
            .solve()
            .peek(x -> logger.info(x.toString()))
            .collect(Collectors.toList());
		assertEquals(20, r.size());
		logger.info(r.toString());
	}
	
	static int square(int n) { return n * n; }

	@Test
	public void testPitagolas() {
		Vars.of(3, Domain.range(1, 100))
			.constraint((x, y, z) -> x < y && y < z && square(x) + square(y) == square(z))
			.solve()
			.sorted(Vars.Compare())
            .forEach(x -> {
            	int w = square(x.get(2));
            	logger.info(x + " " + w);
            });
	}
	
	static int cube(int n) { return n * n * n; }

	@Test
	public void testRamanujan() {
		Vars.of(4, Domain.range(1, 100))
			.constraint((x, y, z, u) -> x < y && y < z && z < u && cube(x) + cube(u) == cube(y) + cube(z))
			.solve()
            .forEach(x -> {
            	int left = cube(x.get(0)) + cube(x.get(3));
            	logger.info(x + " " + left);
            });
	}

	@Test
	public void testNQueens() {
		int N = 8;
		Vars.of(N, Domain.range(1, N))
			.forAllPairs((ix, x, iy, y) -> x != y && x + (iy - ix) != y && x - (iy - ix) != y)
			.solve()
            .forEach(x -> {
            	logger.info(x.toString());
            });
	}

	@Test
	public void testConstraint() {
		Vars.of(3, Domain.range(1, 3))
			.allDifferent()
//			.constraint(x -> x <= 1)		// OK
			.constraint((List<Integer> x) -> x.get(1) <= 1)	// OK
//			.constraint((x) -> x.get(0) <= 1)	// NG
			.solve()
            .forEach(x -> {
            	logger.info(x.toString());
            });
	}

}
