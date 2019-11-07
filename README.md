# Polix
A Library for Reactive Collections

## Overview
Polix is a Scala library that abstracts over consecutive changes made to an imaginary mutable collection.
The main goal of the library is to aid the usage of collections in Functional Reactive Programming (FRP).  

It loosely based on the design patterns shown in the 2013 Paper [Higher-Order Reactive Programming with Incremental Lists](https://link.springer.com/chapter/10.1007/978-3-642-39038-8_29),
by Ingo Maier and Martin Odersky, but it aims to be agnostic in regards to the streaming library used, instead of providing it's own implementation.

### Examples

Polix provides a set of types each representing a collection similar to those found in the `scala.collection` library.  
For example a Sequence can be represented by the `RSeq` type.
It's usage is analogous to scala's `Seq`, with the difference that each operator returns a view on the original `RSeq`,
with the operator applied to all changes made to that original collection.

```scala
val input:  VBuffer[Double, Subject]      = VBuffer[Double](PublishSubject)
val output: RSeq[Double, Observable]      = input.map(n => n * 2)
val result: Observable[RSeqEvent[Double]] = output.stream

result.foreach(println)

input += 1                       // => Append(2)
input += 2                       // => Append(4)
input.prepend(0)                 // => Prepend(0)
input.insert(2, 1.5)             // => Insertion(2, 3)
input ++= List(10, 11, 12)       // => AppendAll(List(20, 22, 24))
input.patch(4, List(5, 6, 7), 0) // => Patch(3, List(10, 12, 14), 0)
```

To input into an `RSeq` you can either create one directly from an `Observable[RSeqEvent]` using `RSeq.lift(source)`,
or you can use a `VBuffer` which is analogous to a `scala.collection.mutable.Buffer` and can be created by supplying any `Subject`.

## Warning
At the moment, the library is still very much a **Work in Progress** and serves mainly as a **Proof of Concept**
until a Structure is found that satisfies all points made in the **Design Goals** section.

## Design Goals
WIP
