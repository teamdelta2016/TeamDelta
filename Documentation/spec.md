#Specification

##Requirements

We will build a desktop application which allows the user to click through images sourced from Google's Street View API, while applying various degrees of image processing to simulate the road through the eyes of an older person.

####User Interface

The user should be able to “jump” forward along the road by holding the up arrow key. At a junction, they should be able to choose which road to take. At any point, they will also be able to turn round.

The user will be able to switch between three views; front, left window and right window.

Before the user enters the simulation, they should be able to choose their starting location by inputting a postal code, address or coordinates, choose between day and night mode, and select the severity of various age-related vision problems.

####Driving Logic

The system will analyse the road map and anticipate junctions and dead ends.

It will also fetch and cache images (four per location) along the expected path to reduce latency.

####Image Processing

The age-related vision problems we have chosen to simulate are: blurriness, loss of peripheral vision, loss of contrast, increased sensitivity to glare and double vision (ghosting). 

Because older people can have particular difficulty driving in the dark if their vision has deteriorated, it will be possible to view the simulation in night-mode. The daytime images will be processed to have a twilight appearance and an ambient street-light effect should be added.

The night-mode will also add headlights and an appropriate amount of glare to images.
