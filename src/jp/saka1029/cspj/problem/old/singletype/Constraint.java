package jp.saka1029.cspj.problem.old.singletype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Constraint<T> {

	public final int no;
	public final ConstraintPredicate<T> predicate;
	public final String predicateName;
	public final List<Expression<T>> arguments;
	public final List<Variable<T>> variables;
	public final List<Integer> variableMap;
	
	Constraint(int no, String predicateName, ConstraintPredicate<T> predicate, Collection<? extends Expression<T>> arguments) {
		if (arguments.size() <= 0)
			throw new IllegalArgumentException("no args");
		this.no = no;
		this.predicate = predicate;
		this.predicateName = predicateName;
		List<Expression<T>> args = new ArrayList<>();
		List<Variable<T>> vars = new ArrayList<>();
		List<Integer> map = new ArrayList<>();
		for (Expression<T> e : arguments) {
			args.add(e);
			if (e instanceof Variable<?>) {
				Variable<T> v = (Variable<T>)e;
				map.add(vars.size());
				vars.add(v);
				v.constraints.add(this);
			} else
				map.add(-1);
		}
		this.arguments = Collections.unmodifiableList(args);
		this.variables = Collections.unmodifiableList(vars);
		this.variableMap = Collections.unmodifiableList(map);
	}
	
	private void variableMap(List<Integer> indices, List<T> values, List<Integer> mappedIndices, List<T> mappedValues) {
		for (int i = 0, size = indices.size(); i < size; ++i) {
			int j = variableMap.get(i);
			if (j >= 0) {
				mappedIndices.set(j, indices.get(i));
				mappedValues.set(j, values.get(i));
			}
		}
	}

	private static <E> List<E> newList(int size) {
		List<E> r = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			r.add(null);
		return r;
	}

	private class EncodeDriver {
		
		final int size = arguments.size();
		final ConstraintEncoder<T> encoder;
		final boolean consistent;
		final List<Integer> argsIndex = newList(arguments.size());
		final List<T> argsValue = newList(arguments.size());
		final List<Domain<T>> argsDomain = new ArrayList<>(arguments.size());
		final List<Integer> varsIndex = newList(variables.size());
		final List<Integer> varsIndexProtected = Collections.unmodifiableList(varsIndex);
		final List<T> varsValue = newList(variables.size());
		final List<T> varsValueProtected = Collections.unmodifiableList(varsValue);
		
		EncodeDriver(Bind<T> bind, boolean consistent, ConstraintEncoder<T> encoder) {
			this.encoder = encoder;
			this.consistent = consistent;
			for (Expression<T> e : arguments)
				argsDomain.add(bind.get(e));
		}
		
		void encode(int index) {
			if (index >= size) {
				if (predicate.test(argsValue) == consistent) {
					variableMap(argsIndex, argsValue, varsIndex, varsValue);
					encoder.encode(varsIndexProtected, varsValueProtected);
				}
			} else {
				int i = 0;
				for (T e : argsDomain.get(index)) {
					argsValue.set(index, e);
					argsIndex.set(index, i++);
					encode(index + 1);
				}
			}
		}
	}

	public void encode(Bind<T> bind, boolean consistent, ConstraintEncoder<T> encoder) {
		new EncodeDriver(bind, consistent, encoder).encode(0);
	}

	boolean test(Bind<T> bind) {
		int size = variables.size();
		List<Domain.Builder<T>> varsBuilder = new ArrayList<>(size);
		for (@SuppressWarnings("unused") Variable<T> e : variables)
			varsBuilder.add(new Domain.Builder<>());
		encode(bind, true, (indices, values) -> {
			for (int i = 0; i < size; ++i)
				varsBuilder.get(i).add(values.get(i));
		});
		for (int i = 0; i < size; ++i)
			if (!variables.get(i).bind(varsBuilder.get(i).build(), bind))
				return false;
		return true;
	}

	private static <T> Object[] asArray(Collection<T> list) {
		int size = list.size();
		Object[] r = new Object[size];
		int i = 0;
		for (T e : list)
			r[i++] = e;
		return r;
	}

	private static <T> List<String> toString(Collection<T> list) {
		List<String> r = new ArrayList<>();
		for (T e : list)
			r.add(e.toString());
		return r;
	}

    @Override
    public String toString() {
        char c = predicateName.charAt(0);
        if (predicateName.contains("%"))
            return String.format(predicateName, asArray(arguments));
        else if  (Character.isAlphabetic(c) || c > 0xff)
            return predicateName + "(" + String.join(", ", toString(arguments)) + ")";
        else if (arguments.size() == 1)
            return String.format("(%s %s)", predicateName, arguments.get(0));
        else
            return "(" + String.join(" " + predicateName + " ", toString(arguments)) + ")";
    }

}
