package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jp.saka1029.cspj.problem.Helper.*;

import org.junit.Test;

import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

public class TestTwoSameVariablesBug {

    @Test
    public void test() {
        Problem problem = new Problem();
        Domain<Integer> domain = Domain.range(1, 3);
        Variable<Integer> a = problem.variable("a", domain);
        Variable<Integer> b = problem.variable("b", domain);
        constraint("lessThan", (x, y) -> x < y, a, a);
        constraint("lessThan", (x, y) -> x < y, a, b);
        Solver solver = new BasicSolver();
        solver.solve(problem, result -> {
            System.out.println(result);
            return true;
        });
    }
    
    /**
     * variableSetとvariableIndexMapの初期化テスト
     */
    @Test
    public void testVariableSet() {
        Problem p = new Problem();
        Domain<Integer> d = Domain.of(1, 2);
        Variable<Integer> a = p.variable("a", d);
        Variable<Integer> b = p.variable("b", d);
        List<Variable<?>> variables = Arrays.asList(a, b, a, b);
        List<Variable<?>> variableSet = new ArrayList<>();
        int[] variableIndexMap = new int[variables.size()];
        int i = 0;
        for (Variable<?> v : variables) {
            int index = variableSet.indexOf(v);
            variableIndexMap[i++] = index;
            if (index == -1)
                variableSet.add(v);
        }
        assertEquals(Arrays.asList(a, b), variableSet);
        assertArrayEquals(new int[] {-1, -1, 0, 1}, variableIndexMap);
    }

}
