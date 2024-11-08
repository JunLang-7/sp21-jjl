# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:
By using formulation to get the next right position with the method nextRight()<br>
I don't really understand it until I search for implements in Github, which inspires me a lot, and I believe I have organised it well. 
-----

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:
Yes, the hexagon is somehow like the room and hallway in different shape, and the nextRight() method is the same thing to ensure each room and hallway is not isolated forever.

-----
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:
Maybe just to organize like the nextRight() method with just interface, which doesn't take so many details but help better understand and link the program.

-----
**What distinguishes a hallway from a room? How are they similar?**

Answer:
Difference:Maybe the width, which hallway has a limitation but room does not, and the hallway can turn while room are just specific shape<br>
Common: they both have to link each other, and all must reachable.