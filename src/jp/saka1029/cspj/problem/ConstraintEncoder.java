package jp.saka1029.cspj.problem;

import java.util.List;

public interface ConstraintEncoder {

	void encode(List<Integer> indices, List<?> values);

}
