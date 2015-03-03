package jp.saka1029.cspj.solver.jacop;

import java.util.Set;

import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.core.Domain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.core.Var;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SolutionListener;

public abstract class AnswerListener implements SolutionListener<IntVar> {

	public abstract boolean answer(Set<IntVar> variables);

	@Override public boolean assignSolution(Store arg0, int arg1) { return false; }
	@Override public boolean executeAfterSolution(Search<IntVar> search, SelectChoicePoint<IntVar> choice) {
		return !answer(choice.getVariablesMapping().keySet());
	}
	@Override public int findSolutionMatchingParent(int arg0) { return 0; } 
	@Override public int getParentSolution(int arg0) { return 0; } 
	@Override public Domain[] getSolution(int arg0) { return null; } 
	@Override public Domain[][] getSolutions() { return null; } 
	@Override public IntVar[] getVariables() { return null; } 
	@Override public boolean isRecordingSolutions() { return false; } 
	@Override public void printAllSolutions() { } 
	@Override public void recordSolutions(boolean arg0) { } 
	@Override public PrimitiveConstraint[] returnSolution() { return null; } 
	@Override public void searchAll(boolean arg0) { } 
	@Override public void setChildrenListeners(SolutionListener<IntVar>[] arg0) { } 
	@Override public void setChildrenListeners(SolutionListener<IntVar> arg0) { } 
	@Override public void setParentSolutionListener(SolutionListener<? extends Var> arg0) { } 
	@Override public void setSolutionLimit(int arg0) { } 
	@Override public boolean solutionLimitReached() { return false; }
	@Override public int solutionsNo() { return 0; }

}
