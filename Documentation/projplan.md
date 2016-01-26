#Project Plan

##User Interface

##Driving Engine

##Route Planner

##Image Fetcher

##Image Processor
The image processor module is a single class instance that runs a single method for every given ImageInputSet. This method itself calls the OpenCV native method via the OpenCV Java bindings, extracted from the OpenCV build.

The instance carries an image parameter object that specifies the parameters of the image processing. This includes the blur radius of the peripheral vision, the extend to which the peripheral is dimmed, the saturation of the output images and the sensitivity to various sources of glare.

These parameters are given in the constructor when the ImageProcessor instance is created and cannot be changed throughout the instance's lifetime. It is passed as an ImageParameter object.

The instance then has a 'process' method that accepts an ImageInputSet, performs the neccessary image filters based on the parameters, and gives back an ImageOutputSet. This process method will block for the duration of the processing and give back the results as the return value.

To achieve these effects we are going to use the OpenCV library. This is a native library written in C++ that we will then call using the prebuilt Java bindings. It lets us efficiently access and change the raw pixels of an image as well as apply various hardware accelerated filters.
