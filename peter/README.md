# Peter README

## Image Requirements

Images should be 640x640 (the max for street view) - we can change this if needs be.

The front images should have a FOV of 50 degrees, then the heading should be seperated
by exactly 50 degrees. The pitch should also be 0. Again, we can change if needs be.

Example queries for the front window images are:

left side: <https://maps.googleapis.com/maps/api/streetview?size=640x640&location=40.720032,-73.388354&fov=50&heading=220&pitch=0&key=AIzaSyBrpVNGTaa3ak5fZPxwccftUv413_EY010>

right side: <https://maps.googleapis.com/maps/api/streetview?size=640x640&location=40.720032,-73.388354&fov=50&heading=270&pitch=0&key=AIzaSyBrpVNGTaa3ak5fZPxwccftUv413_EY010>

