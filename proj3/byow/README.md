## Classes and Data Structures

### Classes

#### Main
the entrance of the program, including two ways to enter, `interactWithInputString` and `interactWithKeyboard()`

#### Engine
the engine to call the methods which is called by Main, helping to generate the World and move the avatar

#### IMapBuilder
the interface of building a World
- `buildMap()` to generate the World
- `getWorldMap()` to return the World map that has been generated.

#### BSPMapBuilder
the implements of `IMapBuilder`.
uses Binary Space Partitioning (BSP) to divide the map into smaller sections, creates rooms within these sections, 
and connects them with corridors to generate a structured, connected dungeon layout.
and then connect them with `Corridor`.

#### MapBuilderUtils
some utilities to help generate the World, including generate different shapes of room and different shapes of corridor.

#### WorldMap
the class that represents the World, with `width`, `height` and every tile of the World (`tiles`).

#### Avatar
the class that controls the avatar to move up, down, left, right validly and judge whether it's game over.

#### RandomUtils
some utilities to help generate random numbers

#### MyUtils
some utilities to help serialize the objects and some helper function for Engine.

#### UserInterface
the class that display some UserInterface(UI)
- `drawMenu()`      draw the Opening Menu
- `drawHUD()`       draw the Head Up Display
- `drawGameover()`  draw the Game over Menu

### Data Structures

#### Position
represents the (x, y) Position
- `x`, `y`     represent the axis x and y
- `distance(Position p)`   the Euclidean Distance between this position and p

#### Rectangle
represents the rectangular shape room (perhaps more than a room)
- `x`, `y`    represent the left bottom of the rectangle's axis, accessing with `getX()`, `getY()`
- `w`, `h`    represent the width and height of the rectangle, accessing with `getW()`, `getH()`
- `isOverlapped(Rectangle r)` judge if the rectangle is overlapped by the other rectangle r.
- `center()` return the center `Position` of the rectangle.


## Algorithms

Roguelike World Generation, referred by [another solution](https://github.com/cs-learning-every-day/cs61b-code-sp21.git).<br>
Using BSP(Binary Space Partitioning) Algorithm with the following steps:
1. split the rectangular areas recursively in the World tiles.
2. create rooms in the split areas.
3. connect rooms with corridors.
4. build the whole map with the following steps.


## Persistence
1. With the same seed, always generate the same World. 
2. With different seeds, never generate the same World. 