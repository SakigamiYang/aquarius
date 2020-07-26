# aquarius-state-machine

State machine library.

**Create a state machine.**

```scala
StateMachine
  .builder()
  .initState("a")
  .addState("a")
  .addState("b")
  .addState("c")
  .addTransmission("a", 1, "b")
  .addTransmission("a", 2, "c")
  .addTransmission("b", -1, "a")
  .addTransmission("b", 1, "c")
  .addTransmission("c", -1, "b")
  .addTransmission("c", -2, "a")
  .build()
```

**Specify enter action and exit action for states.**

```scala
StateMachine.builder()
  .initState(1)
  .addState(1,
    (enterArgs1: Seq[(Type, Any)]) => s += s" enter 1 (+${enterArgs1.length} params)",
    () => s += " exit 1")
  .addState(2,
    (enterArgs2: Seq[(Type, Any)]) => s += s" enter 2 (+${enterArgs2.length} params)",
    () => s += " exit 2")
  .addState(3,
    (enterArgs3: Seq[(Type, Any)]) => s += s" enter 3 (+${enterArgs3.length} params)",
    () => s += " exit 3")
  .addTransmission(1, 1, 2)
  .addTransmission(2, 1, 3)
  .build()
```

**Using ```fire``` or ```forceState``` to change the state of state machine.**

```scala
stateMachine.fire(1).forceState(3)
```

When changing state, you can also specify arguments for enter action.<br />Notice that, arguments will be passed as a ```Seq``` of ```Tuple[Type, Any]```. 

BTW, Exit action has no arguments.

```scala
stateMachine.fire(
    1,
    (typeOf[Int], 1)
  ).forceState(
    3,
    (typeOf[Int], 1),
    (typeOf[String], "a")
  )
```

**Using ```getInitialState```, ```getLastState```, ```getCurrentState``` to show the status of state machine.**
