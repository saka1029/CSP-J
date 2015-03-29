package jp.saka1029.cspj.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Domain;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.solver.Result;
import jp.saka1029.cspj.solver.SolverMain;

/**
 * 明たち４人で競走しました。以下のヒントから、４人の服の色と順位を当ててください。 
 *  ・明の服の色は青。明は１位ではない。
 *  ・晋は緑の服ではなく、４位でもない。
 *  ・赤い服の人は２位。
 *  ・保は白い服より１つ順位が上(１位に近い)｡
 */
public class 推理パズル_4人で競争 extends SolverMain {

	static final Logger logger = Logger.getLogger(推理パズル_4人で競争.class.getName());
	
	enum 服の色 {青, 緑, 赤, 白 }

	static final Domain<服の色> 全ての服の色 = Domain.of(服の色.values());
	static final Domain<Integer> 全ての順位 = Domain.range(1, 4);
	
	static class 人 {

		final String 名前;
		final Variable<服の色> 服の色;
		final Variable<Integer> 順位;
		
		人(Problem problem, String 名前) {
			this.名前 = 名前;
			this.服の色 = problem.variable(名前 + "の服の色", 全ての服の色);
			this.順位 = problem.variable(名前 + "の順位", 全ての順位);
		}
		
		String toString(Result r) {
			return String.format("%s=%s %s=%s", 服の色, r.get(服の色), 順位, r.get(順位));
		}
		
	}

    final 人 明 = new 人(problem, "明");
    final 人 晋 = new 人(problem, "晋");
    final 人 保 = new 人(problem, "保");
    final 人 友 = new 人(problem, "友");
    final List<人> みんな = Arrays.asList(明, 晋, 保, 友);

	@Override
	public void define() throws IOException {
		// 服の色はそれぞれ異なる。
//		allDifferent(map(p -> p.服の色, 明, 晋, 保, 友));
		allDifferent(map(p -> p.服の色, みんな));
		// 順位はそれぞれ異なる。
//		allDifferent(map(p -> p.順位, 明, 晋, 保, 友));
		allDifferent(map(p -> p.順位, みんな));
		// ・明の服の色は青。明は１位ではない。
		constraint(eq(明.服の色, 服の色.青));
		constraint(ne(明.順位, 1));
        // ・晋は緑の服ではなく、４位でもない。
		constraint(ne(晋.服の色, 服の色.緑));
		constraint(ne(晋.順位, 4));
        // ・赤い服の人は２位。
//		constraint(or(map(p -> and(eq(p.服の色, 服の色.赤), eq(p.順位, 2)), 明, 晋, 保, 友)));
		constraint(or(map(p -> and(eq(p.服の色, 服の色.赤), eq(p.順位, 2)), みんな)));
        // ・保は白い服より１つ順位が上(１位に近い)｡
		constraint(or(map(p -> and(eq(p.服の色, 服の色.白), eq(保.順位, minus(p.順位, 1))), 明, 晋, 友)));
	}

	@Override
	public boolean answer(int n, Result result) throws IOException {
		for (人 p : みんな)
            logger.info(p.toString(result));
		return true;
	}

}
