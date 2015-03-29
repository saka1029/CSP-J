package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.saka1029.cspj.main.old.AC3;
import jp.saka1029.cspj.main.old.Akari;
import jp.saka1029.cspj.main.old.Australia;
import jp.saka1029.cspj.main.old.Benchmark;
import jp.saka1029.cspj.main.old.Filomino;
import jp.saka1029.cspj.main.old.Hamilton;
import jp.saka1029.cspj.main.old.Hamilton2;
import jp.saka1029.cspj.main.old.Komachi;
import jp.saka1029.cspj.main.old.NQueen;
import jp.saka1029.cspj.main.old.NumberLink;
import jp.saka1029.cspj.main.old.NumberLinkNoSet;
import jp.saka1029.cspj.main.old.SendMoreMoney;
import jp.saka1029.cspj.main.old.Shikaku;
import jp.saka1029.cspj.main.old.SimpleColoring;
import jp.saka1029.cspj.main.old.Sudoku;
import jp.saka1029.cspj.main.old.TileColorMatch;
import jp.saka1029.cspj.main.old.VerbalArithmetic;
import jp.saka1029.cspj.main.old.春からみんな新生活;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.solver.old.Solver;
import jp.saka1029.cspj.solver.old.basic.BasicSolver;
import jp.saka1029.cspj.solver.old.jacop.JacopSolver;
import jp.saka1029.cspj.solver.old.sat.minisat.MinisatSolver;
import jp.saka1029.cspj.solver.old.sat.sat4j.Sat4jSolver;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestMainOld {

    @DataPoints
    public static Solver[] solvers = { new BasicSolver(), new MinisatSolver(), new JacopSolver(), new Sat4jSolver(), /* new ChocoSolver(), */};
    
    @Theory
    public void testSimpleColoring(Solver solver) throws IOException {
        Log.methodName(); assertEquals(6, new SimpleColoring().solver(solver).solve());
    }
    
    @Theory
    public void testAC3(Solver solver) throws IOException {
        Log.methodName(); assertEquals(3, new AC3().solver(solver).solve());
    }
    
    @Theory
    public void testSendMoreMoney(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new SendMoreMoney().solver(solver).solve());
    }
    
//    @Test
//    public void testSendMoreMoneyChoco() throws IOException {
//        Log.methodName(); assertEquals(1, new SendMoreMoney().solver(new ChocoSolver()).debug(Debug.ALL).solve());
//    }
    
    @Theory
    public void testNumberLink0(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink0.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink1(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink1.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink2(Solver solver) throws IOException {
//    	if (solver instanceof BasicSolver) return;
//    	if (solver instanceof MinisatSolver) return;
//    	if (solver instanceof JacopSolver) return;
//    	if (solver instanceof Sat4jSolver) return;
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink2.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink3(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink3.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink5(Solver solver) throws IOException {
    	if (solver instanceof BasicSolver) return;
    	if (solver instanceof MinisatSolver) return;
    	if (solver instanceof JacopSolver) return;
    	if (solver instanceof Sat4jSolver) return;
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink5.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink10(Solver solver) throws IOException {
//    	if (solver instanceof BasicSolver) return;
    	if (solver instanceof MinisatSolver) return;
    	if (solver instanceof JacopSolver) return;
    	if (solver instanceof Sat4jSolver) return;
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink10.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink11(Solver solver) throws IOException {
//    	if (solver instanceof BasicSolver) return;
    	if (solver instanceof MinisatSolver) return;
    	if (solver instanceof JacopSolver) return;
    	if (solver instanceof Sat4jSolver) return;
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink11.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLinkNoSet2(Solver solver) throws IOException {
//    	if (solver instanceof BasicSolver) return;
    	if (solver instanceof MinisatSolver) return;
    	if (solver instanceof JacopSolver) return;
    	if (solver instanceof Sat4jSolver) return;
        Log.methodName(); assertEquals(1, new NumberLinkNoSet().input("data/numberlink2.txt").solver(solver).solve());
    }

    @Theory
    public void testSudoku1(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new Sudoku().input("data/sudoku1.txt").solver(solver).solve());
    }

    @Theory
    public void testSudoku2(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new Sudoku().input("data/sudoku2.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku2(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new Shikaku().input("data/cis2.txt").solver(solver).solve());
    }

    @Theory
    public void testShikaku53(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new Shikaku().input("data/cis53.txt").solver(solver).solve());
    }

    @Theory
    public void testFilomino3(Solver solver) throws IOException {
        Log.methodName(); assertTrue(new Filomino().input("data/fillomino3.txt").solver(solver).solve() > 0);
    }

//    @Theory
//    public void testFilomino10(Solver solver) throws IOException {
//        Log.methodName(); assertTrue(new Filomino().input("data/fillomino10.txt").solver(solver).solve() > 0);
//    }

    @Theory
    public void test春からみんな新生活(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new 春からみんな新生活().solver(solver).solve());
    }

    @Theory
    public void testHamilton(Solver solver) throws IOException {
        Log.methodName(); assertTrue(new Hamilton().solver(solver).solve() >= 1);
    }

    @Ignore
    @Theory
    public void testHamilton2(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new Hamilton2().solver(solver).solve());
    }

    @Theory
    public void testTileColorMatch(Solver solver) throws IOException {
    	if (solver instanceof JacopSolver) return;
        Log.methodName(); assertTrue(new TileColorMatch().solver(solver).solve() >= 1);
    }

    @Theory
    public void testNQueen(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new NQueen().number(8).solver(solver).solve() > 0);
    }

    @Theory
    public void testAustralia(Solver solver) throws IOException {
    	Log.methodName(); assertEquals(18, new Australia().solver(solver).solve());
    }

    @Ignore
    @Test
    public void testBenchmark() throws IOException {
    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb30-15-1.csp").solver(new JacopSolver()).solve());
//    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb35-17-1.csp").solver(new JacopSolver()).solve());
//    	Log.methodName(); assertEquals(1, new Benchmark().input("data/frb40-19-1.csp").solver(new BasicSolver()).solve());
    }
    
    @Theory
    public void testKomachi(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Komachi().solver(solver).solve() > 0);
    }
    
    @Theory
    public void testVerbalArithmetic(Solver solver) throws IOException {
    	Log.methodName();
//    	String ex = "send+more=money";
//    	String ex = "two+two=four";
//    	String ex = "aaa+aaa+aaa+aaa=bbbc";
//    	String ex = "twelve+twenty=answer";
//    	String ex = "BLACK+GREEN=ORANGE";
//    	String ex = "SIX+SEVEN+SEVEN=TWENTY";
    	String ex = "ONE+TWO+FIVE+NINE+ELEVEN+TWELVE+FIFTY=NINETY";
    	assertTrue(new VerbalArithmetic().expression(ex).solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari1(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari1.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari2(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari2.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari3(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari3.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari7(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari7.txt").solver(solver).solve() > 0);
    }

    @Theory
    public void testAkari10(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari10.txt").solver(solver).solve() > 0);
    }
}
