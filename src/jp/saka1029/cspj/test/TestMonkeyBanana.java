package jp.saka1029.cspj.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import jp.saka1029.cspj.problem.Bind;
import static jp.saka1029.cspj.problem.Helper.*;
import jp.saka1029.cspj.problem.Problem;
import jp.saka1029.cspj.problem.Variable;
import jp.saka1029.cspj.problem.Domain;
import jp.saka1029.cspj.solver.Solver;
import jp.saka1029.cspj.solver.basic.BasicSolver;

import org.junit.Test;

public class TestMonkeyBanana {

	static final Logger logger = Logger.getLogger(TestMonkeyBanana.class.getName());
    static { System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tFT%1$tT.%1$tL %4$s %5$s %6$s%n"); }

	enum Place { A, B, C }
	enum On { Floor, Box }
	enum Hand { Empty, Banana }
	
	static class State {
		final Place self; On on; Place box; Hand hand;
		State(Place self, On on, Place box, Hand hand) {
			this.self = self; this.on = on; this.box = box; this.hand = hand;
		}
		static Set<State> all() {
			Set<State> r = new HashSet<>();
			for (Place self : Place.values())
				for (On on : On.values())
					for (Place box : Place.values())
						for (Hand hand : Hand.values())
							r.add(new State(self, on, box, hand));
			return r;
		}
		@Override
		public int hashCode() {
			return Arrays.hashCode(new int[] {self.ordinal(), on.ordinal(), box.ordinal(), hand.ordinal()});
		}
		public boolean equals(Object obj) {
			if (!(obj instanceof State)) return false;
			State o = (State)obj;
			return self == o.self && on == o.on && box == o.box && hand == o.hand;
		}
		@Override
		public String toString() {
			return String.format("State(%s, %s, %s, %s)", self, on, box, hand);
		}
		/**
		 * change(grasp,                             % バナナをつかむ
                 state(b,box,b,empty),
                 state(b,box,b,banana)).
          change(climb,                             % 箱に乗る
                 state(Place,floor,Place,Hand),
                 state(Place,box,Place,Hand)).
          change(push(Place1,Place2),               % 箱をＰ１からＰ２へ押す
                 state(Place1,floor,Place1,Hand),
                 state(Place2,floor,Place2,Hand)).
          change(walk(Place1,Place2),               % Ｐ１からＰ２へ歩く
                 state(Place1,floor,Box,Hand),
                 state(Place2,floor,Box,Hand)).

          getbanana(state(_,_,_,banana)).         % 猿はバナナを入手した
          getbanana(State1):-
              change(Operate,State1,State2),      % Operateにより状態は変化した
              getbanana(State2).                  % 新たな状態について考える
		 */
//		boolean grab(State n) {
//            return self == Place.B && n.self == Place.B
//            	&& on == On.Box && n.on == On.Box
//            	&& box == Place.B && n.box == Place.B
//            	&& hand == Hand.Empty && n.hand == Hand.Banana;
//		}
//		boolean climb(State n) {
//            return self == n.self && self == box
//            	&& on == On.Floor && n.on == On.Box
//            	&& box == n.box
//            	&& hand == n.hand;
//		}
//		boolean push(State n) {
//            return self != n.self && self == box && n.self == n.box
//            	&& on == On.Floor && n.on == On.Floor
//            	&& box != n.box
//            	&& hand == n.hand;
//		}
//		boolean walk(State n) {
//            return self != n.self
//            	&& on == On.Floor && n.on == On.Floor
//            	&& box == n.box
//            	&& hand == n.hand;
//		}
		boolean goal() {
			return hand == Hand.Banana;
		}
//		boolean transition(State n) {
//			return grab(n) || climb(n) || push(n) || walk(n) || goal() && equals(n);
//		}
	}
	
	static interface Transition {
		abstract boolean transition(State f, State n);
	}
	
//	static class Grab implements Transition {
//		public boolean transition(State f, State n) {
//            return f.self == Place.B && n.self == Place.B
//            	&& f.on == On.Box && n.on == On.Box
//            	&& f.box == Place.B && n.box == Place.B
//            	&& f.hand == Hand.Empty && n.hand == Hand.Banana;
//		}
////		@Override public String toString() { return "Grab"; }
//	}
//	
//	static class Climb implements Transition {
//		public boolean transition(State f, State n) {
//            return f.self == n.self && f.self == f.box
//            	&& f.on == On.Floor && n.on == On.Box
//            	&& f.box == n.box
//            	&& f.hand == n.hand;
//		}
////		@Override public String toString() { return "Climb"; }
//	}
//	
//	static class Push implements Transition {
//        public boolean transition(State f, State n) {
//            return f.self != n.self && f.self == f.box && n.self == n.box
//            	&& f.on == On.Floor && n.on == On.Floor
//            	&& f.box != n.box
//            	&& f.hand == n.hand;
//		}
////		@Override public String toString() { return "Push"; }
//	}
//	
//	static class Walk implements Transition {
//		public boolean transition(State f, State n) {
//            return f.self != n.self
//            	&& f.on == On.Floor && n.on == On.Floor
//            	&& f.box == n.box
//            	&& f.hand == n.hand;
//		}
////		@Override public String toString() { return "Walk"; }
//	}
//
//	static class Goal implements Transition {
//		public boolean transition(State f, State n) {
//            return f.hand == Hand.Banana && f.equals(n);
//		}
////		@Override public String toString() { return "Goal"; }
//	}

	static abstract class NamedLambda<T> {
		public final String name; public final T t;
		public NamedLambda(String name, T t) { this.name = name; this.t = t; }
		@Override public String toString() { return name; } 
	}

	static class Trans extends NamedLambda<Transition> implements Transition {
		Trans(String name, Transition t) { super(name, t); }
		@Override public boolean transition(State f, State n) { return t.transition(f, n); }
	}

	Trans[] allTransitions = {
		new Trans("Grab", (f, n) -> f.self == Place.B && n.self == Place.B
            	&& f.on == On.Box && n.on == On.Box
            	&& f.box == Place.B && n.box == Place.B
            	&& f.hand == Hand.Empty && n.hand == Hand.Banana),
        new Trans("Climb", (f, n) -> f.self == n.self && f.self == f.box
            	&& f.on == On.Floor && n.on == On.Box
            	&& f.box == n.box
            	&& f.hand == n.hand),
        new Trans("Push", (f, n) -> f.self != n.self && f.self == f.box && n.self == n.box
            	&& f.on == On.Floor && n.on == On.Floor
            	&& f.box != n.box
            	&& f.hand == n.hand),
        new Trans("Walk", (f, n) -> f.self != n.self
            	&& f.on == On.Floor && n.on == On.Floor
            	&& f.box == n.box
            	&& f.hand == n.hand),
        new Trans("done", (f, n) -> f.hand == Hand.Banana && f.equals(n)),
	};
//			new Grab(), new Climb(), new Push(), new Walk(), new Goal() };
	
	@Test
	public void test() {
		int step = 20;
		State start = new State(Place.A, On.Floor, Place.C, Hand.Empty);
		Problem problem = new Problem();
		List<Variable<State>> variables = new ArrayList<>();
		List<Variable<Transition>> transitions = new ArrayList<>();
		variables.add(problem.variable("v0", Domain.of(start)));
		Domain<State> stateDomain = Domain.of(State.all());
		Domain<Transition> transitionDomain = Domain.of(allTransitions);
		for (int i = 1; i < step; ++i) {
			transitions.add(problem.variable("t" + i, transitionDomain));
			variables.add(problem.variable("v" + i, stateDomain));
		}
		for (int i = 1; i < step; ++i) {
			constraint("transition", (a, b, c) -> a.transition(b, c),
				transitions.get(i- 1), variables.get(i - 1), variables.get(i));
		}
//		problem.forAllNeighbors("transition", a -> a.get(0).transition(a.get(1)), variables);
		constraint("goal", a -> a.goal(), variables.get(step - 1));
		Bind bind = problem.bind();
		// 最短の手数を求めてそこにゴールを設定する。
		for (int i = step - 1; i > 0; --i) {
			Variable<? extends State> v = variables.get(i);
			for (State s : bind.get(v))
				if (s.goal()) {
					constraint("goal", a -> a.goal(), v);
					break;
				}
		}
		Solver solver = new BasicSolver();
		final int[] count = {0};
		solver.solve(problem, (result) -> {
			++count[0];
			for (Object s : result.values())
                logger.info(s.toString());
			return true;
		});
		assertEquals(1, count[0]);
	}

}
