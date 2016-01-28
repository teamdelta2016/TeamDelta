#Specification

##Background & Research
As a wise old man once said,<sup>1</sup> “youth cannot know how age thinks and feels”. This statement is particularly applicable on the road – young drivers, cyclists and other road users are frequently unaware that older drivers may lack their reactions, motor-skills and eyesight and do not realise they need to take extra precautions around these drivers, for example wearing reflective clothing and using lights on a bicycle, or keeping their distance and dimming their headlights in a car. Through our product, we aim to address this problem by creating a simulation where a young person can view the road through the eyes of an elderly driver, using images from Google Street View.

It became quickly obvious that attempts to turn this idea into an actual driving simulator would be fairly futile. Interpolating between street view images to provide a smooth driving experience would be not only computationally very expensive, but also unrealistic since the images are spaced a few metres apart on most roads. The most troublesome aspect, however, was that approximately half of Street View's images are taken from the back of the vehicle, meaning that in simulation the user would appear to be driving on the wrong side of the road with the other cars reversing away from them – a situation which is obviously not ideal!

With this in mind, we decided it would be better to concentrate on the image-processing aspects of the brief, and intend to create less of a driving simulator and more of an alternative view of Street View. Clearly, this approach is much more suited to modelling the vision problems of an elderly driver rather reaction times or loss of hearing, and during our research we found this kind of image processing (including processing daytime images to appear as if they were taken at night) to be quite feasible. However, the system will maintain interactivity to make it more interesting for the user.

Finally, the brief suggested that the user should be able to drive as someone of a particular age. However, our research made clear that older people's vision problems can vary dramatically from person to person. While using “average values” is a possible solution, it can overgeneralise very individual health problems and could perpetuate misinformation. We have therefore decided to focus on specific vision problems, rather than varying an age parameter, although the project will still focus on elderly drivers.

##System Components and Requirements

We will build a desktop application which allows the user to click through images sourced from Google's Street View API, while applying various degrees of image processing to simulate the road through the eyes of an older person. This system will comprise three main components; User Interface, Driving Engine and Image Processing:

<sub><sup>1. Albus Dumbledore, <em>Harry Potter and the Order of the Phoenix, J.K. Rowling</em><sup><sub>

####User Interface
*This component displays images to the user and requests and processes their responses (e.g. advancing, changing the view or choosing a junction). It will be developed by Alex (approx. 15-20 hours) and Anna (approx. 5-10 hours).*

The user should be able to advance to the next location along the road with a new Street View image by holding the forwards arrow key (a “hop”).

The user interface should display the current location to the user.

At a junction, they should be able to choose which road to take. When appropriate, they will also be able to turn round.

The user will be able to view the windscreen view, and left and right, or switch to view the rear window.

Before the user enters the simulation, they should be able to choose their starting location by inputting a postal code, address or coordinates, choose between day and night mode, and vary the severity of assorted age-related vision problems.

The severity of each vision problem should be varied with a slider.

The user interface should be intuitive, and should be click and keyboard-controlled

It will include a short tutorial on first use which can be disabled or replayed by the user.

The user interface should also display facts and statistics relating to elderly drivers, for example while the simulation is loading.

####Driving Engine
*This component contains the system logic. It is reponsible for getting and processing route information, fetching images and feeding them to the image processor, and interacting with the user interface. The core engine will be developed by Michael (approx. 15-20 hours), while the route planner will be developed by Basha (approx. 15-20 hours) and the image fetcher by Anthoney (approx. 10-15 hours).*

The system will fetch and cache five images per location (two views from the front and one each from left, right and rear windows) along the expected path – this caching will reduce latency.

It will also analyse the road map to find the orientation of the road as well as anticipate junctions and dead ends.

If a location is close enough to a junction, the driving engine will instead snap to the junction's location to allow the user to make their junction decision at the appropriate position.

An approximate hop length should be inferred from the type of road.

To create a more realistic experience, and to allow users across the country to try the simulation in their local area, it should be possible to drive on any UK road which has street view images

####Image Processing
*This component will transform the raw images to a view of the road through the eyes of an elderly driving by applying appropriate algorithms to simulate vision problems. It will be developed by Sam (approx. 15-20 hours) and Anna (approx. 5-10 hours).*

The image processing component should simulate the following age-related vision problems: 
- Blurriness
- Loss of peripheral vision
- Loss of contrast and dullness of colours
- Increased sensitivity to glare, particularly from bright headlamps
- Double vision (ghosting)

Because older people can have particular difficulty driving in the dark if their vision has deteriorated, it will be possible to view the simulation in night-mode. The daytime images will be processed to have a twilight appearance and an ambient street-light effect should be added.
The night-mode will also add car headlights and an appropriate amount of glare to images.

To achieve these effects we are going to use the OpenCV library. This is a native library written in C++ that we will then call using the prebuilt Java bindings. It lets us efficiently access and change the raw pixels of an image as well as apply various hardware accelerated filters.

##Success Criteria

Ideally, we would successfully implement every item from the requirements. However, despite our best efforts in putting them together, it may be that some features turn out not to be feasible in the time allocated. Therefore, the following functional criteria must necessarily be met for the project to be considered a success:

- It allows the user to view Street View images from the vast majority of UK roads

- The system is interactive and allows the user to make junction decisions

- The image processor can simulate at least four of the specified vision problems and the user can vary their severity

- The user can view the simulation in “night-mode”

Additionally:

- This functionality must exist in a well-tested and well-documented state

- Latency from fetching and processing images must be minimised to such a degree that the user can progress through the simulation at a reasonable speed

- The quality of the image processing must provide a reasonably realistic simulation of vision problems faced by elderly people on the road

- The product as whole must provide educational value, helping younger people see the road from the perspective of an older driver and understand why they should be more aware of and sensitive to these road users

- The products should educate without being insensitive towards older people or those with disabilities and recognise that drivers of the same age may have vastly different standards of vision and awareness

####Management Success

Several further criteria for success must be applied relating to the management of the project

- Each team member must make an equal contribution to the project and should undertake all of the following activities: implementation, testing, writing documentation and reviewing the code of other team members

- Unless exceptional circumstances arise, we must meet all deadlines for deliverables and meetings

- The work should not take significantly more time than we have budgeted for it

#####Fulfilling all the criteria from this section would be sufficient to consider the project a success.

##Management Strategy

###Technical

We are using GitHub to manage our project repository. It is very well established, with strong community support and clear documentation, and each of us has prior experience using GitHub. Following best practices of software development, we are creating a separate feature branch for each of our modules, corresponding to the UI, the Engine, the Image Fetcher, the Route Planner, and the Image Processor, as well as a Core module for sharing helper classes between modules. Each pull request will be reviewed by two other team members, who are responsible for looking through the changes and making comments.

Only after the code has been approved will it be merged as part of the master branch, thus ensuring that new features can be integrated with the rest of the project while reducing the potential for introducing new bugs.

We will also do extensive testing, creating many unit tests as code is written to check that our functions behave as expected given certain inputs. Ideally, we will be testing our functions with typical inputs, extremes, and even null or illegal values. We will also manually test for stress/failure conditions using both Black Box and White Box Testing. Initially, the team member who wrote a piece of code is also responsible for writing tests for it, although by the end of the project all team members will be testing one another's code.

We aim to test the image-processing components with older drivers to see if they provide a realistic view of the road in their opinion.

The majority of our code will be written in Java, which provides great portability and means that our application can be used on a variety of operating systems. We will use the Intellij IDEA as our Java IDE.

To build our project, we will use Gradle, an open source system specialized for Java that supports incremental builds, which are important for the development cycle of our application. Gradle also allows us to link our modules together easily.

###Group Organisation

Anna Tindall is the team's project manager and will therefore be the point of contact between the team and the client and project organisers. She will also be responsible for the final compilation and sending of deliverables such as written reports, and will organize any necessary reallocation of work if our timing estimates turn out to be inaccurate.

Informal face-to-face team meetings will be held at least twice per week in the Computer Lab, and further discussion of the project will take place on our team page on Slack, which allows us to attach files, pin to-do lists and create new channels of discussion for different topics.

##Documentation

###End User

The product's user interface is designed to be very intuitive with fairly limited options user input. Consequently, a formal user's manual seems unnecessary. A short in-application tutorial on the first use should suffice; this will involve pop-up explanations of how to use the buttons and settings. 

###Technical

The project will involve multiple people working on distinct modules with interdependencies, so it is vital that all our code is well-documented as well as readable and understandable so that everyone's approach to the implementation is consistent. Each team member will annotate their code with JavaDoc comments, which can then be used to automatically generate documentation for each class. This means we can have rigourous documentation without expending unnecessary effort.  It is especially important that we document the image processing classes well, since these could be extended in the future. 

Team members working with external APIs (for example, Street View or OpenCV) should also document these thoroughly. Proper and consistent documentation should one of the criteria to check against during code reviews; this will make sure the whole team is documenting their code in a consistent way.

