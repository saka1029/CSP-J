SAT encoding
============

Encoding types
--------------

- sparse encoding (Jeavons and Petke 2012) was adopted, among others, by the
- direct encoding (De Kleer 1989; Walsh 2000)
- support encoding (Gent 2002)
- order encoding (Bailleux and Boufkhad 2003; Ansotegui and Many ´ a 2004; Crawford and ` Baker 1994)

The sparse encoding
-------------------

CSPの変数```V : { 1, ... , n}```に対して、n個の論理変数 ```d1, ... dn``` を対応させる。
論理変数d1, ... dnはいずれかひとつだけが真となり、他はすべて偽となる。

The order encoding
------------------

CSPの変数```V : { 1, ... , n}```に対して、n-1個の論理変数```d1, ... , dn-1```を対応させる。
Vのドメインにおけるn番目の要素は```d1, ... dn-1``` の先頭のn-1個が真で他は偽であると表現する。

```
V : {1, 2, 3, 4, 5}
V = 1 の場合 [d1, d2, d3, d4] は [F, F, F, F]
V = 2 の場合 [d1, d2, d3, d4] は [T, F, F, F]
V = 3 の場合 [d1, d2, d3, d4] は [T, T, F, F]
V = 4 の場合 [d1, d2, d3, d4] は [T, T, T, F]
V = 5 の場合 [d1, d2, d3, d4] は [T, T, T, T]
```

V ≠ 3 は d2 = d3 と表現できる。
V in {2, 3} は d1 = T and d3 = F と表現できる。
