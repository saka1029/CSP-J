package jp.saka1029.cspj.problem;

import java.util.Arrays;
import java.util.Iterator;

public class Bind implements Iterable<Domain<?>> {

	private final Domain<?>[] domains;
	
	Bind(int size) {
		this.domains = new Domain<?>[size];
	}
	
	public Bind(Bind prev) {
		this(prev.domains.length);
		System.arraycopy(prev.domains, 0, domains, 0, prev.domains.length);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Domain<T> get(Variable<T> variable)  {
		return (Domain<T>)domains[variable.no];
	}
	
	public <T> void put(Variable<T> variable, Domain<T> domain) {
		domains[variable.no] = domain;
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
