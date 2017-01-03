This open source project was built by Tyler J. Syme
Contact me at "tylersyme@gmail.com" if you have any questions regarding this project
--------------------------------------------------------------------------------------------

This is a simple Checkers game which pits two real players against each other, where the 
player who captures the other player's pieces wins the game.

The game, while still a work in progress, is currently playable and supports both local
games on the same network and games across two separate networks. A player can choose
to either begin hosting their own server or join another player's server to play a 
game. In order to join a host player's game, the joining player must have the host's
ip address. 
Two players who are playing on the same network must simply enter "localhost" into the 
given ip text box in order to start a game. Two players on separate networks must instead
specify the host's computer's public ip address which can be found here at this website:
"http://www.myipaddress.com/show-my-ip-address/".

The game itself works well and has no major bugs so far found. Both players are given a
chat box so that they may send messages back and forth (does not filter crude language).
Players may also choose to resign games or request a draw.
There is no turn time limit.
There is no singleplayer.

To move a checker piece, click and drag a the piece to the square you wish to move to.
An invalid move will not be accepted and your piece will be returned to its original
location. Pieces may jump other pieces as many times as possible and will be "kinged"
if they reach the opposite end of the board. There is currently a little bit of lag
when the very first piece is kinged. Kings may then move and jump backwards.

Important: If player 1 captures player 2s checker piece, player 1 must click the
           "End Turn" button to confirm the end of his/her turn. This is only 
           needed after having captured a piece in order to allow multiple
           jumps. Otherwise, turns are ended automatically.
           
           Once a checkers game ends, both players must close the program and reopen
           it in order to begin another game. This is an issue that is still being
           worked on.


How to play checkers: "https://www.youtube.com/watch?v=SuQY1_fCVsA&ab_channel=About.com"
