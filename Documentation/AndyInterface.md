# Interface: AndyInterface
## Functions: 
```java
+ ImageInputSet getNextImage(int width, int height, double latitude, double longitude, int fov, int heading, int pitch);
```
- This function is responsible for grabbing the next images to be processed by our image processor. The function makes 
an API call to the Google Street View API and grabs five images, corresponding to the left window,
front left window, front right window, right window, and back window of the car. 
The function will grab the next images based on the information provided by our route finder. It then will create a new 
object, ImageInputSet, that will contain these five images. We are currently having the ImageInputSet contain five Image objects because it will allow quicker access to the images than simply writing to disk, and it will be much eaiser to work 
with the Image objects compared to arrays of bytes. The function also contains sensitive information in the form of a unique
API_KEY for Google Street View, so the entire class implements a separate interface that defines the key, but that interface 
is never revealed to the public. 

## Parameters:
```java
+ int width - Value ranging from 0 to 400. Represents the width of the image. 
+ int height - Value ranging from 0 to 640. Represents the height of the image.
+ double latitude - Value representing the degrees in latitude of the image location.
+ double longitude - Value representing the degrees in longitude of the image location.
+ int fov - Value ranging from 0 to 120, representing the horizontal field of view of the image. 
Essentially represents the zoom, with smaller values adding more zoom.
+ int heading - Value ranging from 0 to 360 to represent the compass direction. 0 to 360 represent North,
90 is East, 180 is South, and 270 is West.
+ int pitch - Value ranging from -90 to 90, indicating the vertical angle of the camera, with 90 being 
straight upwards and -90 being straight downwards. 
```
# Helper Classes:

## ImageInputSet
```java
+ Image frontImageLeft;
+ Image frontImageRight;
+ Image leftWindow;
+ Image rightWindow;
+ Image backWindow;
```