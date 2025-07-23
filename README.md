This project contains the code for reverse visibility.
- The main function is inside "MainAlgorithm" package.
- The package "esmaieeli/gisFastLocationOptimization" is for reading OpenStreetMap data and connect streets and junctions.
- There are various testing codes for various functionalities of the project. For instance, testing the shadows for one building. These tests are in package "testPrototypes".
- The shader codes are in the assets folder.

There are numerious known issues:
1. The convex hull of shadow needs to be calculated for each triangle of building but not the whole building. The current code creates all shadows for a building for start of a street segment and then all shadows for the end of the segment and then takes the convex hull. It needs to be changed to take convex hull for each triangle of a building.
2. No proper Breadth-first search was implemented for moving through the streets. Now the code just checks if a junction has all needed inputs to calculate the reverse visibility of the junction. This is not a problem if there are no significant back turns from north to south in the city.
3. Some output textures start to corrupt as the algorithm progresses to the point that it starts to output from main camera framebuffer instead of the fake camera. This is because Java accesses GPU by C bindings that are not memory safe. So if an issue happens with the framebuffer addresses, Java won't be able to catch it.

Due to the above issues, the final output of the algorithm was calculated manually by using just the reverse visibility of each street segment. Therefore, this code currently can't output nice results automatically but only provides the raw material to run the algorithm externally.
