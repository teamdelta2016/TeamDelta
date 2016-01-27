#Project Plan

##User Interface
The user interface module is built upon the JavaFX framework from Oracle. It is the entry point of the program and instantiates all the other modules. 

There is a StackPane object which holds all the different screens in the UI (eg. Start screen, screen to pick location etc.). For each screen in the UI, loadScreen() gets called at the beginning, such that the layout hierarchies (specified in FXML files) are loaded ahead of the screen being displayed. Also, each loaded screen is added to a HashSet so they can be kept track of.

The starting screen is displayed with setScreen(),and switching screens is then a case of further calls to setScreen(), which pops a screen off the StackPane, and pushes a new one on.

Each screen has an associated layout controller which receives events such as button clicks or text input.

The ‘LocationScreen’ has a text input field into which the user types the address of where they would like to begin driving. When they click the ‘next’ button, the Google Geocoding Java API is queried with a static call to GeocodingApi.geocode(). Using method chaining a preferential region for results is also added (the UK), and the call is set to synchronous so that the process blocks until a result is obtained.

The ‘ParameterScreen’ has various sliders for adjusting the degree of the different image processing techniques employed. 

There is a singleton ‘LarrySettings’ object, which holds a Location object and an ImageParams object which have their data set as the user clicks through those respective screens. When the ‘RunningScreen’ is reached, the Driving Engine is constructed, and when the user chooses which direction to go in next, the UI calls nextFrame() on the driving engine. This returns the set of new images to be displayed.


##Driving Engine

##Route Planner

##Image Fetcher
The image fetcher module takes a JunctionInfo object, outputted from the Route Planner, and returns an ImageInputSet object 
of five BufferedImage objects, corresponding to the right, front right, front left, left, and back windows. We split the image
for the front window into two images because the maximum width and height are fixed at 400 and 640 pixels respectively, and 
the front window is naturally wider than the right and left windows. 

The image fetcher grabs images using the Google Street View API with a single function call, based on the input provided
by the JunctionInfo object. The parameters include the latitude, longitude, width, height, heading, field of view, and pitch.

Essentially, the function will first find the general location of the image based on the direction (which corresponds to the
heading), latitude, and longitude. Then, the images for the right, left, and back windows can be fetched as simple changes to 
the heading. The rest of the parameters, if not provided, will have default values. 

Each request to the Google Street View API is accompanied by an API Key, which is kept as a constant in a separate 
interface and is never committed when making changes on GitHub to ensure that our unique key for accessing the API is secure. 

Because each API request takes some time to complete, having users of our application wait for the next image to be fetched
would be unfeasible, so the image fetcher puts the fetched images in an ImageInputSet object, allowing us to prefetch and 
cache the images.


##Image Processor
The image processor module is a single class instance that runs a single method for every given ImageInputSet. This method itself calls the OpenCV native method via the OpenCV Java bindings, extracted from the OpenCV build.

The instance carries an image parameter object that specifies the parameters of the image processing. This includes the blur radius of the peripheral vision, the extend to which the peripheral is dimmed, the saturation of the output images and the sensitivity to various sources of glare.

These parameters are given in the constructor when the ImageProcessor instance is created and cannot be changed throughout the instance's lifetime. It is passed as an ImageParameter object.

The instance then has a 'process' method that accepts an ImageInputSet, performs the neccessary image filters based on the parameters, and gives back an ImageOutputSet. This process method will block for the duration of the processing and give back the results as the return value.

To achieve these effects we are going to use the OpenCV library. This is a native library written in C++ that we will then call using the prebuilt Java bindings. It lets us efficiently access and change the raw pixels of an image as well as apply various hardware accelerated filters.
