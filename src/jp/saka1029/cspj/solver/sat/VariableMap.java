package jp.saka1029.cspj.solver.sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.saka1029.cspj.problem.Bind;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;

public class VariableMap {

	public static class SatLiteral {
		
		public final int variable;
		public final boolean sign;
		
		public SatLiteral(int variable, boolean sign) {
			this.variable = variable;
			this.sign = sign;
		}
		
		@Override
		public String toString() {
			return String.format("SatLiteral(%s, %s)", variable, sign);
		}
	}

	public static class VarValue {

		public final Variable<?> variable;
		public final Object value;

		public VarValue(Variable<?> variable, Object value) {
			this.variable = variable;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return variable.hashCode() << 16 ^ value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof VarValue))
				return false;
			VarValue o = (VarValue)obj;
			return o.variable == variable && o.value == value;
		}
		
		@Override
		public String toString() {
			return String.format("%s=%s", variable, value);
		}
	}
	
	public static class VarValue2 extends VarValue {
		
		public final Object falseValue;
		
		VarValue2(Variable<?> variable, Object value, Object falseValue) {
			super(variable, value);
			this.falseValue = falseValue;
		}
		
		@Override
		public String toString() {
			return String.format("%s=%s:%s", variable, value, falseValue);
		}
		
		public String toString(boolean sign) {
			if (falseValue == null)
				return String.format("%s%s%s", variable, sign ? "!=" : "==", value);
			else
				return String.format("%s==%s", variable, sign ? falseValue : value);
		}

	}
	
	public final List<VarValue2> multiValues = new ArrayList<>();
	public final List<VarValue> singleValues = new ArrayList<>();
	private final Map<VarValue, Integer> map = new HashMap<>();
	
	public VariableMap(Problem problem, Bind bind) {
		for (Variable<?> v : problem.variables)
			add(v, bind);
	}

	public SatLiteral get(Variable<?> expression, Object value, boolean sign) {
		Integer i =  map.get(new VarValue((Variable<?>)expression, value));
		if (i == null) return null;
		VarValue2 v = multiValues.get(i);
		if (v.falseValue != null && value != v.value)
			sign = !sign;
		return new SatLiteral(i, sign);
	}
	
	void add(Variable<?> variable, Object trueValue, Object falseValue) {
		int i = multiValues.size();
		map.put(new VarValue(variable, trueValue), i);
		if (falseValue != null)
            map.put(new VarValue(variable, falseValue), i);
		multiValues.add(new VarValue2(variable, trueValue, falseValue));
	}

	public void add(Variable<?> variable, Bind bind) {
		Domain<?> domain = bind.get(variable);
		if (domain == null)
			throw new IllegalArgumentException("no domain");
		switch (domain.size()) {
		case 0:
			throw new IllegalArgumentException("empty domain");
		case 1:
			singleValues.add(new VarValue(variable, domain.first()));
			break;
		case 2:
			Iterator<?> it = domain.iterator();
			Object trueValue = it.next();
			Object falseValue = it.next();
			add(variable, trueValue, falseValue);
			break;
        default:
        	for (Object e : domain)
        		add(variable, e, null);
			break;
		}
	}
    
}
