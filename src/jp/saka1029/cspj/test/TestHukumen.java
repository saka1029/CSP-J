package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestHukumen {

	static Logger logger = Logger.getLogger(TestMain.class.getName());
	
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n");
    }


    /**
     * □に入れる数は？
     * □×□＝□÷□＝□＋□－□
     * □の中に１～７までの数字を一つずつ入れて式を完成させましょう。
     */
    @Test
    public void test() {
        Problem problem = new Problem();
        Domain<Integer> num = Domain.range(1, 7);
        Variable<Integer> A = problem.variable("A", num);
        Variable<Integer> B = problem.variable("B", num);
        Variable<Integer> C = problem.variable("C", num);
        Variable<Integer> D = problem.variable("D", num);
        Variable<Integer> E = problem.variable("E", num);
        Variable<Integer> F = problem.variable("F", num);
        Variable<Integer> G = problem.variable("G", num);
        allDifferent(A, B, C, D, E, F, G);

//        problem.constraint("equation1", (a, b, c, d) -> a * b == c / d, A, B, C, D);
//        problem.constraint("equation2", (a, b, e, f, g) -> a * b == e + f - g, A, B, E, F, G);

//        problem.constraint("equation0", (a, b, c, d, e, f, g) -> {
//            int x = a + b;
//            int y = c / d;
//            int z = e + f - g;
//            return x == y && x == z;
//        }, A, B, C, D, E, F, G);
        
        Variable<Integer> X = problem.variable("X", "X", (a, b) -> a + b, A, B);
        Variable<Integer> Y = problem.variable("Y", "Y", (c, d) -> c / d, C, D);
        Variable<Integer> Z = problem.variable("Z", "Z", (e, f, g) -> e + f - g, E, F, G);
        problem.constraint("equation", (x, y, z) -> x == y && x == z, X, Y, Z);

        Solver solver = new BasicSolver();
        solver.solve(problem, new Answer() {
            @Override
            public boolean answer(Result result) {
                System.out.println(result);
                return true;
            }
        });
    }
    
    @Test
    public void testCube() {
        Problem problem = new Problem();
        Domain<Integer> domain = Domain.range(1, 8);
        Variable<Integer> A = problem.variable("A", Domain.of(1));
        Variable<Integer> B = problem.variable("B", domain);
        Variable<Integer> C = problem.variable("C", domain);
        Variable<Integer> D = problem.variable("D", domain);
        Variable<Integer> E = problem.variable("E", domain);
        Variable<Integer> F = problem.variable("F", domain);
        Variable<Integer> G = problem.variable("G", domain);
        Variable<Integer> H = problem.variable("H", domain);
        allDifferent(A, B, C, D, E, F, G, H);
        problem.constraint("sum", (a, b, c, d) -> a + b + c + d == 18, A, B, C, D);
        problem.constraint("sum", (a, b, f, e) -> a + b + f + e == 22, A, B, F, E);
        Solver solver = new BasicSolver();
        solver.solve(problem, new Answer() {
            @Override
            public boolean answer(Result result) {
                System.out.println(result + " E+F+G+H=" + (result.get(E) + result.get(F) + result.get(G) + result.get(H)));
                return true;
            }
        });
    }

}
