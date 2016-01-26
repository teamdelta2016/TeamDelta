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
set parameters and gives back a set of 4 images in an ImageOutputSet
class. This function blocks until the result is given.


## Helper Classes:

### ImageParams
Yet to be fully decided, but simple parameters such as blur amount

### ImageOutputSet
```java
+ Image frontImage;
+ Image leftWindow;
+ Image rightWindow;
+ Image backWindow
```

### ImageInputSet
```java
+ Image frontImageLeft;
+ Image frontImageRight;
+ Image leftWindow;
+ Image rightWindow;
+ Image backWindow
```# Interface: EngineInterface
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
# Interface: RouteFinderInterface
## Functions: 
```java
+JunctionInfo get_next_position(Coordinates current_position, int current_orientation, int speed, int time)
```
-This function takes a current position (which contains a pair of longitude latitude) of the car on the road and the clockwise angle from North in degrees to specify which direction it is heading. It returns JunctionInfo of the next position that the car will be after the time specified as input if it continues along the road at the speed as specified. It should be possible to call this function without time and speed specified, as the speed might be infered from the type of road and the time might be fixed, once we see how fast is the rest of the API that we rely on.

## Helper Classes:

### Coordinates:
```java
+double longitude -Value from -180 to 180, longitude in degrees (180E to 180W)
+double latitude -Value from -90 to 90, latitude in degrees (90N to 90S)
```
### JunctionInfo:
```java
+Coordinates position -next position along the road
+boolean junction -true if the next position is a junction or dead end
+ArrayList<int> -angles (clockwise from north) in which the roads from the junction continue
```
##Route Planning Module

The route planning module should provide interface for the driving engine that it can use to retrieve positional information required, such as the next position and orientation of the car on the road, based on its current position and orientation. If the next position of the car is a junction, it should also provide the orientation of all the out-coming roads from there, so that it can be passed further to the UI to provide the user with a choice.

We plan to implement the Route Planner using the OpenStreetMap Overpass API. Depending on the latency of the API and data processing of it, it might be necessary to for the route planning module to be a little more complex, e.g. making decisions on which positions to precompute in order to decrease the latency. 
#Core Classes
## Location
Represents a lat/long location.

```java
+ float getLatitude()
+ float getLongitude()
```

## Direction
Represents the direction the car is facing.

```java
+ float getDegrees()
```
## JunctionInfo
```java
+ Location loc
+ HashSet<Direction> directions
```
## ImageInputSet
Contains all the images for displaying a frame.

```java
+ Image frontLeft
+ Image frontRight
+ Image left
+ Image right
+ Image back
```
## ImageOutputSet 
Contains all the processed images. 

```java
+ Image front
+ Image left
+ Image right
+ Image back
```
## Frame

```java
+ ImageOutputset images
+ JunctionInfo ji
```
