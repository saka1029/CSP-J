package jp.saka1029.cspj.problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Bind implements Iterable<Domain<?>> {

    static Logger logger = Helper.getLogger(Bind.class.getName());

	private final Domain<?>[] domains;
	private final List<Object[]>[] constraints;

	@SuppressWarnings("unchecked")
	Bind(int variableSize, int constraintSize) {
		this.domains = new Domain<?>[variableSize];
		this.constraints = (List<Object[]>[])new List<?>[constraintSize];
	}

	public Bind(Bind prev) {
		this(prev.domains.length, prev.constraints.length);
		System.arraycopy(prev.domains, 0, domains, 0, prev.domains.length);
		for (int i = 0, size = constraints.length; i < size; ++i)
			constraints[i] = new ArrayList<Object[]>(prev.constraints[i]);
	}

	@SuppressWarnings("unchecked")
	public <T> Domain<T> get(Variable<T> variable)  {
		return (Domain<T>)domains[variable.no];
	}

	public <T> void put(Variable<T> variable, Domain<T> domain) {
		domains[variable.no] = domain;
	}

	public List<Object[]> get(Constraint constraint) {
		return constraints[constraint.no];
	}

	public void put(Constraint constraint, List<Object[]> combinations) {
		constraints[constraint.no] = combinations;
	}

	public Object[][] map() {
    	Object[][] map = new Object[domains.length][];
    	int i = 0;
    	for (Domain<?> domain : domains) {
    		Object[] values = new Object[domain.size()];
    		int j = 0;
    		for (Object value : domain)
    			values[j++] = value;
    		map[i++] = values;
    	}
    	return map;
	}

	public int notUniqueVariableSize() {
		int r = 0;
		for (Domain<?> domain : domains)
			if (domain.size() > 1)
				++r;
		return r;
	}

	@Override
	public String toString() {
		return Arrays.toString(domains);
	}

	@Override
	public Iterator<Domain<?>> iterator() {
		return Arrays.asList(domains).iterator();
	}
}
