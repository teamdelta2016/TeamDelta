# Core Classes
These classes are shared between all modules of the project;
generally, they encapsulate data that need to be transferred
betwixt modules.

## Location
Represents a latitude/longitude pair,
the standard location primitive for the project.

```java
+ Location(float latitude, float longitude)
+ float getLatitude()
+ float getLongitude()
```

## Direction
Represents a (planar) direction in polar coordinates.
Useful for e.g. which way the car is pointing.

```java
+ float getDegrees()
```

## JunctionInfo
Represents information about intersections of roads.

```java
+ Location loc
+ Set<Direction> directions
```

## ImageParams
Contains values of the different parameters for the image processing

```java
// exact parameters tbd
- double blurriness
- double darkness
etc.
```

## ImageInputSet
Contains images that have been retrieved from google,
but not yet stitched/processed into an `ImageOutputSet`.

```java
+ Image frontLeft
+ Image frontRight
+ Image left
+ Image right
+ Image back
```

## ImageOutputSet
Contains images that have been stitched and processed for display.
Notably contains one fewer image than an `ImageInputSet`,
as the front two images have been combined.

```java
+ Image front
+ Image left
+ Image right
+ Image back
```

## Frame
This represents a single render step for the UI:
it contains the images to display on the four "windows",
and information about the junctions (if any) that need to be displayed.

```java
+ ImageOutputSet images
+ JunctionInfo ji
```

# Interface: PeterInterface
## Functions:

```java
+ Constructor(ImageParams params);
```

Constructor to set the image processing parameters.

```java
+ ImageOutputSet process(ImageInputSet input);
```

process takes an input of 5 images, processes them based on internally
set parameters and gives back a set of 4 images in an `ImageOutputSet`
class. This function blocks until the result is given.


# Interface: EngineInterface
This module contains the `Engine` class,
the object which contains logic for driving the car
and orchestrating the other modules.

As it is the only object that knows about the state of the system,
it is also responsible for pre-caching results from nearby points
in order to improve performance.

```java
+ EventInterface(JunctionInfo locDir, ImageParams params)
```

The engine constructor should construct the other modules with this initial data.
For example, Peter should be constructed with `params`

```java
+ Frame nextFrame(...)
```

When the UI calls this function,
the engine should return a `Frame` for it to render, hopefully pre-cached.

Additionally, the engine should update the state of the engine
depending on the user input: e.g. go forwards a few metres if they pressed forward,
or down a junction if they chose a junction.

# Interface: RouteFinderInterface
## Functions: 
```java
+JunctionInfo get_next_position(Coordinates current_position, int current_orientation, int speed, int time)
```
-This function takes the `Location` of the car on the road and the clockwise angle from North in degrees to specify which direction it is heading. It returns `JunctionInfo` of the next position that the car will be after the time specified as input if it continues along the road at the speed as specified. It should be possible to call this function without time and speed specified, as the speed might be infered from the type of road and the time might be fixed, once we see how fast is the rest of the API that we rely on.

##Route Planning Module

The route planning module should provide interface for the driving engine that it can use to retrieve positional information required, such as the next `Location` and `Direction` of the car on the road, based on its `Location` and `Direction`. If the next position of the car is a junction, it should also provide the orientation of all the out-coming roads from there, so that it can be passed further to the UI to provide the user with a choice.

We plan to implement the Route Planner using the OpenStreetMap Overpass API. Depending on the latency of the API and data processing of it, it might be necessary to for the route planning module to be a little more complex, e.g. making decisions on which positions to precompute in order to decrease the latency. 
