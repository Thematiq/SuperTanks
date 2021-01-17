# Super tanks <image src="screenshots/icon.png" width=64/>

<a href="https://github.com/apohllo/obiektowe-lab/blob/master/proj2/Czolgi_Superhot.md"> Second project for Object Oriented Programming </a>

---

# Game mechanics

In the game you control the tank having as the main objective destroying as many tanks as you can. You can rotate, move back and forth and shoot. Time only ticks when you move or shoot. Every tick there's a probability of 1/x generating new object, where x is the maximum interval between spawns.

Currently, there are two types of objects being spawned during the game. They are rocks, which are obstacles having 2 HP points, and enemy tanks with 1 HP. Enemy AI is quite simple, and every turn with 50\% probability they move towards the player or shoot in the closest direction to the player.

Bullets move 1 square each turn, until they hit an object, dealing one point of damage.

# Controls

- Left/Right - rotate tank,
- Up/Down - move forward/backward,
- Space - shoot.

# Game parameters

At the beginning you can choose parameters affecting game difficulty. These are: 

- Player HP - number of lives you have at a beginning,
- Enemy tank interval - maximum interval between spawning two tanks, equivalent to the 1/x of spawn probability,
- Obstacle interval - maximum interval between spawning two rocks, equivalent to the 1/x of spawn probability.