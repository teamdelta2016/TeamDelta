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
