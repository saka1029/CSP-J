package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.saka1029.cspj.main.SendMoreMoney;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.sat.minisat.MinisatSolver;

import org.junit.Test;

public class TestSatSolver {

	@Test
	public void test() throws IOException {
        Log.methodName();
        MinisatSolver solver = new MinisatSolver();
        SendMoreMoney m = new SendMoreMoney();
        assertEquals(1, m.solver(solver).debug(Debug.REDUCED).solve());
	}

}