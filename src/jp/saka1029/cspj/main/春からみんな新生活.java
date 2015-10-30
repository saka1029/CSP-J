package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Constraint;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;
import static jp.saka1029.cspj.problem.Helper.*;

/**
 * この春から新生活を始めることになった５人兄弟。
 * 彼らの話から、それぞれの年齢とどんな新生活が
 * 始まるのかを当ててください。
 * なお、５人は年齢の上下に関係なく、
 * 名前を呼び捨てにすることはありますが、
 * 「○○姉さん」と言っているときは○○さんは話してより年上です。
 * また、自分のことを他人のように言っている人はいません。
 * 
 * セツオ：　　オレはこの春、結婚することになったんだ。
 * イクミ：　　私はセツオより一つ年上よ。２５才で思い切って転職する人がいるのね。
 * カナコ：　　私は海外転勤になっちゃった。
 * 　　　　　　私はツキコとは１才違いで、セツオとは３才違いよ。
 * シンイチ：　カナコ姉さんはしっかりしていますよね。
 * 　　　　　　ところで、３０才の記念にペットを飼い始めたのは誰だっけ？
 * ツキコ：　　私もイクミ姉さんのように仕事頑張らないとね。
 */
public class 春からみんな新生活 extends SolverMain {
    
	static final Logger logger = Logger.getLogger(春からみんな新生活.class.getName());
	
    enum 新生活 { 国内転勤, 海外転勤, 転職, 結婚, ペットを飼う }
    
    Domain<新生活> すべての新生活 = Domain.of(新生活.values());
    Domain<Integer> すべての年齢 = Domain.of(25, 26, 27, 29, 30);

    static class 人 {
        public final String 名前;
        public final Variable<Integer> 年齢;
        public final Variable<新生活> 新生活;
        public 人(Problem p, String 名前, Domain<Integer> 年齢, Domain<新生活> 新生活) {
            this.名前 = 名前;
            this.年齢 = p.variable(名前 + "の年齢", 年齢);
            this.新生活 = p.variable(名前 + "の新生活", 新生活);
        }
        public String toString(Result map) {
            return String.format("%s は %s才 で %s", 名前, map.get(年齢), map.get(新生活));
        }
    }

    人 セツオ = new 人(problem, "セツオ", すべての年齢, すべての新生活);
    人 イクミ = new 人(problem, "イクミ", すべての年齢, すべての新生活);
    人 カナコ = new 人(problem, "カナコ", すべての年齢, すべての新生活);
    人 シンイチ = new 人(problem, "シンイチ", すべての年齢, すべての新生活);
    人 ツキコ = new 人(problem, "ツキコ", すべての年齢, すべての新生活);

    @Override
    public void define() throws IOException {
        allDifferent(map(p -> p.年齢, セツオ, イクミ, カナコ, シンイチ, ツキコ));
        allDifferent(map(p -> p.新生活, セツオ, イクミ, カナコ, シンイチ, ツキコ));

        // セツオ：　　オレはこの春、結婚することになったんだ。
        constraint(eq(セツオ.新生活, 新生活.結婚));

        // イクミ：　　私はセツオより一つ年上よ。２５才で思い切って転職する人がいるのね。
        constraint(eq(イクミ.年齢, plus(セツオ.年齢, 1)));
        constraint(or(map(p -> and(eq(p.新生活, 新生活.転職), eq(p.年齢, 25)), セツオ, カナコ, シンイチ, ツキコ)));

        // カナコ：　　私は海外転勤になっちゃった。
        //        私はツキコとは１才違いで、セツオとは３才違いよ。
        constraint(eq(カナコ.新生活, 新生活.海外転勤));
        constraint(eq(abs(minus(カナコ.年齢, ツキコ.年齢)), 1));
        constraint(eq(abs(minus(カナコ.年齢, セツオ.年齢)), 3));

        // シンイチ：　カナコ姉さんはしっかりしていますよね。
        //        ところで、３０才の記念にペットを飼い始めたのは誰だっけ？
        constraint(lt(シンイチ.年齢, カナコ.年齢));
        constraint(or(map(p -> and(eq(p.新生活, 新生活.ペットを飼う), eq(p.年齢, 30)), セツオ, イクミ, カナコ, ツキコ)));
        
        // ツキコ：　　私もイクミ姉さんのように仕事頑張らないとね。
        constraint(lt(ツキコ.年齢, イクミ.年齢));

        logger.info("*** constraints ***");
        for (Constraint c : problem.constraints)
        	logger.info("constraint: " + c);
    }

    @Override
    public boolean answer(int n, Result result) throws IOException {
        logger.info(セツオ.toString(result));
        logger.info(イクミ.toString(result));
        logger.info(カナコ.toString(result));
        logger.info(シンイチ.toString(result));
        logger.info(ツキコ.toString(result));
        return true;
    }

}
