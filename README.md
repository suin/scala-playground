# Scala Playground

Scalaを勉強するときや、スニペットを手軽に試すためのリポジトリです。

## インストール

Java8をインストールしておいてください。

## 使い方

```
./activator
> runMain <実行したいクラス>
```

## サンプル一覧

<!--begin-->
 クラス | 説明と実行コマンド
-------|----------------
[`actor.example1.SimpleExample`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example1/SimpleExample.scala) | Actorにメッセージを送信するシンプルな例 <br> `runMain playground.actor.example1.SimpleExample` 
[`actor.example2.ChatModelExample`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example2/ChatModelExample.scala) | チャットを例に集約ルートのActorを実行時に生成する方法 <br> `runMain playground.actor.example2.ChatModelExample` 
[`actor.example3.ActorLoggingExample`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example3/ActorLoggingExample.scala) | アクターでログを出力する方法 <br> `runMain playground.actor.example3.ActorLoggingExample` 
[`actor.example4.ChatModelExample2`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example4/ChatModelExample2.scala) | ChatModelExampleに表明を加えたバージョンです <br> `runMain playground.actor.example4.ChatModelExample2` 
[`actor.example5.TimeoutAsk`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example5/TimeoutAsk.scala) | アクターのaskでタイムアウトを超過した場合どうなるかを試せるサンプル <br> `runMain playground.actor.example5.TimeoutAsk` 
[`basic.HelloWorld`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/HelloWorld.scala) | Hello Worldを出力するだけのサンプル <br> `runMain playground.basic.HelloWorld` 

<!--end-->

## ライセンス

LICENSEを御覧ください。
