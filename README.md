# Project-Tanks
A game I made with Java using UDP as a networking tool. Turned in as a final project for school. Creating this game has shown me a lot about Java and game development. I hope that this game is usefull for others to learn about programming and its concepts.

## Features:
* Functioning multiplayer concepts with maps and game modes.
* Teams.
* Supports many people in one lobby.
* Music that plays in the background.
* Fun to play with frends.
* Ability to host a server and join a server.
* Quick graphics engine.
* Written in (almost) pure java.


## Changelog: 
#### v1.1.0Git: NOW ON GIT
- +Pushed game to GitHub!

#### v0.0.17: NET UPDATE
- +Client gets world from server
- +Finished javadoc.
- +Now with 10% more cool.
- +Sometimes scores work.

#### v0.0.16: CODE FIX
- +Made the code so much easier to understand.
- +Added a javadoc to all of the elements in the code.

#### v0.0.15: NET FIX
- +Server/client now performs better!
- +Tweening allows the client to send less packets!
- +Controls have been optomized.

###### Bugs:
- +Fixed a bug that would drop packets.
- +Fixed a bug where landmines were left on disconnect.
- +Fixed a bug that would cause the flag to appear at spawn

#### v0.0.14: THE GAMEMODE UPDATE
- +Added a menu for the server host to start a game.
- +Server host can change levels.
- +Server host can start games.
- +The flag entity was added to the game.
- +You can now capture the flag!
- +The flag now updates on the player!
- +Bullet is slightly smaller to improve difficulty
- +Bullet now does 2 hearts of damage
- +Music now sounds much better, and the file size was cut in half!

###### Bugs:
- -Fixed a bug where bullets would pass through walls
- -Fixed a bug where the game would quit when you tried to chat
- -Fixed a bug that caused you to get stuck in walls

#### v0.0.13: MUSIC UPDATE
- +Added music for intro, credits, and in game
- +Yor health slowly regens
- +You now can only have as much energy as health
- +Added a System Menu where the host can start a game mode while clients can also configure options and leave the game.
- +Added control help.
- +Added the (pending) ability to start a game.

###### Bugs:
- -Fixed a bug where small text renders on the wrong side of the screen
- -Fixed a bug where a player would die, but revive and not respawn
- -Fixed a bug where the game would crash when compiled
- -Fixed a bug where Music would continue after exit of credits.
- -Fixed a bug where player health would become unsyncronyzed.
- -Fixed a bug where music would stop playing
- -Fixed a bug where mines would delete themselves when other players placed mines.

#### v0.0.12: CLEAN CODE
- +Code is MUCH easier to read
- +Comments helps understand magic numbers
- +Added credits to the game
- +You can now only place 3 mines

###### Bugs:
- -Fixed a bug where a certian phrase would cause the game to crash.

#### v0.0.11: ID UPDATE!
- +All entities must have a unique ID set to them
- +Entities now sync removal between clients
- +Code optimization and better documentation
- +Entity numbers sync with clients

#### v0.0.10: LANDMINES
- +Added landmines to the game
- +Changed the speed energy charges
- +Added extra camera shake for comedic effect

###### Bugs:
- -Fixed a bug where red players would spawn in the green base
- -Fixed a bug where landmines would be dropped along with bullets

#### v0.0.9: THE TEAMS UPDATE
- +Changed the GUI and the chatter to handle input better.
- +Main menu GUI code is MUCH easier to follow.
- +Silky smooth menus.
- +You can now choose a team.
- +Teams work!! - You cannot kill your own teammates.
- +Chat now disappears after 300 in game ticks.

###### Bugs:
- +You can join a very unbalanced team, and teams can be very unbalanced.
- -Fixed some bugs with the darned' input.

#### v0.0.8: THE GUI UPDATE
- +Added a GUI to the game (No more pop-ups!). Will add an on screen GUI for skills later.
- +Changed some of the handler code
- +Added the possibility for multiple levels to exist in one multi-player universe.

###### Bugs:
- -Fixed an energy related bug

#### v0.0.7: BULLET FIX
- +Cleaned up a lot of bullet related code.
- +Collaterals are now possible.
- +Balanced energy a little bit. Tweaked variable names.

###### Bugs:
- -Made it so bullets just pass through players.
- -Fixed a bug where players would crash on connect.
- -Fixed a bug where players would crash after shooting a bullet.

#### v0.0.6: THE BULLET UPDATE
- +Added a new entity to the game: Bullets!
- +The period button now fires bullets for 2 energy.
- +Bullets communicate with the server!
- +You can take damage from bullets.
- +Some balance changes.

###### Bugs:
- +Bullets do not delete themselves when they are hit by the players.
- +Bullets may randomly cause a co-modification error over networks.

#### v0.0.5: THE HP UPDATE
- +Added HP to the players.
- +Made /kill actually kill you. (deal 9001 damage!)
- +Made a kill screen
- +You now re-spawn on death!
- +Created server side packet for updating health.
- +Cleaned up some chat stuff
- +Added ENERGY to the game.
- +Added death messages to the game. They are chosen at random

###### Bugs:
- -Fixed a bug that would cause the server to send a packet without a user name

#### v0.0.4: THE COLOR UPDATE
- +The 10% more color update!
- +Made the game (there is no version data before v0.0.4)
- +Added versions.

###### Bugs:
- +All the bugs
