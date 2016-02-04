##Route Planning Module

The route planning module should provide interface for the driving engine that it can use to retrieve positional information required, such as the next position and orientation of the car on the road, based on its current position and orientation. If the next position of the car is a junction, it should also provide the orientation of all the out-coming roads from there, so that it can be passed further to the UI to provide the user with a choice.

We plan to implement the Route Planner using the OpenStreetMap Overpass API, which provides access to OpenStreetMap data, in the form of XML dumps, containig required information such as the shape of the roads, from which we should be able to find next position of the car along the road, as well as the junctions.

Depending on the latency of the API and data processing of it, it might be necessary to for the route planning module to be a little more complex, e.g. making decisions on which positions to precompute in order to decrease the latency. 
