package game.entities;

import game.InputHandler;
import game.level.Level;

import java.net.InetAddress;

/**
 * A PlayerMP is a Player that has multiplayer properties.
 *
 * @author AJ
 *
 */
public class PlayerMP extends Player {

	/**
	 * The Internet address of the remote player.
	 */
	public InetAddress ipAddress;

	/**
	 * The port of the remote player.
	 */
	public int port;

	/**
	 * Creates a new multiplayer MP.
	 *
	 * @param level
	 *            Level to make a MP player
	 * @param id
	 *            Id of player
	 * @param x
	 *            X of player
	 * @param y
	 *            Y of player
	 * @param handler
	 *            Handler for the player (Only used in local)
	 * @param username
	 *            Username of player
	 * @param ipAddress
	 *            IP address of player
	 * @param port
	 *            Port the player is on
	 * @param team
	 *            Team the player is on
	 */
	public PlayerMP(Level level, int id, int x, int y, InputHandler handler,
			String username, InetAddress ipAddress, int port, String team) {
		super(level, id, x, y, handler, username, team);
		this.ipAddress = ipAddress;
		this.port = port;
		isLocal = true;
	}

	/**
	 * Simple multiplayer create of a MP
	 *
	 * @param level
	 *            Level to create it in
	 * @param id
	 *            ID of player
	 * @param x
	 *            X of player
	 * @param y
	 *            Y of player
	 * @param username
	 *            Username of player
	 * @param ipAddress
	 *            IP address of player
	 * @param port
	 *            Port of player
	 * @param team
	 *            Team the player is on
	 */
	public PlayerMP(Level level, int id, int x, int y, String username,
			InetAddress ipAddress, int port, String team) {
		super(level, id, x, y, null, username, team);
		this.ipAddress = ipAddress;
		this.port = port;
	}

	@Override
	public void tick() {
		super.tick();
	}
}
