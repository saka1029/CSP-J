package jp.saka1029.cspj.problem.old.singletype;

import java.util.List;

@FunctionalInterface
public interface ConstraintEncoder<T> {
	
	/**
	 * 制約をエンコードするときに呼び出されるコールバックインタフェースです。
	 * 
	 * @see Constraint#encode(Bind, boolean, ConstraintEncoder)
	 * 
	 * @param indices 制約が参照する変数に対応するドメイン上の要素のインデックスを返します。
	 *                リストのサイズは対象となる制約のvariablesと同じです。
	 *                このリストは読み取り専用です。
	 * @param values  制約が参照する変数に対応するドメイン上の要素を返します。
	 *                リストのサイズは対象となる制約のvariablesと同じです。
	 *                このリストは読み取り専用です。
	 */
	public void encode(List<Integer> indices, List<T> values);

}
