package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class Constraint extends ProblemElement implements Comparable<Constraint> {

	static final Logger logger = Logger.getLogger(Constraint.class.toString());
	
	public final ConstraintPredicate<Object> predicate;
	public final List<Variable<?>> variables;
	
	Constraint(Problem problem, int no, String name, ConstraintPredicate<Object> predicate,
	        Collection<? extends Variable<?>> variables) {
		super(problem, no, name);
		if (predicate == null)
			throw new IllegalArgumentException("predicate");
		if (variables == null || variables.size() == 0)
			throw new IllegalArgumentException("variables");
		this.predicate = predicate;
		List<Variable<?>> list = new ArrayList<>(variables.size());
		Set<Variable<?>> set = new HashSet<>();
		for (Variable<?> v : variables) {
		    if (v.problem != problem)
		        throw new IllegalArgumentException(
		            "Variable (" + v + ") belongs to different problem");
		    if (v.domain.size() > 1) {
                if (set.contains(v))
                    throw new IllegalArgumentException(
                        "A variable appears more than once" + variables);
                set.add(v);
		    }
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
                logger.finest("EncodeDriver.encode: test " + values + " = " + predicate.test(values));
				if (predicate.test(values) == consistent) {
					encoder.encode(indicesProtected, valuesProtected);
					logger.finest("EncodeDriver.encde: " + indicesProtected + ", " + valuesProtected);
				}
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

	private boolean build(List<Domain.Builder<Object>> builders, Bind bind, Map<Variable<?>, Domain<?>> que) {
		int size = variables.size();
//		for (int i = 0; i < size; ++i)
//			if (!variables.get(i).rawBind(builders.get(i).build(), bind))
//				return false;
		for (int i = 0; i < size; ++i) {
			Domain.Builder<?> builder = builders.get(i);
			int builderSize = builder.size();
			Variable<?> v = variables.get(i);
			if (builderSize <= 0)
				return false;
			else if (builderSize < bind.get(v).size()) {
				Domain<?> queDomain = que.get(v);
				if (queDomain == null || builderSize < queDomain.size()) {
//					if (logger.isLoggable(Level.INFO) && queDomain != null)
//						logger.info("Constrant.build: " + queDomain.size() + " -> " + builderSize);
					que.put(v, builder.build());
				}
			}
		}
		return true;
	}

	boolean test(Bind bind, Map<Variable<?>, Domain<?>> que) {
		if (bind.get(this) != null)
			return true;
		if (logger.isLoggable(Level.FINEST))
            logger.finest("Constraint.test/1: " + this);
		int size = variables.size();
		List<Object[]> combinations = new ArrayList<>();
		List<Domain.Builder<Object>> builders = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			builders.add(new Domain.Builder<>());
		encode(bind, true, (indices, values) -> {
			for (int i = 0; i < size; ++i)
				builders.get(i).add(values.get(i));
			combinations.add(values.toArray());
		});
		bind.put(this, combinations);
//		logger.finest("Constraint.test/1: " + this + " : " + builders);
		logger.finest("Constraint.test/1: bind.put " + this + ", "
		    + combinations.stream()
		        .map(a -> Arrays.toString(a))
		        .collect(Collectors.joining(", ", "[", "]")));
		return build(builders, bind, que);
	}

	boolean test(Variable<?> variable, Bind bind, Map<Variable<?>, Domain<?>> que) {
		List<Object[]> combinations = bind.get(this);
		if (combinations == null)
			return test(bind, que);
		if (logger.isLoggable(Level.FINEST))
            logger.finest("Constraint.test/2: " + this);
		int orgSize = combinations.size();
		int size = variables.size();
		int pos = variables.indexOf(variable);
		Domain<?> domain = bind.get(variable);
		List<Domain.Builder<Object>> builders = new ArrayList<>(size);
		for (int i = 0; i < size; ++i)
			builders.add(new Domain.Builder<>());
		for (Iterator<Object[]> iterator = combinations.iterator(); iterator.hasNext(); ) {
			Object[] values = iterator.next();
			if (!domain.contains(values[pos]))
				iterator.remove();
			else
                for (int i = 0; i < size; ++i)
                    builders.get(i).add(values[i]);
		}
		int newSize = combinations.size();
//		if (logger.isLoggable(Level.FINEST))
//            logger.finest("Constraint.test/2: combinations size "
//                + orgSize + " -> " + newSize
//                + " " + (orgSize == newSize ? "==" : ""));
		if (newSize == 0)
			return false;
		else if (newSize == orgSize)
			return true;
//		logger.finest("Constraint.test/2: " + this + " : " + builders);
		return build(builders, bind, que);
	}

	private static <T> List<String> toStringList(Collection<T> list) {
		List<String> r = new ArrayList<>();
		for (T e : list)
			r.add(e.toString());
		return r;
	}

	private String toString(List<?> vars) {
        char c = name.charAt(0);
        if (name.contains("%s"))
            return String.format(name, vars.toArray());
        else if  (Character.isAlphabetic(c) || c == '_' || c > 0xff)
            return name + "(" + String.join(", ", toStringList(vars)) + ")";
        else if (vars.size() == 1)
            return String.format("(%s %s)", name, vars.get(0));
        else
            return "(" + String.join(" " + name + " ", toStringList(vars)) + ")";
	}

    @Override
    public String toString() {
    	if (predicate instanceof DerivationPredicate)
    		return String.format("%s == %s",
    			variables.get(0).toString(),
    			toString(variables.subList(1, variables.size())));
    	else
    		return toString(variables);
    }

	@Override
	public int compareTo(Constraint o) {
		return no - o.no;
	}
}
