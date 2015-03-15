package jp.saka1029.cspj.problem.old.singletype;

import java.util.logging.Logger;

public class SimpleSolver<T> {

	private static final Logger logger = Logger.getLogger(SimpleSolver.class.getName());
	
	private Problem<T> problem;
	private Answer<T> answer;
	private int answerCount;
	
	Variable<T> select(Bind<T> bind) {
		Variable<T> selected = null;
		int minsize = Integer.MAX_VALUE;
		for (Variable<T> v : problem.variables) {
			Domain<T> d = bind.get(v);
			int size = d.size();
			if (size <= 0)
				throw new RuntimeException("domain of variable (" + v + ") is empty");
			else if (size == 1)
				continue;
			else if (size < minsize) {
				selected = v;
				minsize = size;
			}
		}
		return selected;
	}

	boolean solve(Bind<T> bind) {
      boolean b = true;
      Variable<T> selected = select(bind);
      if (selected == null) {
          ++answerCount;
          b = answer.answer(new Result<>(problem, bind));
      } else
          for (Domain<T> d : bind.get(selected).singleDomains()) {
//        	  logger.info("try: " + selected + " = " + d);
              Bind<T> newBind = new Bind<>(bind);
              if (selected.bind(d, newBind))
                  b = solve(newBind);
              if (!b) break;
          }
      return b;
	}

	public int solve(Problem<T> problem, Answer<T> answer) {
		long startTime = System.currentTimeMillis();
		this.problem = problem;
		this.answer = answer;
		this.answerCount = 0;
		Bind<T> bind = problem.bind();
		long reducedTime = System.currentTimeMillis();
//		logger.info("*** reduced variables ****");
//		for (Variable<T> x : problem.variables())
//            logger.info(x + " : " + bind.get(x));
		logger.info("*** start solver ****");
        solve(bind);
        long solvedTime = System.currentTimeMillis();
        logger.info("SimpleSolver: answers=" + answerCount
        	+ " reduce=" + (reducedTime - startTime) + "ms"
        	+ " solve=" + (solvedTime - reducedTime) + "ms"
        	+ " total=" + (solvedTime - startTime) + "ms");
        return answerCount;
	}
}
