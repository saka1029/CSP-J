package jp.saka1029.cspj.problem;

import java.util.Arrays;

public class Bind {

    public final boolean debug = false;
    private final Domain<?>[] domains;
    public final int level;
    
    private Bind(int size, boolean debug, int level) {
        this.domains = new Domain<?>[size];
        this.level = level;
    }

    public Bind(int size, boolean debug) {
        this(size, debug, 0);
    }
    
    public Bind(Bind prev) {
        this(prev.domains.length, prev.debug, prev.level + 1);
        System.arraycopy(prev.domains, 0, this.domains, 0, prev.domains.length);
    }
    
    public Domain<?> get(Variable<?> key) {
        return domains[key.no];
    }

    public Domain<?> get(int no) {
    	return domains[no];
    }

    public void put(Variable<?> key, Domain<?> value) {
        if (debug) Log.info("%sBind.put: %s = %s", Util.spaces(level * 2), key, value);
        domains[key.no] = value;
    }

    public void fail(Variable<?> key) {
        if (debug) Log.info("%sBind.fail: %s = %s -> []", Util.spaces(level * 2), key, get(key));
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
