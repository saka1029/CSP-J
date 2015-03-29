package jp.saka1029.cspj.solver.old;

import java.util.EnumSet;

public enum Debug {
    Problem,
    Reduced,
    Trace,
    ;
    
    public static final EnumSet<Debug> ALL = EnumSet.allOf(Debug.class);
    public static final EnumSet<Debug> PROBLEM = EnumSet.of(Debug.Problem);
    public static final EnumSet<Debug> REDUCED = EnumSet.of(Debug.Reduced);
    public static final EnumSet<Debug> PROBLEM_REDUCED = EnumSet.of(Debug.Problem, Debug.Reduced);
    public static final EnumSet<Debug> TRACE = EnumSet.of(Debug.Trace);
    public static final EnumSet<Debug> NONE = EnumSet.noneOf(Debug.class);
    
}
