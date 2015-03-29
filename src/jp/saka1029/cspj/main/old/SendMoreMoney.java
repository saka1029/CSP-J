package jp.saka1029.cspj.main.old;

import jp.saka1029.cspj.problem.old.Constant;
import jp.saka1029.cspj.problem.old.ConstraintFunction;
import jp.saka1029.cspj.problem.old.Domain;
import jp.saka1029.cspj.problem.old.Variable;
import jp.saka1029.cspj.solver.old.Result;
import jp.saka1029.cspj.solver.old.SolverMain;

public class SendMoreMoney extends SolverMain {

    Variable<Integer> s, e, n, d, m, o, r, y;

    @Override
    public void define() {
        Domain<Integer> digits = Domain.range(0, 9);
        Domain<Integer> first = Domain.range(1, 9);
        Domain<Integer> carry = Domain.of(0, 1);
        s = problem.variable("s", first);
        e = problem.variable("e", digits);
        n = problem.variable("n", digits);
        d = problem.variable("d", digits);
        m = problem.variable("m", first);
        o = problem.variable("o", digits);
        r = problem.variable("r", digits);
        y = problem.variable("y", digits);
        Variable<Integer> c1, c2, c3, c4;
        c1 = problem.variable("c1", carry);
        c2 = problem.variable("c2", carry);
        c3 = problem.variable("c3", carry);
        c4 = problem.variable("c4", carry);
        //c4 c3 c2 c1
        //    s  e  n  d
        // +  m  o  r  e
        //--------------
        // m  o  n  e  y
        ConstraintFunction<Boolean> f =
            a -> (int)a[0] + (int)a[1] + (int)a[2] == (int)a[3] + (int)a[4] * 10;
        String fn = "(%s + %s + %s == %s + (%s * 10))";
        Constant<Integer> Z = problem.constant(0);
        problem.constraint(f, fn,  Z, d, e, y, c1);
        problem.constraint(f, fn, c1, n, r, e, c2);
        problem.constraint(f, fn, c2, e, o, n, c3);
        problem.constraint(f, fn, c3, s, m, o, c4);
        problem.constraint(a -> a[0].equals(a[1]), "==", c4, m);
        problem.allDifferent(s, e, n, d, m, o, r, y);
    }

    @Override
    public boolean answer(int c, Result x) {
        write(" %s%s%s%s", x.get(s), x.get(e), x.get(n), x.get(d));
        write("+%s%s%s%s", x.get(m), x.get(o), x.get(r), x.get(e));
        write("-----");
        write("%s%s%s%s%s", x.get(m), x.get(o), x.get(n), x.get(e), x.get(y));
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        new SendMoreMoney().parse(args).solve();
    }

}
