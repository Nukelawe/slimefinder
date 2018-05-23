# Software Requirements Specification (SRS)

## The purpose of the application
Slimefinder is an application for finding clusters of slime chunks in Minecraft. Its purpose is to find positions in a given Minecraft world where a specified number of slime chunks can be fit in the surrounding despawn sphere.

## Terminology

The Minecraft world is a 3-dimensional closed box space. It is 60 million blocks wide in both horizontal directions and 256 blocks tall vertically. Here _block_ is a unit of length 1. Depending on context it can also be used to represent an area or a volume. The coordinate axes are set up in such a way that y represents the vertical direction and x and z the horizontals. The origin of the space is at the bottom middle of the rectangle.

The world is divided into sections called _chunks_. A chunk is 16 blocks wide on the x and z axes and 256 on the y-axis (i.e. spans the whole vertical space).

A _slime chunk_ is a chunk that is capable of spawning a mob called the slime. There is a 10% chance for a chunk to be a slime chunk. Slime chunks are pseudorandomly chosen by a random number generator using a predefined seed, the _world seed_, that is unique to each world.

```
r.setSeed(seed + x * x * 4987142L + x * 5947611L + zChunk * zChunk * 4392871L + z * 389711L ^ 987234911L;
    	return r.nextInt(10) == 0;
```

_Despawn sphere_ is a spherical region of the Minecraft world with radius 128. If a player was positioned at its center all slimes outside it would instantly despawn.

_Exclusion sphere_ is a spherical region of the Minecraft world with radius 24. If a player was positioned at its center no slimes could spawn inside it.

_Mask_ is the region of space despawn sphere excluding the exclusion sphere, i.e. a spherical region with a small spherical cavity in the middle. If a mask is centered at P we say that the mask is at P. Since the slime chunk distribution only depends on the x and z coordinates we restrict ourselves to inspect the mask on a given altitude (y-coordinate) only. This effectively reduces the mask into a 2-dimensional disk.

_Mask size_ 

A _slime chunk cluster_ at position P is the set of slime chunks that overlap a mask at P.

_Chunk size_ of a slime chunk cluster is the number of slime chunks in the cluster that have a "sufficient proportion" of their blocks within the corresponding mask. A sufficient proportion is specified by the number, _minimum chunk weight_. If the number of blocks of the chunk within the mask is less than the minimum chunk weight the chunk is not counted towards the total chunk size of the cluster.

_Block size_ of a slime chunk cluster is the total number of blocks of slimechunk that belong to the corresponding mask.

## Functionality
* The application can be run in two modes, search and image generation modes.
* The mode is specified by a command line argument. Incorrect command line argumetns are 
* The configurations for the application are stored in 3 configuration files, 1 for image generation, one for search mode and one for general configurations.
* If a configuration file is missing a new one is generated with default values.

### Searching for clusters
The application must be able to check large amounts of positions on the Minecraft world to search for slime chunk clusters with a specified size.

* User must define which positions are checked when searching for slime chunk clusters by defining a search area. The search area is a square of a given width centered around a given position. The user must give both the width and the position of the square to define the search area.
* User must specify the shape of the mask that is used for determining the slime chunk clusters. To customize the shape the user can choose if he/she wants the exlusion sphere to be included. The y-coordinate at which the mask is calculated must be given by the user.
* User must define how he/she wants the chunk size of each slime chunk cluster to be calculated by giving a the minimum chunk weight.

Inline-style: 
![alt text](https://github.com/Nukelawe/otm/blob/master/project/resources/search-area.png "Search area")

### Visualizing the clusters graphically

## Further ideas for improvement
* Add support for finding specific patterns of slime chunks.
* Allow the user to define a custom shape for the mask.

