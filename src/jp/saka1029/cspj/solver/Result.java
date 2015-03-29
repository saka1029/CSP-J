package jp.saka1029.cspj.solver;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;

public class Result {

    private final Map<Variable<?>, Object> map = new TreeMap<>();
    
    public Result() {
    }

    public Result(Problem problem, Bind bind) {
        for (Variable<?> v : problem.variables)
            map.put(v, bind.get(v).first());
    }
    
	@SuppressWarnings("unchecked")
	public <T> T get(Variable<T> e) {
        return (T)map.get((Variable<T>)e);
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
