# ByzantineProblem
Byzantine Oral Protocol java Implementation

Chinese blog: https://www.jianshu.com/p/d399e8d8dff8

T is Traitor, L is Loyal, * is Master

Run with Main class:

3(L)* send 我给0发送3的值是ATTACK

3(L)* send 我给1发送3的值是ATTACK

3(L)* send 我给2发送3的值是ATTACK

2(L)  send 我给0发送2听到的3的值是ATTACK

2(L)  send 我给1发送2听到的3的值是ATTACK

0(L)  send 我给1发送0听到的3的值是ATTACK

0(L)  send 我给2发送0听到的3的值是ATTACK

1(T)  send 我给0发送1听到的3的值是ATTACK

1(T)  send 我给2发送1听到的3的值是RETREAT

0(L) :ATTACK

1(T) :ATTACK

2(L) :ATTACK

3(L)* :ATTACK
