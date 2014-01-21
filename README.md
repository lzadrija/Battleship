#Battleship  


![Battleship](http://static3.wikia.nocookie.net/__cb20120303020434/battleship/images/f/fd/Battleship-1.jpg)

Battleship is a simple Java application for finding the solution of the [Battleship guessing game][1]. 


##Problem description


The player needs to find all the ships in the two-dimensional sea map. The two-dimensional sea is Y places wide and X places long. There are four ships positioned somewhere on the map, and each ship has a unique namw and form:
1. Petar Kresimir

   > ```` 
    X
    X  
    X  
    X````

2. TIE fighter

    >````  
    X       X
    X   X   X  
    X       X````
    
3. X Wing

    >````
    X       X  
        X  
    X       X````
    
4. Y Wing

    >````
    X        X  
    X        X  
        X  ````

It is important to stress that ships cannot be rotated, and therefore placed horizontally or diagonally.

The game is played by calling the service written using Google Apps Script. In order to sign up for the game, the user must first obtain [the token][2]. This token is used later on in all the calls to the service.  
The length and the width of the sea can be retrieved using the following calls to the service:
* https://script.google.com/macros/s/AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?token=YOUR_TOKEN_HERE&method=GetRowCount
* https://script.google.com/macros/s/AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?token=YOUR_TOKEN_HERE&method=GetColumnCount  

To find out what is located in the sea on some position, for example, on the position with the coordinates 1 and 2, the user must use this link:  
https://script.google.com/macros/s/AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?token=YOUR_TOKEN_HERE&method=GetCellAt&row=1&col=2  
This call can have two possible results:
* 0 - nothing is located at this position (the user has hit the sea)
* 1 - a ship is located (the user hit some part of the ship)  

In all of the previously listed links, "YOUR_TOKEN_HERE" must be replaced with the retrieved token.


##Solution


In order to find the solution for the game, two algorithms were implemented. The first algorithm is used for locating the point on the map at which some part of the ship is located. When such a point is found, the second algorithm is used to eliminate and identify all ships located in the area of this point. These two algorithms are applied interchangeably until locations of all four ships are found.

###Locating the ships 

Algorithm for locating ships is based on probability. We iterate through each not-yet-sunk ship and try to place it on every unidentified position on the map. Unidentified positions are the ones that are not yet fired at. Every time it is possible for a ship to be placed in over an area around some position, the counter for that position is incremented. The position with the maximum number of ships that can be positioned over it becomes the next target. As there can be multiple positions with counters that have the same maximum value, the position that will be the next target is randomly selected between these positions. If there is in fact a ship positioned on the choosen target (we have a so-called HIT), the algorithm for finding and identifying ships in this area is initiated. This algorithm is repeated until a HIT is found.

###Eliminating located ships

In order to find all of the points occupied by a ship (or a list of ships) around a found HIT, we iterate through each not-yet-sunk ship and try to place it over a HIT. If a ship can be placed over a HIT, counters for all unidentified positions covered with this ship are incremented. As in the algorithm for locating the ship on the map, the position with the highest probability is choosen for the next target. In this manner, more neareby HITs are found.  
Here is an example of trying to place X Wing over a HIT (where a HIT is shown in uppercase):
>````
   *     x      x     *      x     x      x     x      x     x
      x      ,     x      ,     *      ,     x      ,     x	
   x     x      x     x      x     x      *     x      x     *
   ````

This procedure is repeated until all not-yet-sunk ships can be placed over positions with HITs and unidentified positions.

###Identifying the eliminated ships

As it is assumed that ships can be placed side by side in order to confuse the opponent, it is necessary to identify what ships are located on the hit area. First, we determine the size of the area, and what ships can fit into this area. As we have only four ships, number of possible combinations of ships is 15. In order to find all of the combinations of ships that have the combined size equal to the size of the found hit area, a recursive algorithm is used that finds these combinations using two disjoint lists of ships. Each ship covers some area on the map, for example, Y Wing covers an area of size five (it covers five points). By adding ships from one list to another and examining if the size of some combination of ships is the same as the size of the hit area, all relevant combinations can be found. If the size of all the ships in the list into which the ships are added is greater than the size of the hit area, the search in will not continue in depth for these ships. For example, if the size of the hit area is seven, then the combinations which contain ships Petar Kresimir and X Wing are discarded because the size of these ships is nine (4 + 5): 
>[__PK__]   [__XW__, YW, TF]  
__[PK, XW]__ [YW, TF]       [PK, YW] [TF] . . .  

The search won't explore combinations from lists [PK, XW] and [YW, TF] on left branch in level two, instead, it will continue by exploring the right branch on the same level - [PK, YW] [TF].  

After all of the combinations of ships that have the combined size equal to the size of the hit area are found, the program has to find the combination that fits the shape of the hit area. Each combination of ships is then examined in a way that all of the ships from this combination are placed within the hit area. If the ships from some combination are placed within the hit area without overlapping, we've found the right combination of ships and the search is finished. For example, the hit area can be of size 21 and it can contain all four ships:

>````    
    X   X
	X   X
  * + X +
  * + + +	
  * + O + O  
  *     O 
      O   O
````

First, the algorithm tries to position TIE fighter in the upper left point of the hit area (because it is the largest). If TIE fighter can be placed in this location, the algorithm tries to position X Wing is next to it, etc. This procedure is repeated for all positions of the hit area and all ships until the correct position for every ship (without overlapping between ships) within the hit area is found.


##Installation


##Usage  


The program can be run with or without anargument. If it is run with an argument - the argument is previously fetched token. If no token is provided, the program fetches a new token.
For example, program can be started from the command line with the token 867711 as an argument:  

>```` java –jar Battleships.jar 867711 ````  

The output contains the used token and the name and position of each ship when the position of one of the ships is discovered. When all ships are found, the entire two-dimensional map is printed, where zeros represent postions that were not hit, and ones the positions that were hit (misses and ships):
````
Token: 867711  
Type = PETAR_KRESIMIR, position = unknown
Type = X_WING, position = unknown
Type = Y_WING, position = unknown
Type = TIE_FIGHTER, position = [x = 11, y = 39]

Type = PETAR_KRESIMIR, position = unknown
Type = X_WING, position = [x = 18, y = 15]
Type = Y_WING, position = unknown
Type = TIE_FIGHTER, position = [x = 11, y = 39]

Type = PETAR_KRESIMIR, position = unknown
Type = X_WING, position = [x = 18, y = 15]
Type = Y_WING, position = [x = 11, y = 7]
Type = TIE_FIGHTER, position = [x = 11, y = 39]

Type = PETAR_KRESIMIR, position = [x = 16, y = 33]
Type = X_WING, position = [x = 18, y = 15]
Type = Y_WING, position = [x = 11, y = 7]
Type = TIE_FIGHTER, position = [x = 11, y = 39]

0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 1 1 0 0 0 0 1 1 0 0 1 0 0 0 1 1 0 0 0 
0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 1 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 0 0 0 0 0 
1 0 0 0 0 0 0 0 0 0 0 1 0 1 0 1 0 0 0 0 0 0 1 
0 0 0 0 0 0 1 0 0 0 0 1 1 1 0 0 0 0 0 1 0 1 0 
0 0 0 0 1 0 0 0 0 0 0 1 0 1 0 0 0 0 0 0 1 0 0 
0 0 0 0 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 1 1 0 0 1 0 0 0 0 0 0 1 0 0 0 1 0 0 0 0 0 1 
0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 1 1 0 0 0 0 
1 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 1 1 1 0 
0 1 0 0 1 0 0 1 0 0 0 0 0 1 1 1 0 0 1 0 1 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 1 0 
0 0 1 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 1 1 0 0 
0 0 0 1 0 0 0 0 0 0 1 0 0 0 0 0 1 0 0 0 1 0 0 
1 0 0 0 0 1 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 0 1 
0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 1 0 
0 0 1 0 1 0 0 0 0 0 0 1 0 0 0 0 0 1 0 1 1 0 0 
0 0 0 0 0 0 1 1 0 0 0 0 1 1 0 0 1 0 0 0 0 0 0 
0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 1 0 0 1 0 0 
0 1 0 0 0 0 0 1 0 0 0 0 0 1 1 0 0 0 0 0 0 1 0 
0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 1 
0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 1 0 0 0 0 0 0 1 0 0 0 0 1 0 0 0 0 
1 1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 1 0 0 1 0 0 
0 0 1 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0 0 1 0 0 0 
0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 
0 0 0 0 0 1 0 0 0 0 0 1 0 0 1 0 1 0 0 0 0 0 0 
0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 0 0 1 0 
0 0 1 0 0 0 0 1 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 1 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 1 1 1 1 0 0 0 0 0 0 0 0 
0 0 0 0 1 1 0 0 0 1 0 1 1 1 0 1 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 1 0 1 0 1 1 1 0 0 0 0 1 0 0 0 0 
0 0 1 0 0 0 1 0 0 0 0 1 1 1 0 1 0 0 0 0 0 0 0 
0 1 0 0 0 0 0 0 0 0 0 1 1 1 0 0 0 0 0 0 1 0 0 
0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 0 0 0 0 0 0 
1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 
0 0 1 1 1 0 0 0 0 0 1 0 1 1 0 0 0 0 0 1 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 
0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 1 0 0 0 1 0 0 
0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 0 
0 0 0 1 1 0 0 0 0 0 0 0 1 1 0 0 0 0 0 1 0 1 0 
1 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 1 0 0 0 0 
0 0 0 0 0 0 0 0 0 1 0 1 0 0 1 1 1 0 0 0 0 0 0 
0 0 1 0 0 1 0 0 0 0 0 0 0 0 0 0 0 1 0 0 1 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
````


[1]: http://en.wikipedia.org/wiki/Battleship_(game)        "Battleship wiki"
[2]: https://script.google.com/macros/s/AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?method=GetToken        "Token"
