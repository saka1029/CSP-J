package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.saka1029.cspj.problem.ConstraintFunction;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class Hamilton2 extends SolverMain {

    void add(List<Set<String>> edges, String from, String... tos) {
        for (String to : tos) {
            Set<String> edge = new HashSet<>();
            edge.add(from);
            edge.add(to);
            edges.add(edge);
        }
    }

    List<Set<String>> edges() {
        List<Set<String>> edges = new ArrayList<>();
        add(edges, "a0", "a1", "a4", "b0");
        add(edges, "a1", "a2", "b1");
        add(edges, "a2", "a3", "b2");
        add(edges, "a3", "a4", "b3");
        add(edges, "a4", "b4");
        add(edges, "b0", "c0", "c4");
        add(edges, "b1", "c0", "c1");
        add(edges, "b2", "c1", "c2");
        add(edges, "b3", "c2", "c3");
        add(edges, "b4", "c3", "c4");
        add(edges, "c0", "d0");
        add(edges, "c1", "d1");
        add(edges, "c2", "d2");
        add(edges, "c3", "d3");
        add(edges, "c4", "d4");
        add(edges, "d0", "d1");
        add(edges, "d1", "d2");
        add(edges, "d2", "d3");
        add(edges, "d3", "d4");
        add(edges, "d4", "d0");
        return edges;
    }
    
    List<Variable<Boolean>> variables = new ArrayList<>();
    
    @Override
    public void define() throws IOException {
        List<Set<String>> edges = edges();
        Domain<Boolean> bool = Domain.of(true, false);
        for (Set<String> edge : edges)
            variables.add(problem.variable(edge.toString(), bool));
        ConstraintFunction<Boolean> f =
            a -> {
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0, size = a.length; i < size; ++i)
                    if ((boolean)a[i])
                        for (String n : edges.get(i)) {
                            Integer v = map.get(n);
                            if (v == null)
                                v = 0;
                            map.put(n, v + 1);
                        }
                for (int n : map.values())
                    if (n != 2)
                        return false;
                return true;
            };
        problem.constraint(f, "checkAll", variables);
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        Log.info(result);
        return false;
    }

}
