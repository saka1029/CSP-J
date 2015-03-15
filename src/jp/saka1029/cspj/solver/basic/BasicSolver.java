package jp.saka1029.cspj.solver.basic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import jp.saka1029.cspj.problem.old.Bind;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Log;
import jp.saka1029.cspj.problem.old.Problem;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.Answer;
import jp.saka1029.cspj.solver.Debug;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.Solver;

/**
 * 後戻り探索の効率を向上
 * • 汎用的な方法は速度を飛躍的に改善 :
 * – どの変数が割り当てられるべきか
 * – どの順番で値が試みられるべきか
 * – 必ず失敗に導くものを早い時期に見つけることが できるか
 * 
 * 最も制約された変数 
 * • 最も制約された変数:
 *   割当可能な値が最小の変数を選ぶ
 * • 別名： 最小残余値(MRV)での発見的手法
 * 
 * 最も制約している変数 
 * • 最も制約された変数がいくつかあったときの タイブレーク
 * • 最も制約している変数 :
 * – 残る変数に最も制約を与える変数を選ぶ
 * 
 * 最も制約されていない値 
 * • 変数が与えられたとき、最も制約しない値を 選ぶ :
 * – この後の変数の割当で最も柔軟性が高いもの 
 * • これらの発見的方法を組み合わせることで 1000クイーンまで可能になる
 */
public class BasicSolver implements Solver {

    public EnumSet<Debug> debug = Debug.NONE;
    @Override public EnumSet<Debug> debug() { return debug; }
    @Override public void debug(EnumSet<Debug> debug) { this.debug = debug; }

    private Problem problem;
    private Answer answer;
    private int answerCount;
    private List<Variable<?>> reduced;
    
    public BasicSolver(EnumSet<Debug> debug) {
        this.debug = debug;
    }
    
    public BasicSolver() {
    }
    
    static class Bin {
    	final Variable<?> v;
    	final Domain<?> d;
    	Bin(Variable<?> v, Domain<?> d) { this.v = v; this.d = d; }
    }

    private Variable<?> select(Bind bind) {
    	return reduced.stream()
    		.map(v -> new Bin(v, bind.get(v)))
    		.filter(e -> e.d.size() > 1)
    		.peek(e -> { if (e.d.size() == 0) throw new IllegalStateException("empty domain found"); })
    		.min((a, b) -> Math.min(a.d.size(), b.d.size()))
    		.orElse(new Bin(null, null)).v;
//        Variable<?> sel = null;
//        int min = Integer.MAX_VALUE;
//        for (Variable<?> v : reduced) {
//            Domain<?> d = bind.get(v);
//            int size = d.size();
//            if (size <= 0) throw new IllegalStateException("empty domain found in bound");
//            if (size == 1) continue;
//            if (sel == null || size < min) {
//                sel = v;
//                min = size;
//            }
//        }
//        return sel;
    }

//    private boolean solveSimple(Bind bind) {
//        boolean b = true;
//        Variable<?> var = select(bind);
//        if (var == null) {
//            ++answerCount;
//            b = answer.answer(new Result(problem, bind));
//        } else
//            for (Object i : bind.get(var)) {
//                Bind newBind = new Bind(bind);
//                if (var.bindSingle(i, newBind))
//                    b = solveSimple(newBind);
//                if (!b) break;
//            }
//        return b;
//    }
    
    private boolean solveParallel(Bind bind, boolean parallel) {
        boolean b = true;
        Variable<?> var = select(bind);
        if (var == null) {
            ++answerCount;
            b = answer.answer(new Result(problem, bind));
        } else
        	return bind.get(var).stream(parallel)
        		.map(i -> {
        			Bind nb = new Bind(bind);
        			return var.bindSingle(i, nb) ? nb : null;
        		})
        		.filter(x -> x != null)
        		.allMatch(x -> solveParallel(x, false));
        return b;
    }
    
    private void solve(Problem problem) {
        long start = System.currentTimeMillis();
        if (debug.contains(Debug.Problem))
            problem.info();
        this.answerCount = 0;
        Bind b = problem.bind();
        if (b == null) return;
        if (debug.contains(Debug.Reduced)) {
        	Log.info("BasicSolver: **** reduced problem ****");
        	for (Variable<?> v : problem.variables())
        		Log.info("BasicSolver: %s : %s", v, b.get(v));
        }
        Bind bind = new Bind(b);
        this.reduced = new ArrayList<>();
        for (Variable<?> v : problem.variables())
            if (bind.get(v).size() > 1)
                this.reduced.add(v);
//        solveSimple(bind); 
        solveParallel(bind, false); 
        int vars = problem.variables().size();
        Log.info("BasicSolver: vars=%d reduced vars=%d answers=%d elapse=%dms",
            vars, vars - reduced.size(),
            answerCount, System.currentTimeMillis() - start);
    }

    @Override
    public void solve(Problem problem, Answer answer) {
        this.problem = problem;
        this.answer = answer;
        solve(problem);
    }
}