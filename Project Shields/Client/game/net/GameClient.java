package game.net;

import game.Game;
import game.entities.Bullet;
import game.entities.Entity;
import game.entities.FlagBase;
import game.entities.Landmine;
import game.entities.Player;
import game.entities.PlayerMP;
import game.net.packets.Packet;
import game.net.packets.Packet.PacketTypes;
import game.net.packets.Packet00Login;
import game.net.packets.Packet01Disconnect;
import game.net.packets.Packet02Move;
import game.net.packets.Packet03TestLogin;
import game.net.packets.Packet04Chat;
import game.net.packets.Packet05Damage;
import game.net.packets.Packet06Bullet;
import game.net.packets.Packet07Mine;
import game.net.packets.Packet10Destroy;
import game.net.packets.Packet11StartGame;
import game.net.packets.Packet13Score;
import game.net.packets.Packet77Level;
import game.utils.Debug;
import game.utils.Type;

import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * A GameClient is a background thread that handles all of the packets that come
 * in-bound to the client and the packets that go out-bound from the client. It
 * modifies game data as it is received, requiring methods to be synchronized.
 *
 * @author AJ Walter
 */
public class GameClient extends Thread {

	/**
	 * The IP of the server we are connecting to.
	 */
	private InetAddress ipAddress;

	/**
	 * The socket to send and receive packets over the Internet.
	 */
	private DatagramSocket socket;
	
	/**
	 * The socket to send and receive levels over the Internet.
	 */
	private Socket imageSocket;
	
	/**
	 * The level downloaded from the image socket.
	 */
	private BufferedImage downloadedLevel = null;

	/**
	 * This game.
	 */
	private final Game game; // The game in question

	/**
	 * Name of the class.
	 */
	public static final String CLASS = "Client";

	/**
	 * ID of the player.
	 */
	private int id = 0;

	/**
	 * <code>True</code> if the player can log in to the server, <code>False
	 * </code> otherwise.
	 */
	private int loginable = 0;

	/**
	 * Amount of players on the GREEN team.
	 */
	private int green = -1;

	/**
	 * Amount of players on the RED team.
	 */
	private int red = -1;

	/**
	 * Version received from the server.
	 */
	private final String serverVersion = "";

	/**
	 * Array list of placed mines. Used for deleting mines when the player has
	 * placed more than the maximum amount of mines.
	 */
	private final ArrayList<Integer> mineIDs = new ArrayList<Integer>();

	/**
	 * Creates the server for the first time.
	 * 
	 * @param game
	 *            The current game this client is attached to.
	 * @param ipAddress
	 *            IP address of the remote server.
	 */
	public GameClient(Game game, String ipAddress) {
		Debug.out(Type.INFO, CLASS, "Thread started. Client running.");
		this.game = game;
		try {
			socket = new DatagramSocket();
			Debug.out(Type.DEBUG, CLASS, "Starting socket");
			this.ipAddress = InetAddress.getByName(ipAddress);
			Debug.out(Type.DEBUG, CLASS, "Listening on " + this.ipAddress + ":"
					+ socket.getPort());
		} catch (final Exception e) {
			Debug.out(
					Type.SEVERE,
					CLASS,
					"Something went wrong when trying to start the client! Perhaps you typed in a bad IP?");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			final byte[] data = new byte[1024];
			final DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet); // We got something!
			} catch (final Exception e) {
				Debug.out(Type.SEVERE, CLASS, "Getting packet failed!");
				e.printStackTrace(); // Oh nooo!
			}

			final String message = new String(packet.getData()).trim();
			if (message.substring(0, 2).equals("02")
					|| message.substring(0, 2).equals("11")) {
				Debug.out(Type.TRACE, CLASS, "RECEIVED [" + packet.getAddress()
						+ ":" + packet.getPort() + "] " + message);
			} else {
				Debug.out(Type.DEBUG, CLASS, "RECEIVED [" + packet.getAddress()
						+ ":" + packet.getPort() + "] " + message);
			}
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	/**
	 * Parse packet takes a packet received from the SocketClient and changes it
	 * to game data. All packets received go through this method.
	 * 
	 * @param data
	 *            The raw data received from the server.
	 * @param address
	 *            Internet Address the packet was received from.
	 * @param port
	 *            Numerical port the packet was received from.
	 */
	private void parsePacket(byte[] data, InetAddress address, int port) {
		final String message = new String(data).trim();
		final PacketTypes type = Packet.lookupPacket(message.substring(0, 2)); // lookup
		Packet packet = null;
		switch (type) { // switch to what we looked up
		default:
		case INVALID: // Do nothing
			break;
		case LOGIN: // Run login
			packet = new Packet00Login(data);
			Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress() + ":"
					+ port + "] " + ((Packet00Login) packet).getUsername()
					+ " has joined the game!");
			handleLogin((Packet00Login) packet, address, port);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress() + ":"
					+ port + "] " + ((Packet01Disconnect) packet).getUsername()
					+ " has left the game!");
			game.level.removePlayerMP(((Packet01Disconnect) packet)
					.getUsername());
			for (final Entity e : game.level.getEntities()) {
				if (e instanceof Landmine) {
					if (((Landmine) e).getUsername().equals(
							((Packet01Disconnect) packet).getUsername())) {
						e.markForDelete();
					}
				}
			}
			break;
		case MOVE:
			packet = new Packet02Move(data);
			handleMove((Packet02Move) packet);
			break;
		case TESTLOGIN:
			packet = new Packet03TestLogin(data);
			handleTest((Packet03TestLogin) packet);
			break;
		case CHAT:
			packet = new Packet04Chat(data);
			game.addMessageToHistory(((Packet04Chat) packet).getUsername(),
					((Packet04Chat) packet).getMessage(),
					((Packet04Chat) packet).getColor());
			break;
		case DAMAGE:
			packet = new Packet05Damage(data);
			handleDamage((Packet05Damage) packet);
			break;
		case BULLET:
			packet = new Packet06Bullet(data);
			handleBullet((Packet06Bullet) packet);
			break;
		case MINE:
			packet = new Packet07Mine(data);
			handleMine((Packet07Mine) packet);
			break;
		case DESTROY:
			packet = new Packet10Destroy(data);
			destroyObject((Packet10Destroy) packet);
			break;
		case STARTGAME:
			packet = new Packet11StartGame(data);
			startGame((Packet11StartGame) packet);
			break;
		case SCORE:
			packet = new Packet13Score(data);
			handleScore((Packet13Score) packet);
			break;
		case LEVEL:
			packet = new Packet77Level(data);
			downloadLevel((Packet77Level) packet, address, port);
			break;
		}
	}

	/**
	 * Takes a {@link Packet00Longin} and connects a player to the client.
	 *
	 * @param packet
	 *            {@link Packet00Longin} packet sent from the server
	 * @param address
	 *            Internet Address of the remote packet.
	 * @param port
	 *            Port of the remote packet.
	 */
	private void handleLogin(Packet00Login packet, InetAddress address, int port) {
		final PlayerMP player = new PlayerMP(game.level, packet.getID(),
				packet.getX(), packet.getY(), packet.getUsername(), address,
				port, (packet.getTeam().equals("GREEN") ? "GREEN" : "RED"));
		Debug.out(Type.INFO, CLASS, "Added a " + packet.getTeam());
		game.level.addEntity(player);
	}

	/**
	 * Takes a {@link Packet02Move} and moves the player from the packet to a
	 * location.
	 *
	 * @param packet
	 *            {@link Packet02Move} packet sent from the server
	 */
	private void handleMove(Packet02Move packet) {
		game.level.movePlayer(packet.getUsername(), packet.getX(),
				packet.getY(), packet.isMoving(), packet.getMovingDir(),
				packet.getFlag());
	}

	/**
	 * Takes a {@link Packet03TestLogin} and checks to see if the client can
	 * connect to the server.
	 *
	 * @param packet
	 *            {@link Packet03TestLogin} packet sent from the server
	 */
	private void handleTest(Packet03TestLogin packet) {
		if (packet.getReply().equalsIgnoreCase("!Server OK!")) {
			id = packet.getID();
			loginable = 1;
			Debug.out(Type.INFO, CLASS, "Logging in...");
			return;
		} else if (packet.getReply()
				.contains("!The server is running version ")) {
			loginable = 2;
			Debug.out(Type.SEVERE, CLASS, "The server said: "
					+ packet.getReply().substring(1));
		} else if (packet.getReply().contains(
				"!The server got an invalid username ")) {
			loginable = 3;
			Debug.out(Type.SEVERE, CLASS, "The server said: "
					+ packet.getReply().substring(1));
		} else if (packet.getReply().contains(
				"!This username is already taken!")) {
			loginable = 4;
			Debug.out(Type.SEVERE, CLASS, "The server said: "
					+ packet.getReply().substring(1));
		} else if (packet.getReply().contains("!Players!")) {
			green = packet.getGreen();
			red = packet.getRed();
			Debug.out(Type.INFO, CLASS, "The server said: "
					+ packet.getReply().substring(1));
		} else if (packet.getReply().contains("!You are not whitelisted!")) {
			Debug.out(Type.SEVERE, CLASS, "The server said: "
					+ packet.getReply().substring(1));
		}
	}

	/**
	 * Takes a {@link Packet05Damage} and damages the player from the packet
	 * some amount of health..
	 *
	 * @param packet
	 *            {@link Packet05Damage} packet sent from the server
	 */
	private void handleDamage(Packet05Damage packet) {
		game.level.damagePlayer(packet.getUsername(), packet.getDamage());
	}

	/**
	 * Takes a {@link Packet06Bullet} and creates a {@link Bullet} at some
	 * location.
	 * 
	 * @param packet
	 *            {@link Packet06Bullet} packet sent from the server
	 */
	private void handleBullet(Packet06Bullet packet) {
		boolean immune = false;
		if (packet.getUsername().equals(game.getUsername())
				|| packet.getTeam().equals(game.getTeam())) {
			immune = true;
		}
		final Entity bullet = new Bullet(game.level, packet.getID(),
				packet.getX(), packet.getY(), packet.getDirection(), immune);
		game.level.addLowestEntity(bullet);
	}

	/**
	 * Takes a {@link Packet07Mine} and creates a {@link Landmine} at some
	 * location.
	 * 
	 * @param packet
	 *            {@link Packet07Mine} packet sent from the server
	 */
	private void handleMine(Packet07Mine packet) {
		boolean immune = false;
		if (packet.getUsername().equals(game.getUsername())
				|| packet.getTeam().equals(game.getTeam())) {
			immune = true;
		}
		final Entity mine = new Landmine(game.level, packet.getID(),
				packet.getUsername(), packet.getX(), packet.getY(), immune,
				packet.getTeam());
		if (packet.getUsername().equals(game.getUsername())) {
			mineIDs.add(mine.getID());
		}
		if (mineIDs.size() > 3) {
			final Packet10Destroy destroy = new Packet10Destroy(
					game.getUsername(), (int) (mineIDs.remove(0)));
			destroy.writeData(this);
		}
		game.level.addLowestEntity(mine);
	}

	/**
	 * Takes a {@link Packet10Destroy} and makes {@link Entity#markForDelete} of
	 * some entity <code>True</code>.
	 * 
	 * @param packet
	 *            {@link Packet10Destroy} packet sent from the server
	 */
	private void destroyObject(Packet10Destroy packet) {
		boolean found = false;
		for (final Entity e : game.level.getEntities()) {
			if (e.getID() == packet.getID()) {
				e.markForDelete();
				found = true;
			}
		}
		if (!found) {
			Debug.out(Type.WARNING, CLASS, "The ID " + packet.getID()
					+ " was not found!");
		}
	}

	/**
	 * Takes a {@link Packet11StartGame} and starts a{@link Game#GAME_MODES}
	 * Teleports a player back to the spawn.
	 *
	 * @param packet
	 *            {@link Packet11StartGame} packet sent from the server
	 */
	private void startGame(Packet11StartGame packet) {		
		final Packet02Move move = new Packet02Move(game.getUsername(), (game
				.getTeam().equals("GREEN") ? 100
				: (game.level.width * 32) - 100), (game.getTeam().equals(
				"GREEN") ? 100 : (game.level.height * 32) - 100), false, 0,
				false);
		sendData(move.getData());
		game.player.x = move.getX();
		game.player.y = move.getY();
		game.player.setMoving(move.isMoving());
		game.player.setMovingDir(move.getMovingDir());
		if (packet.getGameMode().equals("CTF")) {
			final FlagBase green = new FlagBase(game.level, -2, 200, 200,
					"GREEN");
			final FlagBase red = new FlagBase(game.level, -2,
					(game.level.width * 32) - 200,
					(game.level.height * 32) - 200, "RED");
			game.level.addLowestEntity(green);
			game.level.addLowestEntity(red);
			game.setGameRunning(true);
			game.player.setFlag(false);
			game.currentRunningGamemode = packet.getGameMode();
		} else { //Whatever
			game.player.setFlag(false);
			game.currentRunningGamemode = packet.getGameMode();
		}
	}

	/**
	 * Takes a score and sets it to the players.
	 * @param packet
	 */
	private void handleScore(Packet13Score packet) {
		if(game.currentRunningGamemode.equals("CTF")) {
			for(Entity e : game.level.getEntities()) {
				if(e instanceof Player) {
					Player p = (Player)e;
					if(p.getUsername().equals(packet.getUsername())) {
						p.setScore(packet.getScore());
					}
				}
			}
		} else {
			
		}
	}

	private void downloadLevel(Packet77Level packet, InetAddress address, int port) {
		if(game.socketServerIsNull() == true) { //Never attempt to download if client is host.
			try {
				System.out.println(packet.getWidth()+","+ packet.getHeight());
				Thread.sleep(100);
				 imageSocket = new Socket(address, port);
				 downloadedLevel = ImageIO.read(imageSocket.getInputStream());
				 imageSocket.close();
				 game.level.regenLevel(packet.getPath(),
						 downloadedLevel.getSubimage(0, 0, packet.getWidth(), packet.getHeight()));
					
				 final Packet02Move move = new Packet02Move(game.getUsername(), (game
							.getTeam().equals("GREEN") ? 100
							: (game.level.width * 32) - 100), (game.getTeam().equals(
							"GREEN") ? 100 : (game.level.height * 32) - 100), false, 0,
							false);
				sendData(move.getData());
				game.player.x = move.getX();
				game.player.y = move.getY();
				game.player.setMoving(move.isMoving());
				game.player.setMovingDir(move.getMovingDir());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sends an byte array of data over the Internet.
	 * @param data
	 * 					Array of data to send to a remote address.
	 */
	public void sendData(byte[] data) { // data to send.
		final DatagramPacket packet = new DatagramPacket(data, data.length,
				ipAddress, 9714); // construct a packet.
		try {
			socket.send(packet);
			Thread.sleep(5);
		} catch (final Exception e) {
			Debug.out(Type.SEVERE, CLASS, "Sending packet failed!");
			e.printStackTrace();
		}

		final String message = new String(packet.getData());
		if (message.substring(0, 2).equals("02")
				|| message.substring(0, 2).equals("11")) {
			Debug.out(Type.TRACE, CLASS, "SENT [" + packet.getAddress() + ":"
					+ packet.getPort() + "] " + message);
		} else {
			Debug.out(Type.DEBUG, CLASS, "SENT [" + packet.getAddress() + ":"
					+ packet.getPort() + "] " + message);
		}
	}

	/**
	 * Check to see if the connecting client can log into a server.
	 * 
	 * @return <code>True</code> when the GameClient can log into the server.
	 *         <code>False</code> at any other time.
	 */
	public synchronized int getLoginable() {
		return loginable;
	}

	/**
	 * Gets the amount of players logged into the server.
	 * 
	 * @return amount of green players.
	 */
	public synchronized int getGreen() {
		return green;
	}

	/**
	 * Gets the amount of players logged into the server.
	 * 
	 * @return amount of red players.
	 */
	public synchronized int getRed() {
		return red;
	}

	/**
	 * Gets the version of the server.
	 * 
	 * @return {@link String} representation of the server's version.
	 */
	public synchronized String getVersion() {
		return serverVersion;
	}

	/**
	 * Gets the ID of the player from the server.
	 * 
	 * @return ID that the server issued the player.
	 */
	public synchronized int getID() {
		return id;
	}
}
