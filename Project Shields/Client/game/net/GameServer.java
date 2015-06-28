package game.net;

import game.Game;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * GameServer is a class that is run in the background that handles
 * all of the data going into and leaving the server. Because this game
 * is relatively undeveloped (compared to large triple A titles), most of the
 * actual computation for clients is handled client side. The main goal for
 * GameServer is to bounce packets back to all of the clients.
 * @author AJ Walter
 *
 */
public class GameServer extends Thread {

	/**
	 * Name of this class.
	 */
	public static final String CLASS = "Server";

	/**
	 * ID of the entity last used. Is incremented automatically.
	 */
	private int id = 1;

	/**
	 * The socket used for UDP, or the packets that can be dropped
	 * if not handled carefully.
	 */
	private DatagramSocket socket;
	
	/**
	 * The socket server to send and receive levels over the Internet.
	 */
	private ServerSocket imageSocketServer;
	
	/**
	 * The socket to send and receive levels over the Internet.
	 */
	private Socket imageSocket;
	
	/**
	 * The running game.
	 */
	private final Game game;
	
	/**
	 * This is a list of all the Players as PlayerMPs connected to the server.
	 */
	private final List<PlayerMP> connectedPlayers = new ArrayList<>();
	
	/**
	 * This is a list of usernames currently connected to the server.
	 */
	private final List<String> acceptablePackets = new ArrayList<>();
	
	/**
	 * If we are running a game, this will contain the packet to send of that
	 * game mode.
	 */
	private Packet11StartGame runningGame = null;
	
	/**
	 * Help file used when the player types /help.
	 */
	private final String[] helpDocument = { "HELP DOCUMENT:",
			"/help    - Shows the help", "/kill    - Kills the player",
			"/list    - Lists connected players",
			"/version - Displays the server version" };

	/**
	 * Creates the GameServer. This thread is run in the background (Like the game client)
	 * and updates as packets are received.
	 * @param game Game to control.
	 */
	public GameServer(Game game) {
		Debug.out(Type.INFO, CLASS, "Thread started. Server running.");
		this.game = game;
		try {
			socket = new DatagramSocket(9714); // Listen on this port, m8
			Debug.out(Type.DEBUG, CLASS, "Starting server.");
		} catch (final Exception e) {
			Debug.out(
					Type.SEVERE,
					CLASS,
					"Something went wrong when trying to start the server! "
							+ "Perhaps there is a server already running on the port 9714?");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			final byte[] data = new byte[1024]; // Thing to be gotten
			final DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet); // Receive a packet
			} catch (final Exception e) {
				Debug.out(Type.SEVERE, CLASS, "Getting packet failed!");
				e.printStackTrace();
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
			if ((id > -10) && (id < 0)) { // If (for some reason) the data wraps
				// around all the way, skip these
				// negitive numbers.
				id = 1;
			}
		}
	}

	/**
	 * Accepts a packet from the run() method and handles it.
	 * @param data Array of data to handle.
	 * @param address Address from where the data was received.
	 * @param port Port of which the data was received.
	 */
	private void parsePacket(byte[] data, InetAddress address, int port) {
		final String message = new String(data).trim();
		// look up the packet
		final PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) { // switch to what we looked up
		default:
		case INVALID: // Do nothing
			break;
		case LOGIN: // Run login
			packet = new Packet00Login(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			Debug.out(Type.INFO, CLASS, "Obtained player ID: "
					+ ((Packet00Login) packet).getID());
			final PlayerMP player = new PlayerMP(game.level, 100, 100,
					((Packet00Login) packet).getID(), packet.getUsername(),
					address, port, ((Packet00Login) packet).getTeam());
			//Adds a new connection to the game
			addConnection(player, (Packet00Login) packet);
			sendNoticeMessage(packet.getUsername(), 00, address, port);
			if(!((Packet00Login) packet).getUsername().equals(game.getUsername())) { 
				//You can't download a level from yourself!
				Packet77Level level = 
						new Packet77Level(((Packet00Login) packet).getUsername(), 
								game.level.getPath(), 
								game.level.width, 
								game.level.height);
				level.writeData(this, address, port);
				sendImageToClient(game.level.getImage());
			}
			if (runningGame != null) {
				sendData(runningGame.getData(), address, port);
				//If there is a player on your currenttly connecting team that isn't you, set your score to that players score.
				for(PlayerMP players : connectedPlayers) {
					if(players.getTeam().equals(((Packet00Login)packet).getTeam())) {
						if(!players.getUsername().equals(((Packet00Login)packet).getUsername())) {
							for(PlayerMP searchForPlayer : connectedPlayers) {
								if(searchForPlayer.getUsername().equals(((Packet00Login)packet).getUsername())) {
									searchForPlayer.setScore(players.getScore());
								}					
							}
						}
					}
				}
			}
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			removeConnection((Packet01Disconnect) packet);
			sendNoticeMessage(packet.getUsername(), 01, address, port);
			break;
		case MOVE:
			packet = new Packet02Move(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			handleMove((Packet02Move) packet);
			break;
		case TESTLOGIN:
			packet = new Packet03TestLogin(data);
			sendTestLogin((Packet03TestLogin) packet, address, port);
			break;
		case CHAT:
			packet = new Packet04Chat(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress() + ":"
					+ port + "] Message: " + packet.getUsername()
					+ ((Packet04Chat) packet).getMessage());
			checkMessage((Packet04Chat) packet, address, port);
			break;
		case DAMAGE:
			packet = new Packet05Damage(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			damagePlayer((Packet05Damage) packet);
			break;
		case BULLET:
			packet = new Packet06Bullet(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			createBullet((Packet06Bullet) packet);
			break;
		case MINE:
			packet = new Packet07Mine(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			createMine((Packet07Mine) packet);
			break;
		case DESTROY:
			packet = new Packet10Destroy(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			destroyObject((Packet10Destroy) packet);
			break;
		case SCORE:
			packet = new Packet13Score(data);
			if (!packet.isValid() || !acceptablePacket(packet)) {
				break;
			}
			handleScore((Packet13Score) packet);
			break;
		}
	}

	/**
	 * Connects a player to the game.
	 * @param player Physical PlayerMP to connect to the game
	 * @param packet Packet of the connecting player Player00Login player.
	 */
	public void addConnection(PlayerMP player, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (final PlayerMP p : connectedPlayers) {
			if (player.getUsername().equalsIgnoreCase(p.getUsername())) {
				if (p.ipAddress == null) {
					p.ipAddress = player.ipAddress;
				}
	
				if (p.port == -1) {
					p.port = player.port;
				}
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), p.ipAddress, p.port);
				final Packet00Login spacket = new Packet00Login(
						p.getUsername(), p.getID(), p.x, p.y, p.getTeam());
				sendData(spacket.getData(), player.ipAddress, player.port);
			}
		}
		if (!alreadyConnected) {
			connectedPlayers.add(player);
		}
	}

	/**
	 * Drops a connection from the game.
	 * @param packet Packet01Disconnect packet)
	 */
	private void removeConnection(Packet01Disconnect packet) {
		int index = getPlayerMPIndex(packet.getUsername());
		connectedPlayers.remove(index);
		index = getAcceptedIndex(packet.getUsername());
		acceptablePackets.remove(index);
		packet.writeData(this);
	}

	/**
	 * Handles the movement of the players.
	 * @param packet Packet02Move packet.
	 */
	private void handleMove(Packet02Move packet) {
		if (getPlayerMP(packet.getUsername()) != null) {
			final int index = getPlayerMPIndex(packet.getUsername());
			final PlayerMP player = connectedPlayers.get(index);
			player.x = packet.getX();
			player.y = packet.getY();
			player.setMoving(packet.isMoving());
			player.setMovingDir(packet.getMovingDir());
			packet.writeData(this);
		}
	}

	/**
	 * Checks an incoming pre-login packet Packet03TestLogin and
	 * handles it.
	 * @param packet Packet03TestLogin packet.
	 * @param address Address to connect to.
	 * @param port Port to connect to.
	 */
	private void sendTestLogin(Packet03TestLogin packet, InetAddress address,
			int port) {
		boolean loggingIn = true;
		if (packet.getRed() == -2) {
			loggingIn = false;
			final Packet03TestLogin reply = new Packet03TestLogin("!Players!",
					-1, Game.version, getGreen(), getRed());
			reply.writeData(this, address, port);
		}
		if (loggingIn && (!packet.getVersion().equals(Game.version))) {
			loggingIn = false;
			Debug.out(Type.WARNING, CLASS, "[" + address.getHostAddress() + ":"
					+ port
					+ "] A client tried to connect with a different version "
					+ packet.getVersion() + "!");
			final Packet03TestLogin reply = new Packet03TestLogin(
					"!The server is running version " + Game.version + "!", -1,
					Game.version);
			reply.writeData(this, address, port);
		}
		if (loggingIn
				&& ((packet.getUsername() == null)
						|| packet.getUsername().equals("") || packet
						.getUsername().substring(0, 1).equals("!"))) {
			loggingIn = false;
			Debug.out(Type.WARNING, CLASS, "[" + address.getHostAddress() + ":"
					+ port
					+ "] A client tried to connect with a different version "
					+ packet.getVersion() + "!");
			final Packet03TestLogin reply = new Packet03TestLogin(
					"!The server got an invalid username "
							+ packet.getUsername() + "!", -1, Game.version);
			reply.writeData(this, address, port);
		}
		if (loggingIn) {
			for (final PlayerMP players : connectedPlayers) {
				if (players.getUsername()
						.equalsIgnoreCase(packet.getUsername())) {
					final Packet03TestLogin reply = new Packet03TestLogin(
							"!This username is already taken!", -1,
							Game.version);
					reply.writeData(this, address, port);
					loggingIn = false;
					break;
				}
			}
		}
		if (loggingIn) {
			final int tempID = getUniqueID();
			acceptablePackets.add((packet.getGreen() == -1 ? "G: " : "R: ")
					+ packet.getUsername() + "^" + tempID);
			final Packet03TestLogin reply = new Packet03TestLogin(
					"!Server OK!", tempID, Game.version);
			sendData(reply.getData(), address, port);
		}
	}

	/**
	 * This (fun) method checks an incoming message and sees if it is 
	 * a command. If it is, it'll figure out what to do, otherwise, it'll send
	 * data to all of the clients with that message data.
	 * @param packet Packet04Chat packet.
	 * @param address Internet address of the player that sent a message.
	 * @param port Port of the player that sent a message.
	 */
	private void checkMessage(Packet04Chat packet, InetAddress address, int port) {
		if (packet.getMessage().substring(2, 3).equals("/")) {
			if (packet.getMessage().substring(3).equalsIgnoreCase("kill")) {
				final Packet05Damage packetdamage = new Packet05Damage(
						packet.getUsername(), 9001);
				packetdamage.writeData(this);
			} else if (packet.getMessage().substring(3)
					.equalsIgnoreCase("list")) {
				packet = new Packet04Chat("SERVER", 000, ": Connected players:");
				packet.writeData(this, address, port);
				for (int height = 0; height < (((acceptablePackets.size() - 1) / 4) + 1); height++) {
					String send = "";
					for (int width = 0; width < 4; width++) {
						final int on = (height * 4) + width;
						if (on < acceptablePackets.size()) {
							if (width != 0) {
								send += ", ";
							}
							send += acceptablePackets.get(on);
						} else {
							break;
						}
					}
					packet = new Packet04Chat("SERVER", 000, ": " + send);
					packet.writeData(this, address, port);
				}
			} else if (packet.getMessage().substring(3)
					.equalsIgnoreCase("help")) {
				for (int i = 0; i < helpDocument.length; i++) {
					packet = new Packet04Chat("SERVER", 000, ": "
							+ helpDocument[i]);
					packet.writeData(this, address, port);
				}
			} else if (packet.getMessage().substring(3)
					.equalsIgnoreCase("version")) {
				packet = new Packet04Chat("SERVER", 000, ": The version is "
						+ Game.version);
				packet.writeData(this, address, port);
			} else if (packet.getMessage().substring(3)
					.equalsIgnoreCase("easteregg")) {
				packet = new Packet04Chat("SERVER", 000,
						": There are no easter eggs here, go away!");
				packet.writeData(this, address, port);
			} else {
				packet = new Packet04Chat("SERVER", 000,
						": Unknown command, type /help for help");
				packet.writeData(this, address, port);
			}
		} else {
			sendMessage(packet);
		}
	}

	/**
	 * Sends a message to all of the clients.
	 * @param packet Packet04Chat packet.
	 */
	private void sendMessage(Packet04Chat packet) {
		for (final PlayerMP p : connectedPlayers) {
			packet.writeData(this, p.ipAddress, p.port);
		}
	}

	/**
	 * Damages some player for a specific amount of damage.
	 * @param packet Packet05Damage
	 */
	private void damagePlayer(Packet05Damage packet) {
		if (packet.getDamage() > 0) {
			packet.writeData(this);
		} else {
			packet.writeData(this);
		}
	}

	/**
	 * Gets a new ID for a bullet and sends it to all clients.
	 * @param packet Packet06Bullet packet.
	 */
	private void createBullet(Packet06Bullet packet) {
		packet.setID(getUniqueID());
		packet.writeData(this);
	}

	/**
	 * Gets a new ID for a Landmine and sends it to all clients.
	 * @param packet Packet07Mine packet.
	 */
	private void createMine(Packet07Mine packet) {
		packet.setID(getUniqueID());
		packet.writeData(this);
	}

	/**
	 * Reflects a Packet10Destroy packet sent from a client to all of the clients.
	 * @param packet Packet10Destroy packet.
	 */
	private void destroyObject(Packet10Destroy packet) {
		packet.writeData(this);
	}

	/**
	 * Starts a game using some {@link game.Game#GAME_MODES} that was
	 * passed from the host of the game. Also changes the level.
	 * @param mode Mode to change to.
	 * @param map Map name to change to.
	 * @param systemMap Directory of the map to change to.
	 */
	public void startGame(String mode, String map, String systemMap) {
		game.level.regenLevel(systemMap);
		Debug.out(Type.WARNING, CLASS, "SWITCHING TO: " + mode + " " + map
				+ " at " + systemMap);
		Packet77Level level = 
				new Packet77Level("SERVER", 
						game.level.getPath(), 
						game.level.width, 
						game.level.height);
		level.writeData(this);
		sendImageToAllClients(game.level.getImage());
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Packet10Destroy destroy = new Packet10Destroy("SERVER", -2);
		destroy.writeData(this);
		try {
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		runningGame = new Packet11StartGame("SERVER", mode, map, systemMap);
		runningGame.writeData(this);
	}

	/**
	 * Handles a packet when a Score packet is received.
	 * @param packet Packet13Score packet.
	 */
	private void handleScore(Packet13Score packet) {
		if(game.currentRunningGamemode != null) {
			for(PlayerMP player : connectedPlayers) {
				if(game.currentRunningGamemode.equals("CTF")) {
					if(player.getTeam().equals(packet.getTeam())) {
						player.setScore(player.getScore() + packet.getScore());
					}
				}
			}
			for (PlayerMP playerToSend : connectedPlayers) {
				for (PlayerMP players : connectedPlayers) {
					//Send all the players score to all of the players.
					Packet13Score score = new Packet13Score(players.getUsername(), players.getTeam(), players.getScore());
					score.writeData(this, playerToSend.ipAddress, playerToSend.port);
				}
			}
		}
	}

	/**
	 * Sends some form of notice.
	 * @param player Username of notice.
	 * @param type Type of notice.
	 * @param address Address to send to.
	 * @param port Port to send to.
	 */
	private void sendNoticeMessage(String player, int type,
			InetAddress address, int port) {
		final int color = 440;
		Packet04Chat notice = null;
		if (type == 00) {
			if (runningGame != null) {
				Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress()
						+ ":" + port + "] " + player + " joined the game.");
				notice = new Packet04Chat(player, color, " joined the game.");
			} else {
				Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress()
						+ ":" + port + "] " + player + " joined the lobby.");
				notice = new Packet04Chat(player, color, " joined the lobby.");
			}
		}
		if (type == 01) {
			Debug.out(Type.INFO, CLASS, "[" + address.getHostAddress() + ":"
					+ port + "] " + player + " left the game.");
			notice = new Packet04Chat(player, color, " left the game.");
		}
		if (notice != null) {
			sendMessage((Packet04Chat) notice);
		} else {
			Debug.out(Type.INFO, CLASS, "I tried to send a notice, but failed!");
		}
	}

	/**
	 * Checks if a packet can be written to this server or not.
	 * @param packet Packet packet
	 * @return <code>True</code> if it can, false otherwise.
	 */
	private boolean acceptablePacket(Packet packet) {
		for (final String acceptablePlayers : acceptablePackets) {
			if (acceptablePlayers.substring(3,
					acceptablePlayers.lastIndexOf("^")).equals(
					packet.getUsername())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the amount of green players.
	 * @return int amount of green players connected to the server.
	 */
	private int getGreen() {
		int green = 0;
		for (final String str : acceptablePackets) {
			if (str.substring(0, 3).equals("G: ")) {
				green++;
			}
		}
		return green;
	}

	/**
	 * Get the amount of red players.
	 * @return int amount of red players connected to the server.
	 */
	private int getRed() {
		int red = 0;
		for (final String str : acceptablePackets) {
			if (str.substring(0, 3).equals("R: ")) {
				red++;
			}
		}
		return red;
	}

	/**
	 * Gets a unique ID
	 * @return Current ID++;
	 */
	private int getUniqueID() {
		Debug.out(Type.DEBUG, CLASS, "On id: " + id);
		return id++;
	}

	/**
	 * Gets the index of a username in the acceptable packets 
	 * list.
	 * @param username String username
	 * @return The index of the searched player.
	 */
	private int getAcceptedIndex(String username) {
		int index = 0;
		for (final String acceptedPlayer : acceptablePackets) {
			if (acceptedPlayer.substring(3, acceptedPlayer.lastIndexOf("^"))
					.equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	/**
	 * Gets some PlayerMP from the connectedPlayers list.
	 * @param username Username to look up.
	 * @return  PlayerMP of the looked up username.
	 */
	public PlayerMP getPlayerMP(String username) {
		for (final PlayerMP player : connectedPlayers) {
			if (player.getUsername().equals(username)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Gets the index of a PlayerMP by their username.
	 * @param username Username to index.
	 * @return The index of the PlayerMP.
	 */
	public int getPlayerMPIndex(String username) {
		int index = 0;
		for (final PlayerMP player : connectedPlayers) {
			if (player.getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	/**
	 * Writes data to a specific client.
	 * @param data Data array to write
	 * @param ipAddress Address of the remote player
	 * @param port Port of the remote player.
	 */
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		final DatagramPacket packet = new DatagramPacket(data, data.length,
				ipAddress, port);
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
	 * Sends a data array to all of the clients.
	 * @param data Data array to send.
	 */
	public void sendDataToAllClients(byte[] data) {
		for (final PlayerMP p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}

	/**
	 * Sends a data array to all of the clients EXCEPT for the person
	 * who sent it.
	 * @param data Data array to be sent.
	 * @param username Username of the sender that sent the data.
	 */
	public void sendDataToAllClientsExceptSelf(byte[] data, String username) {
		for (final PlayerMP p : connectedPlayers) {
			if (!p.getUsername().equals(username)) {
				sendData(data, p.ipAddress, p.port);
			}
		}
	}
	
	/**
	 * Sends an image to a client.
	 * @param img BufferedImage to be sent.
	 */
	public void sendImageToClient(BufferedImage img) {
		Debug.out(Type.INFO, CLASS, "Someone is trying to download a level!");
        try{
        	if(imageSocketServer == null) {
        		imageSocketServer = new ServerSocket(9714);
        	}
            imageSocket = imageSocketServer.accept();
            ImageIO.write(img, "PNG", imageSocket.getOutputStream());
       }
       catch(Exception ex){
           ex.printStackTrace();
       }
	}
	
	/**
	 * Image sent to all the clients.
	 * @param img BufferedImage image.
	 */
	public void sendImageToAllClients(BufferedImage img) {
		Debug.out(Type.INFO, CLASS, "Everyone is trying to download a level!");
		for(int i = 0; i < connectedPlayers.size() - 1; i++) {
	        try{
	        	if(imageSocketServer == null) {
	        		imageSocketServer = new ServerSocket(9714);
	        	}
	            imageSocket = imageSocketServer.accept();
	            ImageIO.write(img, "PNG", imageSocket.getOutputStream());
	       }
	       catch(Exception ex){
	           ex.printStackTrace();
	       }
		}
	}
}