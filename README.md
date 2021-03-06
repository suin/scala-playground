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
[`actor.example6.UnknownMessage`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example6/UnknownMessage.scala) | もしアクターが未知のメッセージを受信したらどうなるか？ <br> `runMain playground.actor.example6.UnknownMessage` 
[`actor.example7.WatchDeathOfActors`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example7/WatchDeathOfActors.scala) | 子アクターが死んだら通知を受け取る例 <br> `runMain playground.actor.example7.WatchDeathOfActors` 
[`actor.example8.EventStream`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/actor/example8/EventStream.scala) | Event Streamを使ったメッセージング <br> `runMain playground.actor.example8.EventStream` 
[`basic.EitherChain`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/EitherChain.scala) | Eitherをforでつなげる方法 <br> `runMain playground.basic.EitherChain` 
[`basic.HelloWorld`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/HelloWorld.scala) | Hello Worldを出力するだけのサンプル <br> `runMain playground.basic.HelloWorld` 
[`basic.Polymorphism`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/Polymorphism.scala) | Scalaのポリモーフィズム <br> `runMain playground.basic.Polymorphism` 
[`basic.ThreadsAndCPUs`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/ThreadsAndCPUs.scala) | ScalaがデフォルトでCPUの数しかスレッドを作らないことを確認できるサンプル <br> `runMain playground.basic.ThreadsAndCPUs` 
[`basic.WhatIsSuspiciousShadowing`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/basic/WhatIsSuspiciousShadowing.scala) | 安定識別子パターン (Stable Identifier Patterns) <br> `runMain playground.basic.WhatIsSuspiciousShadowing` 
[`ddd.ImmutableEntities`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/ddd/ImmutableEntities.scala) | ホテルの予約を例に、DDDでImmutableなEntityの実装サンプル <br> `runMain playground.ddd.ImmutableEntities` 
[`spray.example1.ServerSentEventsExample`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/spray/example1/ServerSentEventsExample.scala) | SprayでServer-Sent Eventsを実装するサンプル <br> `runMain playground.spray.example1.ServerSentEventsExample` 
[`spray.example2.Base64DecodingServiceExample`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/spray/example2/Base64DecodingServiceExample.scala) | spray-routingでBase64エンコードされたHTTPリクエストをデコードするサンプル <br> `runMain playground.spray.example2.Base64DecodingServiceExample` 
[`xml.AcceptNonStringAttributes`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/xml/AcceptNonStringAttributes.scala) | XML要素の属性にString以外を使う方法 <br> `runMain playground.xml.AcceptNonStringAttributes` 
[`xml.AddChildrenToXmlElement`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/xml/AddChildrenToXmlElement.scala) | XML要素に複数の子要素を追加するサンプル <br> `runMain playground.xml.AddChildrenToXmlElement` 
[`xml.AddDynamicallyAttributesToXmlElement`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/xml/AddDynamicallyAttributesToXmlElement.scala) | XML要素に動的に属性を追加する <br> `runMain playground.xml.AddDynamicallyAttributesToXmlElement` 
[`xml.XmlElementWithAnOptionalAttribute`](https://github.com/suin/scala-playground/blob/master/src/main/scala/playground/xml/XmlElementWithAnOptionalAttribute.scala) | オプショナルな属性を持つXML要素の作り方 <br> `runMain playground.xml.XmlElementWithAnOptionalAttribute` 

<!--end-->

## ライセンス

LICENSEを御覧ください。
