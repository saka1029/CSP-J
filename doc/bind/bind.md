Problem.bind()は何をしているか？
================================

モデル
------

![Problem.bindに関連するモデル](model.svg  "Problem.bindに関連するモデル")


interface Bind
--------------

```
public interface Bind {

    int size();
    Domain<?> get(Variable<?> key);
    void put(Variable<?> key, Domain<?> value);
    void fail(Variable<?> key, Domain<?> oldValue);

    Iterable<Variable<?>> variables();

}
```

Bind Problem.bind()
-------------------

1. すべての変数について既定のドメインをputする。
2. 全ての変数についてbind(bind)を呼ぶ。
   （bindが変更されたことを通知する）


```
    public Bind bind() {
        Bind bind = new BindMap();
        for (Variable<?> v : variables)
            bind.put(v, v.domain(bind));
        for (Variable<?> v : variables)
            v.bind(bind);
        return bind;
    }

```

Domain Variable.domain(Bind bind)
----------------------------

bindにDomainがあればそれを返す。
なければ既定のDomainを返す。
ただしトップレベルの場合で論理値の場合はTRUEを返す。
このメソッドはExpressionをオーバーライドしている。
（Constantの場合は常に既定のDomainを返す）

```
    @SuppressWarnings("unchecked")
    @Override
    protected Domain<T> domain(Bind bind) {
        Domain<T> d = get(bind);
        if (d == null) d = domain;
        if (referers.size() == 0 && d.isBoolean()) {
            if (!d.containsTrue())
                throw new IllegalArgumentException("top level domain does not contain true");
            d = (Domain<T>)Domain.TRUE;
        }
        return d;
    }
```

boolean Variable.bind(Bind bind)
-------------------

bindにおける自分自身のドメインを指定されたdomainに変更する。
ドメインを変更することによって影響を受ける変数がある場合は、
それらを再バインドする。
正常に変更できた場合はtrueを返す。
ドメインを変更することによって矛盾が生じる場合
（自分自身または影響を受ける変数のドメインが空集合となる場合）、
falseを返す。

```
    protected boolean bind(Bind bind) {
        return bind(domain(bind), bind);
    }
```

boolean Variable.bind(Domain domain, Bind bind)
-----------------------------------------------

このメソッドは抽象メソッド。
実装クラスはふたつある。

1. Var extends Variable
2. Constraint extends Variable

boolean Variable.put(Domain domain, Bind bind, Set<Variable> que)
------------------------------------------------------------

domainが空集合ならfalseをリターンする。
bindされているDomainの要素数と同じ要素数ならtrueを返す。
（putせずに成功する）
割り当てようとしているdomainの要素数が小さければ
bindにputして、referersをすべてキューに追加する。


```
   protected boolean put(Domain<T> domain, Bind bind, Set<Variable<?>> que) {
        int size = domain.size();
        if (size <= 0) {
            bind.fail(this, get(bind)); // Variable.domain(bind)と違って単にbindからgetする
            return false;
        }
        if (size >= get(bind).size()) return true;
        bind.put(this, domain);
        que.addAll(referers);
        return true;
    }
```

boolean Var.bind(Domain domain, Bind bind)
------------------------------------------

キュー（Variableの集合）を作成して
put(Domain, Bind, キュー)を呼ぶ。
putに失敗したらfalseを返す。
成功した場合はキューにあるすべての変数について
v.bind(Bind)を呼ぶ。


```
    @Override
    public boolean bind(Domain<T> domain, Bind bind) {
        Set<Variable<?>> que = newQue();
        if (!put(domain, bind, que))    // Variableで実装
            return false;
        return bindQue(que, bind);  // Variableで実装
    }
```

boolean Constraint.bind(Domain domain, Bind bind)
-------------------------------------------------

キュー（Variableの集合）を作成して
自分自身と引数のDomainをビルドする。
ビルドしたDomainでputする。
putに失敗したらfalseを返す。
成功したら、引数の内のすべての変数について
bind(ビルドしたDomain, bind)を呼ぶ。
このbindにひとつでも失敗したらfalseを返す。
すべて成功した場合はキューにあるすべての変数について
v.bind(Bind)を呼ぶ。


```
    @Override
    public boolean bind(Domain<T> domain, Bind bind) {
        Set<Variable<?>> que = newQue();
        DomainBuilder db = new DomainBuilder();
        db.build(domain, bind);
        if (!put(db.builder.build(), bind, que))    // Variableで実装
            return false;
        for (int i = 0, size = arguments.size(); i < size; ++i) {
            Expression<A> e = arguments.get(i);
            if (e instanceof Variable)
                if (!((Variable<A>)e).bind(db.argsBuilder.get(i).build(), bind))
                    return false;
        }
        return bindQue(que, bind);  // Variableで実装
    }
```

DomainBuilderの中では以下のようなことをやっている。

```
Domain domain; // 自分自身のDomain
Builder builder; // 自分自身のDomain
List<Domain> argsDomain // 引数のDomain
List<Builder> argsBuilder // 引数のBuilder
List<A> args    // functionに与える引数
```

argsDomainにある要素のすべての組み合わせについて
function.eval(args)を実行する。
結果がnullでなくて、domainに含まれる場合は
結果をbuilderに追加し、argsBuilderの各要素にもargsをそれぞれ追加する。
