JARS are in out/artifacts and were built with JDK 12

Welcome to the readme file for tilegame!

Contents:
1.The game
2.The map builder
3.The MetaSet tool
4.Technical Information

1. The Game
1a. What is it?
This is a tilemap based game, which means levels are made entirely out of tiles from a set, and includes a player, enemies, and randomly scoring gold piles from each slain foe.
1b. What do I do?
You can play the game by moving with WASD and holding U to put the sword out in front of you and I to drag it behind you. Also, space lets you roll, allowing you a short period of invincibility

2. The map builder
2a. What is it?
This is a tool I developed to be able to make levels for the game after compiling. It is very rough around the edges, because I really only developed it for myself so I could make levels.
2b. What do I do?
Once you have a file either selected or created ('New' means pick a tile image, 'Open' means pick a meta file), you can paint tiles onto the map, place characters (clicking them again deletes them) and in the properties tab resize the map and place the player when ready. Don't forget to save!

3. The MetaSet tool
3a. What is it?
Extremely barebones tool used to manipulate .meta files used by the program to describe a tileset. Allows modification of the tile name, material type, and whether or not it is filled.
3b. What do I do?
Load a tileset, then select tiles and fill in the information and save. The information will not automatically be written until the "Save Changes" button is clicked for each tile.

4. Technical Information
Tiles and characters outside of the viewport are not tracked or rendered. So far, no scaling options are available for the main game. Collisions are computed with consideration to objects' position in a tilemap, not on screen, meaning invisible shapes are produced and then checked for intersection every frame.