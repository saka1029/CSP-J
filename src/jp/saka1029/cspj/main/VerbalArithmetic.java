package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.saka1029.cspj.problem.old.Constant;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Expression;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

public class VerbalArithmetic extends SolverMain {

	enum Operator {
		None(""), Plus("+"), Minus("-"), Equals("=");
		public final String name;
		Operator(String name) { this.name = name; }
	}
	
	static class Equation {

        class Factor {

            final Operator operator;
            final List<String> digits = new ArrayList<>();

            Factor(Operator operator) { this.operator = operator; }

            @Override
            public String toString() {
            	StringBuilder sb = new StringBuilder();
            	sb.append(operator.name);
            	for (int i = digits.size() - 1; i >= 0; --i)
            		sb.append(digits.get(i));
            	return sb.toString();
            }
            
            String toString(Result r) {
            	StringBuilder sb = new StringBuilder();
            	sb.append(operator.name);
            	for (int i = digits.size() - 1; i >= 0; --i)
            		sb.append(r.get(problem.variable(digits.get(i))));
            	return sb.toString();
            }
        }

		final Problem problem;
		List<Factor> factors;
		Map<String, Boolean> firsts;
		Map<String, Variable<Integer>> variables;

		public Equation(Problem problem, String s) {
			this.problem = problem;
			parse(s);
			check();
			constraints();
		}

		/*
		 * TODO: ABC+BCC...の場合 B:{1..9}となってしまう。
		 */
		private void parse(String s) {
			factors = new ArrayList<>();
			firsts = new HashMap<>();
			Factor factor = new Factor(Operator.None);
			factors.add(factor);
			for (int i = 0, size = s.length(); i < size; ++i) {
				String c = s.substring(i, i + 1);
				char ch = s.charAt(i);
				if (Character.isWhitespace(ch)) continue;
				switch (ch) {
				case '+': factors.add(factor = new Factor(Operator.Plus)); break;
				case '-': factors.add(factor = new Factor(Operator.Minus)); break;
				case '=': factors.add(factor = new Factor(Operator.Equals)); break;
				default:
					Boolean f = firsts.get(c);
					if (f == null) f = false;
					firsts.put(c, f || factor.digits.size() == 0);
					factor.digits.add(0, c);
					break;
				}
			}
		}
		
//		private static final ConstraintFunction<Boolean> vertical = args -> {
//			int sum = (int)args[0];
//			int c1 = (int)args[1];
//			int c0 = (int)args[2];
//			int total = 0;
//			for (int i = 3; i < args.length; ++i)
//				total += (int)args[i];
//			return sum + c1 * 10 == c0 + total;
//		};

		private void check() {
			if (firsts.size() > 10)
				throw new RuntimeException("too many variables");
			if (factors.size() < 2)
				throw new RuntimeException("too few factors");
			if (factors.get(factors.size() - 1).operator != Operator.Equals)
				throw new RuntimeException("last word must begin with '='");
		}

		private Variable<Integer> var(int row, int col) {
			Factor f = factors.get(row);
			return col >= f.digits.size() ? null : variables.get(f.digits.get(col));
		}

		private void constraints() {
			variables = new HashMap<>();
			Domain<Integer> firstDomain = Domain.range(1, 9);
			Domain<Integer> restDomain = Domain.range(0, 9);
			for (Entry<String, Boolean> e : firsts.entrySet())
				variables.put(e.getKey(),
					problem.variable(e.getKey(), e.getValue() ? firstDomain : restDomain));
			problem.allDifferent(variables.values());
			Constant<Integer> zero = problem.constant(0);
			int rows = factors.size();
			int cols = 0;
			for (Factor e : factors)
				cols = Math.max(cols, e.digits.size());
			List<Expression<Integer>> carrys = new ArrayList<>();
			Domain<Integer> carryDomain = Domain.range(0, rows - 2);
			carrys.add(zero);
			for (int i = 1; i < cols; ++i)
				carrys.add(problem.variable("c" + i, carryDomain));
			carrys.add(zero);
			
//			Constant<Integer> ten = problem.constant(10);
			for (int i = 0; i < cols; ++i) {

			/** with Helper **/
//				Expression<Integer> total = plus(var(rows - 1, i), mult(carrys.get(i + 1), ten));
//				Expression<Integer> sum = carrys.get(i);
//				for (int j = 0; j < rows - 1; ++j) {
//					Variable<Integer> v = var(j, i);
//					if (v != null)
//						sum = plus(sum, v);
//				}
//				eq(total, sum);

			/*** no Helper ***/
				Expression<Integer> total = problem.constraint(
					a -> (int)a[0] + (int)a[1] * 10, "%s + %s * 10",
					var(rows - 1,  i), carrys.get(i + 1));
				Expression<Integer> sum = carrys.get(i);
				for (int j = 0; j < rows - 1; ++j) {
					Variable<Integer> v = var(j, i);
					if (v != null)
						sum = problem.constraint(a -> (int)a[0] + (int)a[1], "+", sum, v);
				}
				problem.constraint(Problem.EQ, "==", total, sum);

			/** constraint between many variables (slow solution) ***/
//                List<Expression<Integer>> digits = new ArrayList<>();
//                digits.add(variables.get(factors.get(rows - 1).digits.get(i)));
//                digits.add(carrys.get(i + 1));
//                digits.add(carrys.get(i));
//                for (int j = 0; j < rows - 1; ++j) {
//                    Factor f = factors.get(j);
//                    Expression<Integer> e;
//                    if (i >= f.digits.size()) 
//                    	e = zero;
//                    else {
//                    	Variable<Integer> v = variables.get(f.digits.get(i));
//                    	e = f.operator == Operator.Minus ?
//                            problem.constraint(a -> -(int)a[0], "-", v) : v;
//                    }
//                    digits.add(e);
//                }
//                problem.constraint(vertical, "vertical", digits);

			}
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (Factor f : factors)
				sb.append(f);
			return sb.toString();
		}
		
		public String toString(Result r) {
			StringBuilder sb = new StringBuilder();
			for (Factor f : factors)
				sb.append(f.toString(r));
			return sb.toString();
		}
	}

//	static final String PROBLEM = "SEND+MORE=MONEY";
//	static final String PROBLEM = "TWO+TWO=FOUR";
//	static final String PROBLEM = "ONE+TWO+FOUR=SEVEN";
	String expression;
	public VerbalArithmetic expression(String a) { expression = a; return this; }
	
	public VerbalArithmetic() {
		addOption("-e", true, true, a -> expression(a));
	}

	Equation e;
	
	@Override
	public void define() throws IOException {
		e = new Equation(problem, expression);
		Log.info("equation = %s", e);
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		Log.info("*** answer %d ***", n);
//		for (Entry<Variable<?>, Object> e : result.entrySet())
//			if (!(e.getKey() instanceof Constraint<?>))
//                Log.info("%s = %s", e.getKey(), e.getValue());
		Log.info(e.toString(result));
		return true;
	}

	public static void main(String[] args) throws Exception {
		String[] as = {"-s", "b"};
		new VerbalArithmetic().parse(as).debug(Debug.ALL).solve();
	}

}
