package jp.saka1029.cspj.solver.old;

import java.util.Map.Entry;

import jp.saka1029.cspj.problem.old.Constraint;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Variable;

public class AnswerVars implements Answer {

    public enum Output {
        STDOUT, LOG_INFO
    }

    public final boolean allAnswers;
    public final boolean allVars;
    public final Output output;
    
    public AnswerVars(boolean allAnswers, boolean allVars, Output output) {
        this.allAnswers = allAnswers;
        this.allVars = allVars;
        this.output = output;
    }
    
    public AnswerVars() {
        this(true, false, Output.STDOUT);
    }

    @Override
    public boolean answer(Result result) {
        for (Entry<Variable<?>, Object> e : result.entrySet())
            if (allVars || !(e.getKey() instanceof Constraint<?>))
                switch (output) {
                    case STDOUT:
                        Log.info("%s = %s%n", e.getKey(), e.getValue());
                        break;
                    case LOG_INFO:
                        Log.info("%s = %s%n", e.getKey(), e.getValue());
                        break;
                }
        return allAnswers;
    }

}
