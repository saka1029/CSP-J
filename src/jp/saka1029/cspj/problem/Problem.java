package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Problem {
	
	private final List<Variable<?>> _variables = new ArrayList<>();
	public final List<Variable<?>> variables = Collections.unmodifiableList(_variables);
	private final Map<String, Variable<?>> variableNames = new HashMap<>();

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
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("variable names start with '_' are reserved: " + name);
		return add(new Variable<T>(this, no, name, domain));
	}
	
	private static <T> List<T> list(int size) {
		List<T> r = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			r.add(null);
		return r;
	}

	private static class Builder<T> {
		
		final DerivationFunction<T> function;
		final int size;
		final List<Object> values;
		final List<Variable<?>> variables;
		final Domain.Builder<T> builder = new Domain.Builder<>();
		
		Builder(DerivationFunction<T> function, Collection<Variable<?>> variables) {
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
				for (Object e : variables.get(index).domain) {
					values.set(index, e);
					build(index + 1);
				}
		}

		Domain<T> build() {
			build(0);
			return builder.build();
		}
	}

	private <T> Variable<T> variable0(String name, String constraintName, DerivationFunction<T> function, Collection<? extends Variable<?>> variabes) {
		Domain<T> domain = new Builder<>(function, variables).build();
		Variable<T> variable = variable(name, domain);
		List<Variable<?>> constraintVariables = new ArrayList<>();
		constraintVariables.add(variable);
		constraintVariables.addAll(variables);
		constraint0(constraintName, new DerivationPredicate<>(function), constraintVariables);
		return variable;
	}

//	public <T> Variable<T> variable(String name, String constraintName, DerivationFunction<T> function, Collection<Variable<?>> variables) {
//		return variable0(name, constraintName, function, variables);
//	}

//	public <T> Variable<T> variable(String name, String constraintName, DerivationFunction<T> function, Variable<?>... variables) {
//		return variable0(name, constraintName, function, Arrays.asList(variables));
//	}

	public final <T, A> Variable<T> variable(String name, String constraintName, DerivationFunction2<T, ? extends A> function, Collection<Variable<? extends A>> variables) {
		return variable0(name, constraintName, function, variables);
	}

	@SafeVarargs
	public final <T, A> Variable<T> variable(String name, String constraintName, DerivationFunction2<T, ? extends A> function, Variable<? extends A>... variables) {
		return variable0(name, constraintName, function, Arrays.asList(variables));
	}

	private final List<Constraint> _constraints = new ArrayList<>();
	public final List<Constraint> constraints = Collections.unmodifiableList(_constraints);
	
	private Constraint add(Constraint constraint) {
		_constraints.add(constraint);
		return constraint;
	}
	
	private Constraint constraint0(String name, ConstraintPredicate predicate, Collection<? extends Variable<?>> variables) {
		int no = _constraints.size();
		if (name == null)
            name = "_c" + no;
		else if (name.startsWith("_"))
			throw new IllegalArgumentException("constraint names start with '_' are reserved: " + name);
		return add(new Constraint(this, no, name, predicate, variables));
	}

//	public Constraint constraint(String name, ConstraintPredicate predicate, Collection<Variable<?>> variables) {
//		return constraint0(name, predicate, variables);
//	}

//	public Constraint constraint(String name, ConstraintPredicate predicate, Variable<?>... variables) {
//		return constraint0(name, predicate, Arrays.asList(variables));
//	}

	public final <A> Constraint constraint(String name, ConstraintPredicate2<A> predicate, Collection<Variable<? extends A>> variables) {
		return constraint0(name, predicate, variables);
	}

	@SafeVarargs
	public final <A> Constraint constraint(String name, ConstraintPredicate2<A> predicate, Variable<? extends A>... variables) {
		return constraint0(name, predicate, Arrays.asList(variables));
	}
	
	public Bind bind() {
		Bind bind = new Bind(_variables.size());
		for (Variable<?> v : _variables)
			v.put(bind);
		for (Variable<?> v : _variables)
			if (!v.test(bind))
				throw new NoSolutionException("variable(" + v +") has no value");
		return bind;
	}
}
