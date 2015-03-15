package jp.saka1029.cspj.problem.old.singletype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Problem<T> {

	public final ConstraintPredicate<T> EQ = a -> a.get(0).equals(a.get(1));
	public final ConstraintPredicate<T> NE = a -> !a.get(0).equals(a.get(1));
	
	public Constant<T> constant(T e) {
		return new Constant<>(e);
	}
	
	final List<Constraint<T>> constraints = new ArrayList<>(); 
	public List<Constraint<T>> constraints() { return Collections.unmodifiableList(constraints); }

	private Constraint<T> add(Constraint<T> c) {
        constraints.add(c);
		return c;
	}

	public Constraint<T> constraint(String name, ConstraintPredicate<T> predicate, Collection<? extends Expression<T>> arguments) {
		int no = variables.size();
		if (name == null)
			name = "_" + no;
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("constraint name cannot start with _");
		return add(new Constraint<>(constraints.size(), name, predicate, arguments));
	}

	@SafeVarargs
	public final Constraint<T> constraint(String name, ConstraintPredicate<T> predicate, Expression<T>... arguments) {
		return constraint(name, predicate, Arrays.asList(arguments));
	}

	public void forEachPairs(String name, ConstraintPredicate<T> predicate, Collection<? extends Expression<T>> arguments) {
		int i = 0;
		for (Expression<T> ii : arguments) {
			int j = 0;
			for (Expression<T> jj : arguments) {
				if (i < j)
                    constraint(name, predicate, ii, jj);
				++j;
			}
			++i;
		}
	}

	public void forEachPairs(ConstraintPredicate<T> predicate, Collection<? extends Expression<T>> arguments) {
		forEachPairs(null, predicate, arguments);
	}

	@SafeVarargs
	public final void forEachPairs(String name, ConstraintPredicate<T> predicate, Expression<T>... arguments) {
		forEachPairs(name, predicate, Arrays.asList(arguments));
	}
	
	public void allDifferent(List<Expression<T>> arguments) {
		forEachPairs("!=", NE, arguments);
	}

	@SafeVarargs
	public final void allDifferent(Expression<T>... arguments) {
		forEachPairs("!=", NE, arguments);
	}

	final List<Variable<T>> variables = new ArrayList<>();
	public List<Variable<T>> variables() { return Collections.unmodifiableList(variables); }
	final Map<String, Variable<T>> variableNames = new HashMap<>();

	private Variable<T> add(Variable<T> v) {
		if (variableNames.containsKey(v.name))
			throw new IllegalArgumentException("variable name (" + v.name + ") duplicated");
		variableNames.put(v.name, v);
        variables.add(v);
        return v;
	}
	
	public Variable<T> variable(String name, Domain<T> domain) {
		int no = variables.size();
		if (name == null)
			name = "_" + no;
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("variable name cannot start with _");
		return add(new Variable<>(no, name, domain));
	}
	
	public Variable<T> variable(Domain<T> domain) {
		return variable(null, domain);
	}

	static class Derivation<T> {
		
		final int size;
		final DerivationFunction<T> derivation;
		final List<T> argsValue = new ArrayList<>();
		final List<Domain<T>> argsDomain = new ArrayList<>();
		final Domain.Builder<T> builder = new Domain.Builder<>();
		
		Derivation(DerivationFunction<T> derivation, Collection<? extends Expression<T>> argsExpression) {
			this.size = argsExpression.size();
			this.derivation = derivation;
			for (Expression<T> e : argsExpression) {
				argsDomain.add(e.domain);
				argsValue.add(null);
			}
		}
		
		void build(int index) {
			if (index >= size) {
				T e = derivation.apply(argsValue);
				if (e != null)
					builder.add(e);
			} else
				for (T e : argsDomain.get(index)) {
					argsValue.set(index, e);
					build(index + 1);
				}
		}

		Domain<T> build() {
			build(0);
			return builder.build();
		}
	}

	public Variable<T> variable(String name, DerivationFunction<T> derivation, Collection<? extends Expression<T>> arguments) {
		int no = variables.size();
		if (name == null)
			name = "_" + no;
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("variable name cannot start with _");
		Variable<T> v = add(new Variable<>(no, name, new Derivation<>(derivation, arguments).build()));
		List<Expression<T>> cat = new ArrayList<>();
		cat.add(v);
		cat.addAll(arguments);
		constraint("constraint_" + name, new DerivationPredicate<T>(derivation), cat);
		return v;
	}
	
	public Variable<T> variable(DerivationFunction<T> derivation, Collection<? extends Expression<T>> arguments) {
		return variable(null, derivation, arguments);
	}

	@SafeVarargs
	public final Variable<T> variable(String name, DerivationFunction<T> derivation, Expression<T>... arguments) {
		return variable(name, derivation, Arrays.asList(arguments));
	}

	@SafeVarargs
	public final Variable<T> variable(DerivationFunction<T> derivation, Expression<T>... arguments) {
		return variable(null, derivation, arguments);
	}
	
	public Bind<T> bind() {
		Bind<T> bind = new Bind<>(variables.size());
		for (Variable<T> e : variables)
			bind.put(e, e.domain);
		for (Variable<T> e : variables)
			if (!e.test(bind))
				return null;
		return bind;
	}
}
