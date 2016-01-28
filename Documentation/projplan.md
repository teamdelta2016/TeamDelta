#Project Plan

####This document describes the system's modules and their functions in more detail.
At the end of this document are descriptions of "core" classes used across modules.

##User Interface
The user interface module is built upon the JavaFX framework from Oracle. It is the entry point of the program and instantiates all the other modules. 

There is a StackPane object which holds all the different screens in the UI (eg. Start screen, screen to pick location etc.). For each screen in the UI, loadScreen() gets called at the beginning, such that the layout hierarchies (specified in FXML files) are loaded ahead of the screen being displayed. Also, each loaded screen is added to a HashSet so they can be kept track of.

The starting screen is displayed with setScreen(),and switching screens is then a case of further calls to setScreen(), which pops a screen off the StackPane, and pushes a new one on.

Each screen has an associated layout controller which receives events such as button clicks or text input.

The ‘LocationScreen’ has a text input field into which the user types the address of where they would like to begin driving. When they click the ‘next’ button, the Google Geocoding Java API is queried with a static call to GeocodingApi.geocode(). Using method chaining a preferential region for results is also added (the UK), and the call is set to synchronous so that the process blocks until a result is obtained.

The ‘ParameterScreen’ has various sliders for adjusting the degree of the different image processing techniques employed. 

There is a singleton ‘LarrySettings’ object, which holds a `Location` and an `ImageParams` which have their data set as the user clicks through those respective screens. When the ‘RunningScreen’ is reached, the Driving Engine is constructed, and when the user chooses which direction to go in next, the UI calls nextFrame() on the driving engine. This returns the set of new images to be displayed.


##Driving Engine

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

##Route Planner

The route planning module should provide interface for the driving engine that it can use to retrieve positional information required, such as the next `Location` and `Direction` of the car on the road, based on its `Location` and `Direction`. If the next position of the car is a junction, it should also provide the orientation of all the out-coming roads from there, so that it can be passed further to the UI to provide the user with a choice.

We plan to implement the Route Planner using the OpenStreetMap Overpass API. Depending on the latency of the API and data processing of it, it might be necessary to for the route planning module to be a little more complex, e.g. making decisions on which positions to precompute in order to decrease the latency. 

### Functions: 
```java
+JunctionInfo get_next_position(Coordinates current_position, int current_orientation, int speed, int time)
```
-This function takes the `Location` of the car on the road and the clockwise angle from North in degrees to specify which direction it is heading. It returns `JunctionInfo` of the next position that the car will be after the time specified as input if it continues along the road at the speed as specified. It should be possible to call this function without time and speed specified, as the speed might be infered from the type of road and the time might be fixed, once we see how fast is the rest of the API that we rely on.


##Image Fetcher
The image fetcher module takes a `JunctionInfo` object, outputted from the Route Planner, and returns an `ImageInputSet` 
of five BufferedImage objects, corresponding to the right, front right, front left, left, and back windows. We split the image
for the front window into two images because the maximum width and height are fixed at 400 and 640 pixels respectively, and 
the front window is naturally wider than the right and left windows. 

The image fetcher grabs images using the Google Street View API with a single function call, based on the input provided
by the `JunctionInfo` object. The parameters include the latitude, longitude, width, height, heading, field of view, and pitch.

Essentially, the function will first find the general location of the image based on the `Direction` (which corresponds to the
heading) and `Location`. Then, the images for the right, left, and back windows can be fetched as simple changes to 
the heading. The rest of the parameters, if not provided, will have default values. 

Each request to the Google Street View API is accompanied by an API Key, which is kept as a constant in a separate 
interface and is never committed when making changes on GitHub to ensure that our unique key for accessing the API is secure. 

Because each API request takes some time to complete, having users of our application wait for the next image to be fetched
would be unfeasible, so the image fetcher puts the fetched images in an `ImageInputSet`, allowing us to prefetch and 
cache the images.


##Image Processor

The image processor module is a single class instance that runs a single method for every given `ImageInputSet`. This method itself calls the OpenCV native method via the OpenCV Java bindings, extracted from the OpenCV build.

The instance carries an `ImageParams` object that specifies the parameters of the image processing. This includes the blur radius of the peripheral vision, the extend to which the peripheral is dimmed, the saturation of the output images and the sensitivity to various sources of glare.

These parameters are given in the constructor when the ImageProcessor instance is created and cannot be changed throughout the instance's lifetime. It is passed as an `ImageParams` object.

The instance then has a 'process' method that accepts an `ImageInputSet`, performs the neccessary image filters based on the parameters, and gives back an `ImageOutputSet`. This process method will block for the duration of the processing and give back the results as the return value.

To achieve these effects we are going to use the OpenCV library. This is a native library written in C++ that we will then call using the prebuilt Java bindings. It lets us efficiently access and change the raw pixels of an image as well as apply various hardware accelerated filters.


## Core Classes
These classes are shared between all modules of the project;
generally, they encapsulate data that need to be transferred
between modules.


### Location
Represents a latitude/longitude pair,
the standard location primitive for the project.

```java
+ Location(float latitude, float longitude)
+ float getLatitude()
+ float getLongitude()
```

### Direction
Represents a (planar) direction in polar coordinates.
Useful for e.g. which way the car is pointing.

```java
+ float getDegrees()
```

### JunctionInfo
Represents information about intersections of roads.

```java
+ Location loc
+ Set<Direction> directions
```

### ImageParams
Contains values of the different parameters for the image processing

```java
// exact parameters tbd
- double blurriness
- double darkness
etc.
```

### ImageInputSet
Contains images that have been retrieved from google,
but not yet stitched/processed into an `ImageOutputSet`.

```java
+ Image frontLeft
+ Image frontRight
+ Image left
+ Image right
+ Image back
```

### ImageOutputSet
Contains images that have been stitched and processed for display.
Notably contains one fewer image than an `ImageInputSet`,
as the front two images have been combined.

```java
+ Image front
+ Image left
+ Image right
+ Image back
```

### Frame
This represents a single render step for the UI:
it contains the images to display on the four "windows",
and information about the junctions (if any) that need to be displayed.

```java
+ ImageOutputSet images
+ JunctionInfo ji
```

