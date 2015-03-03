Java CSP interface
==================

JaCop
-----
JaCopにはExtensionalSupportとExtensionalConflict constraintsがある。
これは組み合わせ可能な値を配列の配列で表現する制約である。

```java
IntVar a = new IntVar(store, "a", 0, 1); 
   IntVar b = new IntVar(store, "b", 0, 1); 
   IntVar c = new IntVar(store, "c", 0, 1); 
   IntVar[] v = {a, b, c}; 
   // version with ExtensionalSupport constraint 
   store.impose(new ExtensionalSupportVA(v, 
                                         new int[][] {{0, 0, 0}, 
                                                      {0, 1, 1}, 
                                                      {1, 0, 1}, 
                                                      {1, 1, 0}}));
```
ExtensionalSupportは許容できる組み合わせを、
ExtensionalConflictは許容できない組み合わせを与える。


Choco
-----

ChocoにはTable制約がある。

```java
public void testtable1() {
        Solver solver = new Solver();
        IntVar X = VF.enumerated("X", 0, 5, solver);
        IntVar Y = VF.enumerated("Y", -1, 3, solver);
        Tuples tuples = new Tuples(true);
        tuples.add(1, -2);
        tuples.add(1, 1);
        tuples.add(4, 2);
        tuples.add(1, 4);
        Chatterbox.showSolutions(solver);
 ```