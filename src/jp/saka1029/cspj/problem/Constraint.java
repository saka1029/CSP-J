package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class Constraint extends ProblemElement {

	static final Logger logger = Logger.getLogger(Constraint.class.toString());
	
	public final ConstraintPredicate predicate;
	public final List<Variable<?>> variables;
	
	Constraint(Problem problem, int no, String name, ConstraintPredicate predicate, Collection<? extends Variable<?>> variables) {
		super(problem, no, name);
		this.predicate = predicate;
		List<Variable<?>> list = new ArrayList<>(variables.size());
		for (Variable<?> v : variables) {
			list.add(v);
			v.constraints.add(this);
		}
		this.variables = Collections.unmodifiableList(list);
	}
	
	private static <T> List<T> list(int size) {
		List<T> r = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			r.add(null);
		return r;
	}

	private class EncodeDriver {

		final int size = variables.size();
		final List<Integer> indices = list(size);
		final List<Integer> indicesProtected = Collections.unmodifiableList(indices);
		final List<Object> values = list(size);
		final List<Object> valuesProtected = Collections.unmodifiableList(values);
		final List<Domain<?>> domains = list(size);
		final boolean consistent;
		final ConstraintEncoder encoder;
		
		EncodeDriver(Bind bind, boolean consistent, ConstraintEncoder encoder) {
			for (int i = 0; i < size; ++i)
				domains.set(i, bind.get(variables.get(i)));
			this.consistent = consistent;
			this.encoder = encoder;
		}
		
		void encode(int index) {
			if (index >= size) {
				if (predicate.test(values) == consistent)
					encoder.encode(indicesProtected, valuesProtected);
			} else {
				int i = 0;
				for (Object e : domains.get(index)) {
					indices.set(index, i++);
					values.set(index, e);
					encode(index + 1);
				}
			}
		}
	}

	public void encode(Bind bind, boolean consistent, ConstraintEncoder encoder) {
		new EncodeDriver(bind, consistent, encoder).encode(0);
	}

	boolean test(Bind bind) {
		int size = variables.size();
		List<Domain.Builder<Object>> builders = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			builders.add(new Domain.Builder<>());
		encode(bind, true, (indices, values) -> {
			for (int i = 0; i < size; ++i)
				builders.get(i).add(values.get(i));
		});
		logger.fine("Constraint.test: " + this + " : " + builders);
		for (int i = 0; i < size; ++i)
			if (!variables.get(i).rawBind(builders.get(i).build(), bind))
				return false;
		return true;
	}

	private static <T> List<String> toStringList(Collection<T> list) {
		List<String> r = new ArrayList<>();
		for (T e : list)
			r.add(e.toString());
		return r;
	}

	private String toString(List<?> vars) {
        char c = name.charAt(0);
        if (name.contains("%"))
            return String.format(name, vars.toArray());
        else if  (Character.isAlphabetic(c) || c > 0xff)
            return name + "(" + String.join(", ", toStringList(vars)) + ")";
        else if (vars.size() == 1)
            return String.format("(%s %s)", name, vars.get(0));
        else
            return "(" + String.join(" " + name + " ", toStringList(vars)) + ")";
	}

    @Override
    public String toString() {
    	if (predicate instanceof DerivationPredicate<?>)
    		return String.format("%s == %s",
    			variables.get(0).toString(),
    			toString(variables.subList(1, variables.size())));
    	else
    		return toString(variables);
    }
}
