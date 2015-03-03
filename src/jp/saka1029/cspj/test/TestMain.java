package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;

import jp.saka1029.cspj.main.Akari;
import jp.saka1029.cspj.main.Australia;
import jp.saka1029.cspj.main.Benchmark;
import jp.saka1029.cspj.main.Filomino;
import jp.saka1029.cspj.main.Hamilton;
import jp.saka1029.cspj.main.Hamilton2;
import jp.saka1029.cspj.main.Komachi;
import jp.saka1029.cspj.main.NQueen;
import jp.saka1029.cspj.main.NumberLink;
import jp.saka1029.cspj.main.SendMoreMoney;
import jp.saka1029.cspj.main.Shikaku;
import jp.saka1029.cspj.main.Sudoku;
import jp.saka1029.cspj.main.TileColorMatch;
import jp.saka1029.cspj.main.TriangleTileColorMatch;
import jp.saka1029.cspj.main.VerbalArithmetic;
import jp.saka1029.cspj.main.春からみんな新生活;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;
import jp.saka1029.cspj.solver.jacop.JacopSolver;
import jp.saka1029.cspj.solver.sat.minisat.MinisatSolver;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class TestMain {

    @DataPoints
    public static Solver[] solvers = { new BasicSolver(), new MinisatSolver(), new JacopSolver()};
    
    @Theory
    public void testSendMoreMoney(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new SendMoreMoney().solver(solver).debug(Debug.ALL).solve());
    }
    
//    @Test
//    public void testSendMoreMoneyMinisat() throws IOException {
//        Log.methodName(); assertEquals(1, new SendMoreMoney().solver(new MinisatSolver(Debug.ALL)).solve());
//    }
    
    @Theory
    public void testNumberLink1(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink1.txt").solver(solver).solve());
    }
    
    @Theory
    public void testNumberLink0(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new NumberLink().input("data/numberlink0.txt").solver(solver).solve());
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
        Log.methodName(); assertTrue(new TileColorMatch().solver(solver).solve() >= 1);
    }

    @Theory
    public void testTriangleTileColorMatch(Solver solver) throws IOException {
        Log.methodName(); assertEquals(1, new TriangleTileColorMatch().solver(solver).solve());
    }
    
    @Theory
    public void testNQueen(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new NQueen().number(8).solver(solver).solve() > 0);
    }

    @Theory
    public void testAustralia(Solver solver) throws IOException {
    	Log.methodName(); assertEquals(18, new Australia().solver(solver).debug(Debug.ALL).solve());
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
    	assertTrue(new VerbalArithmetic().expression(ex).solver(solver).debug("Reduced").solve() > 0);
    }

    @Theory
    public void testAkari1(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari1.txt").solver(solver).debug("Reduced").solve() > 0);
    }

    @Theory
    public void testAkari2(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari2.txt").solver(solver).debug("Reduced").solve() > 0);
    }

    @Theory
    public void testAkari3(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari3.txt").solver(solver).debug("Reduced").solve() > 0);
    }

    @Theory
    public void testAkari7(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari7.txt").solver(solver)/*.debug("Reduced")*/.solve() > 0);
    }

    @Theory
    public void testAkari10(Solver solver) throws IOException {
    	Log.methodName(); assertTrue(new Akari().input("data/akari10.txt").solver(solver)/*.debug("Reduced")*/.solve() > 0);
    }
}
