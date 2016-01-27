#Project Plan

##User Interface

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
