package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import jp.saka1029.cspj.main.TypeInference;
import jp.saka1029.cspj.main.TypeInferenceIdent;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import org.junit.Test;

public class TestTypeInference {

	static Logger logger = Logger.getLogger(TestMain.class.getName());
	
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n");
    }

	void methodName() {
        StackTraceElement s = Thread.currentThread().getStackTrace()[2];
        logger.info("<<<<< " + s.getClassName() + "#" + s.getMethodName() + " >>>>>");
	}

	@Test
	public void testTypeInference() throws IOException {
		methodName();
		assertEquals(1, new TypeInference().solver(new BasicSolver()).solve());
	}

	@Test
	public void testTypeInferenceIdent() throws IOException {
		methodName();
		assertEquals(3, new TypeInferenceIdent().solver(new BasicSolver()).solve());
	}

}
