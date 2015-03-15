package jp.saka1029.cspj.problem.old.singletype;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Result<T> {

    private final Map<Variable<T>, T> map = new HashMap<>();
    
    public Result() {
    }

    public Result(Problem<T> problem, Bind<T> bind) {
        for (Variable<T> v : problem.variables())
            map.put(v, bind.get(v).first());
    }
    
	public T get(Expression<T> e) {
    	if (e instanceof Variable<?>)
    		return map.get((Variable<T>)e);
    	else if (e instanceof Expression<?>)
    		return e.domain.first();
    	else
    		throw new IllegalArgumentException("unknown expression: " + e);
    }
    
    public void put(Variable<T> key, T value) {
        map.put(key, value);
    }
    
    public Iterable<Entry<Variable<T>, T>> entrySet() {
        return map.entrySet();
    }
    
    public Iterable<T> values() {
        return map.values();
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
