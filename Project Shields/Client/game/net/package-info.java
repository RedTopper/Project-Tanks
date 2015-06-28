/**
 * This package contains all the classes needed to communicate between
 * other instances of the game over the Internet. The {@link game.net.GameClient}
 * locally accepts and sends values, while the {@link game.net.GameServer} remotely
 * (or locally, if the Game is the server host) sends data to all currently accepted 
 * {@link game.net.GameClient}s.
 * 
 * @author AJ Walter
 */
package game.net;