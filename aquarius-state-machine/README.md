# aquarius-state-machine

State machine library.

**Create a state machine.**

```scala
StateMachine
  .builder()
  .init("a")
  .permit("a", "b", 1)
  .permit("a", "c", 2)
  .permit("b", "a", -1)
  .permit("b", "c", 1)
  .permit("c", "b", -1)
  .permit("c", "a", -2)
  .build()
```

**Specify onto action and transit action for states.**

```scala
StateMachine.builder()
  .init(1)
  .permit(1, 2, 1, (_: Seq[Any]) => s += "[1->2]")
  .permit(2, 3, 1, (_: Seq[Any]) => s += "[2->3]")
  .onto(2, () => s += "[onto 2]")
  .onto(3, () => s += "[onto 3]")
  .build()
```

**Using ```fire``` or ```forceState``` to change the state of state machine.**

```scala
stateMachine.fire(1).forceState(3)
```

When changing state, you can also specify arguments for transit action.<br />Notice that, arguments will be passed as a ```Seq``` of ```Any```. 

BTW, onto action has no arguments.

```scala
stateMachine.fire(
    1,
    "a"  // arg 1 for transit action
  ).forceState(
    3,
    "bc",  // arg 1 for transit action
    someObject  // arg 2 for transit action
  )
```

**Using ```getInitialState```, ```getLastState```, ```getCurrentState``` to show the status of state machine.**
