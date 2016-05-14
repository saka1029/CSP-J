CSP-J
=====

概要
----

CSP-JはJavaで記述された制約充足問題のソルバーです。

制約充足問題
------------

Constraint satisfaction problems (CSPs) are mathematical problems defined as a set of objects
whose state must satisfy a number of constraints or limitations.
制約充足問題（せいやくじゅうそくもんだい、英: Constraint satisfaction problem, CSP）は、
複数の制約条件を満たすオブジェクトや状態を見つけるという数学の問題です。
CSPs represent the entities in a problem as a homogeneous collection of finite constraints over variables,
which is solved by constraint satisfaction methods.
CSPは変数に対する有限個の制約
CSPs are the subject of intense research in both artificial intelligence and operations research,
since the regularity in their formulation provides a common basis to analyze
and solve problems of many seemingly unrelated families.
CSPs often exhibit high complexity, requiring a combination of heuristics
and combinatorial search methods to be solved in a reasonable time. The Boolean satisfiability problem (SAT),
the satisfiability modulo theories (SMT) and answer set programming (ASP)
can be roughly thought of as certain forms of the constraint satisfaction problem.


定義
----

Formally, a constraint satisfaction problem is defined as a triple \langle X,D,C \rangle, where [2]
制約充足問題はX, D, Cの組であらわされます。

`X = {X1, ... ,Xn}` は変数の集合です。<br>
`D = {D1, ... , Dn}` は値域の集合です。<br>
`C = {C1, ... , Cm}` は制約の集合です。<br>

Each variable X_i can take on the values in the nonempty domain D_i.
変数`Xi`は値域`Di`の値になることができます。
Every constraint C_j \in C is in turn a pair \langle t_j,R_j \rangle, where t_j \subset X is a subset of k variables
制約`cj`はペア`<tj, Rj>`であらわすことができます。
`tj`は`X`の部分集合であるk個の変数です。
`Rj`は
and R_j is an k-ary relation on the corresponding subset of domains D_j.
An evaluation of the variables is a function from a subset of variables
to a particular set of values in the corresponding subset of domains.
An evaluation v satisfies a constraint \langle t_j,R_j \rangle if the values assigned
to the variables t_j satisfies the relation R_j.

An evaluation is consistent if it does not violate any of the constraints.
An evaluation is complete if it includes all variables.
An evaluation is a solution if it is consistent and complete;
such an evaluation is said to solve the constraint satisfaction problem.



