package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Log;
import jp.saka1029.cspj.problem.StringFormatter;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class SimpleColoring extends SolverMain {

	enum Color { Red, Green, Blue }
	
	List<Variable<Color>> variables = new ArrayList<>();
	
	@Override
	public void define() throws IOException {
		Domain<Color> domain = Domain.of(Color.values());
		variables = new ArrayList<>();
		for (int i = 0; i < 4; ++i)
			variables.add(problem.variable("v" + i, domain));
		ne(variables.get(0), variables.get(1));
		ne(variables.get(0), variables.get(2));
		ne(variables.get(1), variables.get(2));
		ne(variables.get(1), variables.get(3));
		ne(variables.get(2), variables.get(3));
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		StringFormatter sf = new StringFormatter();
        sf.append("*** answer %d :", n);
        for (Variable<Color> v : variables)
            sf.append(" %s = %s", v, result.get(v));
        Log.info(sf.toString());
        assert result.get(variables.get(0)) == result.get(variables.get(3));
        return true;
	}

}
