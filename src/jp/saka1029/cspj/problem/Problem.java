package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Problem {
	
	static final Logger logger = Logger.getLogger(Problem.class.getName());
	
	private final List<Variable<?>> _variables = new ArrayList<>();
	public final List<Variable<?>> variables = Collections.unmodifiableList(_variables);
	public Variable<?> variable(int index) { return _variables.get(index); }
	private final Map<String, Variable<?>> variableNames = new HashMap<>();
	public Variable<?> variable(String name) { return variableNames.get(name); }

	private <T> Variable<T> add(Variable<T> variable) {
		String name = variable.name;
		if (variableNames.containsKey(name))
			throw new IllegalArgumentException("variable name(" + name + ") is duplicated");
		variableNames.put(name, variable);
		_variables.add(variable);
		return variable;
	}
	
	public <T> Variable<T> variable(String name, Domain<T> domain) {
		int no = _variables.size();
		if (name == null)
			name = "_v" + no;
		else if (name.equals(""))
			throw new IllegalArgumentException("invalid variable name: \"\"");
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("variable names start with '_' are reserved: " + name);
		return add(new Variable<T>(this, no, name, domain));
	}
	
	private final Map<Object, Variable<?>> constantCache = new HashMap<>();
	
	public <T> Variable<T> constant(T value) {
		@SuppressWarnings("unchecked")
		Variable<T> constant = (Variable<T>)constantCache.get(value);
		if (constant == null)
			constantCache.put(value, constant = variable(value.toString(), Domain.of(value)));
		return constant;
	}
	
	private static <T> List<T> list(int size) {
		List<T> r = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			r.add(null);
		return r;
	}

	private static class Builder<T, A> {
		
		final DerivationFunction<T, A> function;
		final int size;
		final List<A> values;
		final List<Variable<? extends A>> variables;
		final Domain.Builder<T> builder = new Domain.Builder<>();
		
		Builder(DerivationFunction<T, A> function, Collection<Variable<? extends A>> variables) {
            this.function = function;
            this.size = variables.size();
            this.values = list(size);
            this.variables = new ArrayList<>(variables);
		}
		
		void build(int index) {
			if (index >= size) {
				T t = function.apply(values);
				if (t != null)
					builder.add(t);
			} else
				for (A e : variables.get(index).domain) {
					values.set(index, e);
					build(index + 1);
				}
		}

		Domain<T> build() {
			build(0);
			return builder.build();
		}
	}

	@SuppressWarnings("unchecked")
	public final <T, A> Variable<T> variable(String name, String constraintName, DerivationFunction<T, A> function, Collection<Variable<? extends A>> variables) {
		Domain<T> domain = new Builder<>(function, variables).build();
		Variable<T> variable = variable(name, domain);
		List<Variable<? extends A>> constraintVariables = new ArrayList<>();
		constraintVariables.add((Variable<? extends A>)variable);
		constraintVariables.addAll(variables);
		constraint(constraintName, new DerivationPredicate<T, A>(function), constraintVariables);
		return variable;
	}

//	@SafeVarargs
//	public final <T, A> Variable<T> variable(String name, String constraintName, DerivationFunction<T, A> function, Variable<A>... variables) {
//		return variable(name, constraintName, function, Arrays.asList(variables));
//	}
	
	public final <T, A> Variable<T> variable(String name, String constraintName, DerivationFunction1<T, A> function, Variable<A> a) {
		return variable(name, constraintName, function, Arrays.asList(a));
	}
	
	public final <X, T, A extends X, B extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction2<X, T, A, B> function,
            Variable<A> a, Variable<B> b) {
		return variable(name, constraintName, function, Arrays.asList(a, b));
	}
	
	public final <X, T, A extends X, B extends X, C extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction3<X, T, A, B, C> function,
            Variable<A> a, Variable<B> b, Variable<C> c) {
		return variable(name, constraintName, function, Arrays.asList(a, b, c));
	}
	
	public final <X, T, A extends X, B extends X, C extends X, D extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction4<X, T, A, B, C, D> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d) {
		return variable(name, constraintName, function, Arrays.asList(a, b, c, d));
	}
	
	public final <X, T, A extends X, B extends X, C extends X, D extends X, E extends X> Variable<T> variable(
            String name, String constraintName, DerivationFunction5<X, T, A, B, C, D, E> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e) {
		return variable(name, constraintName, function, Arrays.asList(a, b, c, d, e));
	}
	
	public final <X, T, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X> Variable<T> variable(
			String name, String constraintName, DerivationFunction6<X, T, A, B, C, D, E, F> function,
            Variable<A> a, Variable<B> b, Variable<C> c, Variable<D> d, Variable<E> e, Variable<F> f) {
		return variable(name, constraintName, function, Arrays.asList(a, b, c, d, e, f));
	}
	
	private final List<Constraint> _constraints = new ArrayList<>();
	public final List<Constraint> constraints = Collections.unmodifiableList(_constraints);
	
	private <A> Constraint add(Constraint constraint) {
		_constraints.add(constraint);
		return constraint;
	}
	
	@SuppressWarnings("unchecked")
	public <T, A> Constraint constraint(String name, ConstraintPredicate<A> predicate, Collection<Variable<? extends A>> variables) {
		int no = _constraints.size();
		if (name == null)
            name = "_c" + no;
		else if (name.equals(""))
			throw new IllegalArgumentException("invalid constraint name: \"\"");
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("constraint names start with '_' are reserved: " + name);
		return add(new Constraint(this, no, name, (ConstraintPredicate<Object>)predicate, variables));
	}

//	@SafeVarargs
//	public final <A> Constraint<A> constraint(String name, ConstraintPredicate<A> predicate, Variable<? extends A>... variables) {
//		return constraint(name, predicate, Arrays.asList(variables));
//	}

	public final <A> Constraint constraint(String name, ConstraintPredicate1<A> predicate, Variable<? extends A> a) {
		return constraint(name, predicate, Arrays.asList(a));
	}
	
	public final <X, A extends X, B extends X> Constraint constraint(String name,
		ConstraintPredicate2<X, A, B> predicate,
		Variable<? extends A> a, Variable<? extends B> b) {
		return constraint(name, predicate, Arrays.asList(a, b));
	}
	
	public final <X, A extends X, B extends X, C extends X> Constraint constraint(String name,
		ConstraintPredicate3<X, A, B, C> predicate,
		Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c) {
		return constraint(name, predicate, Arrays.asList(a, b, c));
	}
	
	public final <X, A extends X, B extends X, C extends X, D extends X> Constraint constraint(String name,
		ConstraintPredicate4<X, A, B, C, D> predicate,
		Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c, Variable<? extends D> d) {
		return constraint(name, predicate, Arrays.asList(a, b, c, d));
	}
	
	public final <X, A extends X, B extends X, C extends X, D extends X, E extends X> Constraint constraint(String name,
		ConstraintPredicate5<X, A, B, C, D, E> predicate,
		Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c, Variable<? extends D> d, Variable<? extends E> e) {
		return constraint(name, predicate, Arrays.asList(a, b, c, d, e));
	}
	
	public final <X, A extends X, B extends X, C extends X, D extends X, E extends X, F extends X> Constraint constraint(String name,
		ConstraintPredicate6<X, A, B, C, D, E, F> predicate,
		Variable<? extends A> a, Variable<? extends B> b, Variable<? extends C> c, Variable<? extends D> d, Variable<? extends E> e, Variable<? extends F> f) {
		return constraint(name, predicate, Arrays.asList(a, b, c, d, e, f));
	}
	
	public <A> void forAllPairs(String name, ConstraintPredicate2<A, A, A> predicate, Collection<Variable<? extends A>> variables) {
		int i = 0;
		for (Variable<? extends A> u : variables) {
			int j = 0;
			for (Variable<? extends A> v : variables) {
				if (i < j)
					constraint(name, predicate, u, v);
				++j;
			}
			++i;
		}
	}
	
	@SafeVarargs
	public final <A> void forAllPairs(String name, ConstraintPredicate2<A, A, A> predicate, Variable<A>... variables) {
		forAllPairs(name, predicate, Arrays.asList(variables));
	}
	
	public <A> void forAllPairs(String name, ConstraintPredicate4<Object, Integer, A, Integer, A> predicate, Collection<Variable<? extends A>> variables) {
		int i = 0;
		for (Variable<? extends A> u : variables) {
			int j = 0;
			for (Variable<? extends A> v : variables) {
				if (i < j)
					constraint(name, predicate, constant(i), u, constant(j), v);
				++j;
			}
			++i;
		}
	}

	@SafeVarargs
	public final <A> void forAllPairs(String name, ConstraintPredicate4<Object, Integer, A, Integer, A> predicate, Variable<A>... variables) {
		forAllPairs(name, predicate, Arrays.asList(variables));
	}

	public <A> void allDifferent(Collection<Variable<? extends A>> variables) {
		forAllPairs("!=", (a, b) -> !a.equals(b), variables);
	}
	
	@SafeVarargs
	public final <A> void allDifferent(Variable<A>... variables) {
		allDifferent(Arrays.asList(variables));
	}

	public <A> void forAllNeighbors(String name, ConstraintPredicate2<A, A, A> predicate, Collection<Variable<A>> variables) {
		Variable<A> left = null;
		for (Variable<A> v : variables) {
			if (left != null)
                constraint(name, predicate, left, v);
            left = v;
		}
	}
	
	@SafeVarargs
	public final <A> void forAllNeighbors(String name, ConstraintPredicate2<A, A, A> predicate, Variable<A>... variables) {
		forAllNeighbors(name, predicate, Arrays.asList(variables));
	}
	
	public Bind bind() {
		Bind bind = new Bind(_variables.size(), _constraints.size());
		for (Variable<?> v : _variables)
			v.put(bind);
		for (Constraint c : _constraints)
			if (!c.test(bind))
				throw new RuntimeException("Constraint (" + c + ") unsatisfied");
		return bind;
	}
}
