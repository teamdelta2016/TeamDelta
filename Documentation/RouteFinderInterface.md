# Interface: RouteFinderInterface
## Functions: 

+JunctionInfo get_next_position(Coordinates current_position, int current_orientation, int speed, int time)
-This function takes a current position (which contains a pair of longitude latitude) of the car on the road and the clockwise angle from North in degrees to specify which direction it is heading. It returns JunctionInfo of the next position that the car will be after the time specified as input if it continues along the road at the speed as specified. It should be possible to call this function without time and speed specified, as the speed might be infered from the type of road and the time might be fixed, once we see how fast is the rest of the API that we rely on.

## Helper Classes:

### Coordinates:
+double longitude -Value from -180 to 180, longitude in degrees (180E to 180W)
+double latitude -Value from -90 to 90, latitude in degrees (90N to 90S)

### JunctionInfo:
+Coordinates position -next position along the road
+boolean junction -true if the next position is a junction or dead end
+ArrayList<int> -angles (clockwise from north) in which the roads from the junction continue
