# Interface: EngineInterface
## Functions:

```java
+ EventInterface(String location_query)
```

Constructor should query other subsystems to determine initial state,
then be ready start operation.

```java
+ Frame nextFrame(UserInput input)
```

Update the state of the engine depending on the user input:
e.g. go forwards a few metres if they pressed forward,
or down a junction if they chose a junction.
Then, return the `Frame` for the new state.

```java
+ Location getLocation()
+ Direction getDirection()
```

Retrieve location/direction: possibly useful for UI?

## Helper Classes:

### Frame
Encapsulates all the information that the UI needs to know in one object.

```java
+ ImageSet getImages()
```

Get the images from a frame for display.

```java
+ int numJunctions()
```

The number of junctions available at the current state: if 0,
we can only keep driving and no choice should be presented.
