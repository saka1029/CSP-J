package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.main.Akari;
import jp.saka1029.cspj.main.Australia;
import jp.saka1029.cspj.main.Filomino;
import jp.saka1029.cspj.main.Hamilton;
import jp.saka1029.cspj.main.SendMoreMoney;
import jp.saka1029.cspj.main.Shikaku;
import jp.saka1029.cspj.main.AC3;
import jp.saka1029.cspj.main.TileColorMatch2;
import jp.saka1029.cspj.main.推理パズル_4人で競争;
import jp.saka1029.cspj.main.春からみんな新生活;
import jp.saka1029.cspj.main.TileColorMatch;
import jp.saka1029.cspj.main.Komachi;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;
import jp.saka1029.cspj.solver.choco.ChocoSolver;
import jp.saka1029.cspj.solver.jacop.JacopSolver;
import jp.saka1029.cspj.solver.sat.MinisatSolver;
import jp.saka1029.cspj.solver.sat.Sat4jSolver;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestMain {

	static Logger logger = Logger.getLogger(TestMain.class.getName());
	
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n");
    }

	void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        logger.info("<<<<< " + s.getClassName() + "#" + s.getMethodName() + " >>>>>");
	}

    @DataPoints
    public static Solver[] solvers = { new BasicSolver(), /* new ChocoSolver(), */ new MinisatSolver(), new Sat4jSolver(), new JacopSolver(), };
    
//    @Theory
//    public void testSimpleColoring(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(6, new SimpleColoring().solver(solver).solve());
//    }
    
    @Test
    public void testAC3() throws IOException {
        methodName(); assertEquals(3, new AC3().solver(new BasicSolver()).solve());
    }
    
    @Theory
    public void testSendMoreMoney(Solver solver) throws IOException {
        methodName(); assertEquals(1, new SendMoreMoney().solver(solver).solve());
    }
    
//    @Test
//    public void testSendMoreMoneyChoco() throws IOException {
//        Log.methodName(); assertEquals(1, new SendMoreMoney().solver(new ChocoSolver()).debug(Debug.ALL).solve());
//    }
    
//    @Theory
//    public void testNumberLink0(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink0.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink1(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink1.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink2(Solver solver) throws IOException {
////    	if (solver instanceof BasicSolver) return;
////    	if (solver instanceof MinisatSolver) return;
////    	if (solver instanceof JacopSolver) return;
////    	if (solver instanceof Sat4jSolver) return;
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink2.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink3(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink3.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink5(Solver solver) throws IOException {
//    	if (solver instanceof BasicSolver) return;
//    	if (solver instanceof MinisatSolver) return;
//    	if (solver instanceof JacopSolver) return;
//    	if (solver instanceof Sat4jSolver) return;
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink5.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink10(Solver solver) throws IOException {
////    	if (solver instanceof BasicSolver) return;
//    	if (solver instanceof MinisatSolver) return;
//    	if (solver instanceof JacopSolver) return;
//    	if (solver instanceof Sat4jSolver) return;
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink10.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLink11(Solver solver) throws IOException {
////    	if (solver instanceof BasicSolver) return;
//    	if (solver instanceof MinisatSolver) return;
//    	if (solver instanceof JacopSolver) return;
//    	if (solver instanceof Sat4jSolver) return;
//        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink11.txt").solver(solver).solve());
//    }
//    
//    @Theory
//    public void testNumberLinkNoSet2(Solver solver) throws IOException {
////    	if (solver instanceof BasicSolver) return;
//    	if (solver instanceof MinisatSolver) return;
//    	if (solver instanceof JacopSolver) return;
//    	if (solver instanceof Sat4jSolver) return;
//        Log.methodName(); assertEquals(1, new NumberLinkNoSet().input("data/numberlink2.txt").solver(solver).solve());
//    }
//
//    @Theory
//    public void testSudoku1(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(1, new Sudoku().input("data/sudoku1.txt").solver(solver).solve());
//    }
//
//    @Theory
//    public void testSudoku2(Solver solver) throws IOException {
//        Log.methodName(); assertEquals(1, new Sudoku().input("data/sudoku2.txt").solver(solver).solve());
//    }

    @Theory
    public void testShikaku2(Solver solver) throws IOException {
        methodName(); assertEquals(1, new Shikaku().input("data/cis2.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku003(Solver solver) throws IOException {
        methodName(); assertEquals(1, new Shikaku().input("data/cis003.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku003many(Solver solver) throws IOException {
        if (!(solver instanceof BasicSolver)) return;
//        methodName(); assertEquals(8, new Shikaku().input("data/cis003-many.txt").solver(solver).solve());
        methodName(); assertEquals(12, new Shikaku().input("data/cis003-4.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku53(Solver solver) throws IOException {
        methodName(); assertEquals(1, new Shikaku().input("data/cis53.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku54(Solver solver) throws IOException {
        methodName(); assertEquals(1, new Shikaku().input("data/cis54.txt").solver(solver).solve());
    }

    @Theory
    public void testFilomino3(Solver solver) throws IOException {
        methodName(); assertTrue(new Filomino().input("data/fillomino3.txt").solver(solver).solve() > 0);
    }
//
////    @Theory
////    public void testFilomino10(Solver solver) throws IOException {
////        Log.methodName(); assertTrue(new Filomino().input("data/fillomino10.txt").solver(solver).solve() > 0);
////    }

    @Test
    public void test春からみんな新生活() throws IOException {
        methodName(); assertEquals(1, new 春からみんな新生活().solver(new BasicSolver()).solve());
    }

    @Test
    public void test推理パズル_4人で競争() throws IOException {
        methodName(); assertEquals(1, new 推理パズル_4人で競争().solver(new BasicSolver()).solve());
    }

    @Ignore
    @Theory
    public void testHamilton(Solver solver) throws IOException {
        methodName(); assertTrue(new Hamilton().solver(solver).solve() >= 1);
    }

    @Ignore
    @Theory
    public void testTileColorMatch(Solver solver) throws IOException {
    	if (solver instanceof ChocoSolver) return;
        methodName(); assertTrue(new TileColorMatch().solver(solver).solve() >= 1);
    }

    @Ignore
    @Theory
    public void testTileColorMatch2(Solver solver) throws IOException {
    	if (solver instanceof ChocoSolver) return;
        methodName(); assertTrue(new TileColorMatch2().solver(solver).solve() >= 1);
    }

//    @Theory
//    public void testNQueen(Solver solver) throws IOException {
//    	Log.methodName(); assertTrue(new NQueen().number(8).solver(solver).solve() > 0);
//    }

    @Theory
    public void testAustralia(Solver solver) throws IOException {
    	methodName(); assertEquals(18, new Australia().solver(solver).solve());
    }

//    @Ignore
//    @Test
//    public void testBenchmark() throws IOException {
//    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb30-15-1.csp").solver(new JacopSolver()).solve());
////    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb35-17-1.csp").solver(new JacopSolver()).solve());
////    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb40-19-1.csp").solver(new BasicSolver()).solve());
//    }
//    
    @Theory
    public void testKomachi(Solver solver) throws IOException {
    	methodName(); assertTrue(new Komachi().solver(solver).solve() > 0);
    }
//    
//    @Theory
//    public void testVerbalArithmetic(Solver solver) throws IOException {
//    	Log.methodName();
////    	String ex = "send+more=money";
////    	String ex = "two+two=four";
////    	String ex = "aaa+aaa+aaa+aaa=bbbc";
////    	String ex = "twelve+twenty=answer";
////    	String ex = "BLACK+GREEN=ORANGE";
////    	String ex = "SIX+SEVEN+SEVEN=TWENTY";
//    	String ex = "ONE+TWO+FIVE+NINE+ELEVEN+TWELVE+FIFTY=NINETY";
//    	assertTrue(new VerbalArithmetic().expression(ex).solver(solver).solve() > 0);
//    }
//
    @Theory
    public void testAkari1(Solver solver) throws IOException {
    	methodName(); assertTrue(new Akari().input("data/akari1.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari2(Solver solver) throws IOException {
    	methodName(); assertTrue(new Akari().input("data/akari2.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari3(Solver solver) throws IOException {
    	methodName(); assertTrue(new Akari().input("data/akari3.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari7(Solver solver) throws IOException {
    	if (solver instanceof ChocoSolver) return;
    	methodName(); assertTrue(new Akari().input("data/akari7.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari10(Solver solver) throws IOException {
    	if (solver instanceof ChocoSolver) return;
    	methodName(); assertTrue(new Akari().input("data/akari10.txt").solver(solver).solve() > 0);
    }
}
