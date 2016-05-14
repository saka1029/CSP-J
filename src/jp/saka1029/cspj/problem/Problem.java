package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

public class Problem {

	static final Logger logger = Helper.getLogger(Problem.class.getName());

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
	public final <T, A> Variable<T> variable(String name, String constraintName,
	        DerivationFunction<T, A> function, Collection<Variable<? extends A>> variables) {
		Domain<T> domain = new Builder<>(function, variables).build();
		Variable<T> variable = variable(name, domain);
		List<Variable<? extends A>> constraintVariables = new ArrayList<>();
		constraintVariables.add((Variable<? extends A>)variable);
		constraintVariables.addAll(variables);
		constraint(constraintName, new DerivationPredicate<T, A>(function), constraintVariables);
		return variable;
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

	public Bind bind() {
		Bind bind = new Bind(_variables.size(), _constraints.size());
		for (Variable<?> v : _variables)
			v.put(bind);
		Map<Variable<?>, Domain<?>> que = new TreeMap<>();
		for (Constraint c : _constraints)
			if (!c.test(bind, que))
				throw new RuntimeException("Constraint (" + c + ") unsatisfied");
		for (Entry<Variable<?>, Domain<?>> e : que.entrySet())
			if (!e.getKey().rawBind(e.getValue(), bind))
				throw new RuntimeException("bind " + e.getKey() + " <- " + e.getValue() + " failed");
		return bind;
	}
}
