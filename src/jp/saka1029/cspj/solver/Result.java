package jp.saka1029.cspj.solver;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.saka1029.cspj.problem.old.Bind;
import jp.saka1029.cspj.problem.old.Expression;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;

public class Result {

    private final Map<Variable<?>, Object> map = new TreeMap<>();
    
    public Result() {
    }

    public Result(Problem problem, Bind bind) {
        for (Variable<?> v : problem.variables())
            map.put(v, bind.get(v).first());
    }
    
    @SuppressWarnings("unchecked")
	public <T> T get(Expression<T> e) {
    	if (e instanceof Variable<?>)
    		return (T)map.get((Variable<T>)e);
    	else if (e instanceof Expression<?>)
    		return e.domain.first();
    	else
    		throw new IllegalArgumentException("unknown expression: " + e);
    }
    
    public void put(Variable<?> key, Object value) {
        map.put(key, value);
    }
    
    public Iterable<Entry<Variable<?>, Object>> entrySet() {
        return map.entrySet();
    }
    
    public Iterable<Object> values() {
        return map.values();
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
