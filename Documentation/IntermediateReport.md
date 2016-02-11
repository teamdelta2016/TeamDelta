#Intermediate Report

####Most of the implementation groundwork is now complete - much of the remaining work will involve optimisations to reduce latency, refinements to logic, handling of corner cases and improving the user experience (as well as testing).
####The driving engine, user interface, route planner, image processor and image fetcher now function together to create a stripped-down version of the application that allows the viewer to choose a location, change processing parameters and will display one set of images. 
####The user cannot yet "drive" through the simulation.

##Anthoney - Image Fetcher

- Since the first meeting, Anthoney has been working on successfully fetching images from Google Street View. The image fetching module can now concurrently grab five images, corresponding to the left, front left, front right, right, and back windows of a car. Downloading and saving the images as BufferedImage objects takes about 1-2 seconds in total. 

- The Image fetcher now successfully saves the BufferedImage objects within an ImageInputSet object from the core module. The original design of the ImageInputSet object has been slightly optimised as well by removing the HashMap and directly using getters for each BufferedImage instead.

- Testing for this module includes making sure that the correct images have been fetched. Given random coordinates, the image fetcher has been successful in grabbing images in the proper orientation. It has also been confirmed to work well with the classes in the core module, most notably the ImageInputSet object. Finally, it appears that multiple consecutive calls to the Google Street View API does not noticeably degrade the speed of image fetching. Further testing will require interaction with the rest of the modules to determine whether the entire system is fully functional. 

##Sam - Image Processor

- The first few hours of development involved simply testing the OpenCV methods, and getting a feel for the Java bindings.

- He then started to create seperate methods for each image processing feature, and then moved these methods to static methods in helper classes, just to lay out the code more cleanly. He also made a Peripheral class that needed an instance as it stored circular masks in memory over the course of the image processors lifetime.

- Currently, functionality exists for image stitching, blur, peripheral darkness, double vision, night mode and headlamp glare.

- Testing the image processor just involved running manual tests with various street view images. The whole module isn't very object oriented due to the nature of the OpenCV library. Becuase of this, black box, white box and unit testing will be difficult.

##Basha - Route Planner

- Basha has been working on the RoutePlanner part of the project, which is responsible for determining where the car moves next, and detecting junctions given its current position and direction.

- Initial work was was concentrated on devising ways of getting the right data from OpenStreetMap, and after getting reasonable data out of it, the work was focused on the RoutePlanner itself. The RoutePlanner now returns the next position along the road, and if the next position is a junction, it also returns all directions to roads from given junction.

- Testing has involved checking how much and which data is downloaded for each query, testing helper functions for navigating on the map and testing the whole module on some test cases, assessing the result visually on a map.

##Alex - UI

- Alex has created a user interface for the user to choose a starting point to 'drive' around, and choose a set of image processing parameters to alter the Street View images with. So far, the following features are implemented:

	- Styled with CSS

	- Bi-directional navigation

	- Location entry and lookup using Google Geocoding API

	- Reverse-geocoded location to feedback to the user a more specific location than the one they typed in

	- Multiple screen version when there are multiple monitors available

	- Saving of location and image processing parameters between screens

	- Some hand-made graphics

	- Running tasks in non-UI thread

- Visual inspection and clicking through the UI, coupled with random key presses has been the majority of the testing done. Unit tests were made for the ScreenContainer class which is responsible for switching back and forth between screens.

##Michael - Driving Engine

- Michael has been working on tying all the other modules together

- The engine implementation can now construct and initialise various subsystems, and then query and re-package them for use in the UI.

- The UI has also been patched in order to replace the test data that was previously being used.

- Now the application can receive a location and display a scene with appropriate images processing for that location.

- Testing has largely involved simply running the UI to make sure the various features work.

- Since the subsystems have all been tested individually, all that is needed is to make sure their interaction works by testing the whole application.

##Anna - Various

- Since the last meeting, Anna has mainly been working on vehicle detection algorithms for the placing of headlamp glare. Unfortunately, this task now has a significantly lower priority since she and Sam discovered that simply placing glaring headlamps at the right point in the image provide 
a very satisfactory approximation to detecting the vehicles themselves. This is much better for latency.

- If time allows, she may return to this task at the end of the project, but for now has turned her attention to adding to the user interface.

- The loading screen has been modified to provide the user with facts and stastistics relating to driving and age, and a short tutorial is in progress.

