package jp.saka1029.cspj.problem.old.singletype;

import java.util.Arrays;

public class Bind<T> {

    private final Domain<T>[] domains;
    public final int level;
    
    @SuppressWarnings("unchecked")
	private Bind(int size, int level) {
        this.domains = (Domain<T>[])new Domain<?>[size];
        this.level = level;
    }

    public Bind(int size) {
        this(size, 0);
    }
    
    public Bind(Bind<T> prev) {
        this(prev.domains.length, prev.level + 1);
        System.arraycopy(prev.domains, 0, this.domains, 0, prev.domains.length);
    }
    
	public Domain<T> get(Expression<T> e) {
    	if (e instanceof Variable<?>)
            return domains[((Variable<T>)e).no];
    	else if (e instanceof Constant<?>)
    		return e.domain;
    	else
    		throw new IllegalArgumentException("unknown expression: " + e);
    }

    public Domain<T> get(int no) {
    	return domains[no];
    }

    public void put(Variable<T> key, Domain<T> value) {
        domains[key.no] = value;
    }

    public void fail(Variable<?> key) {
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

    @Override
    public String toString() {
        return Arrays.toString(domains);
    }
}
