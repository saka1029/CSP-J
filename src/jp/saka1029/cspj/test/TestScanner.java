package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.Scanner;
import java.util.regex.Pattern;

import jp.saka1029.cspj.problem.old.Log;

import org.junit.Test;

public class TestScanner {

	static Pattern DELIMITER = Pattern.compile("[ \t:()]+");

	@Test
	public void test() {
		String str = "22 29: (12 11) (12 13)\r\n 1 2: \t (2 3) (4 5)\r\n";
		try (Scanner s = new Scanner(str)) {
            s.useDelimiter(DELIMITER);
            while (s.hasNext()) {
                int v1 = s.nextInt();
                int v2 = s.nextInt();
                Log.info("line v1=%s v2=%s", v1, v2);
                while (!s.hasNext("\r?\n")) {
                    int c1 = s.nextInt();
                    int c2 = s.nextInt();
                    Log.info("c1=%s c2=%s", c1, c2);
                }
                s.next();
            }
		}
	}

}
