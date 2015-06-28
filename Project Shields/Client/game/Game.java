package game;

import game.entities.Entity;
import game.entities.Player;
import game.entities.PlayerMP;
import game.gfx.Chat;
import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;
import game.gfx.Sprites;
import game.level.Level;
import game.net.GameClient;
import game.net.GameServer;
import game.net.packets.Packet00Login;
import game.net.packets.Packet02Move;
import game.net.packets.Packet03TestLogin;
import game.net.packets.Packet04Chat;
import game.net.packets.Packet05Damage;
import game.net.packets.Packet13Score;
import game.utils.Credits;
import game.utils.Debug;
import game.utils.MP3;
import game.utils.Menu;
import game.utils.MenuAt;
import game.utils.Type;
import game.utils.TypeSystem;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * The {@link Game} class sets up anything that has to do with the game itself.
 * Only one instance of the {@link Game} class should ever be made. Constructing
 * the class creates a {@link JFrame} to render contents on. This class is a
 * {@link Runnable} {@link Thread} that continues to run until the application
 * is closed.
 *
 * @author AJ Walter
 *
 */
public class Game extends Canvas implements Runnable {

	/**
	 * The {@link String} of the game. Starts with a v0. This is used when
	 * logging on to a server, so update it often to prevent out dated clients
	 * from connecting.
	 */
	public static final String version = "v1.1.01";
	public static final long serialVersionUID = 1L;

	/**
	 * The minimum {@link Type} of debug that is output. Should be
	 * {@link Type#INFO} or higher on release. NEVER SET TO {@link Type#TRACE}
	 * ON RELEASE!
	 */
	public static final Type debugLevel = Type.INFO;

	/**
	 * Name of the class.
	 */
	public static final String CLASS = "Game";

	/**
	 * The {@link String} of the class. This is displayed in the title.
	 */
	public static final String NAME = "Project Tanks";

	/**
	 * Integer representing the dimensions of the JFrame.
	 */
	public static final int WIDTH = 620, HEIGHT = (WIDTH / 16) * 9, SCALE = 2;

	/**
	 * An array of randomly selected messages that are said when the player's
	 * health falls to zero.
	 */
	public static final String[] DEATH_MESSAGE = { "fell out of the world.",
			"crashed and burned.", "exploded.", "died in a fireball.",
			"fell off a cliff.", "completed their life.",
			"didn't respond fast enough.", "was slam dunked.", "was rekt.",
			"didn't see that coming.", "needs to get better at this game.",
			"acended to the afterlife." };
	/**
	 * An array of controls that are displayed when the user enters the menu in
	 * the game.
	 */
	public static final String[] CONTROLS = new String[] {
		"W+A+S+D        - Move", "L-Shift        - Sprint",
		"1 OR U         - Fire Bullet", "2 OR I         - Place Mine",
		"T              - Chat", "Space + Arrows - Pan",
		"Esc            - Close this" };

	/**
	 * An array of all selectable game modes.
	 * <p>
	 * <b>CTF</b>
	 * </p>
	 * <p>
	 * The Capture The Flag game mode starts a game with flags. The objective of
	 * the game is to capture the flag from the enemy team and return to your
	 * teams base. One flag grants that team one point.
	 * </p>
	 * <p>
	 * <b>Free for All</b>
	 * </p>
	 * <p>
	 * The Free for All gamemode is a mode where the players are out to kill 
	 * each other to earn points. The person to kill the most people in a given time
	 * wins.
	 * 
	 * </p>
	 */
	public static final String[] GAME_MODES = new String[] { "CTF",
	"Free for All" };

	/**
	 * A list of both human readable map names and their paths within the
	 * program. Human readable lines are on MAPS[0] and all map names are on
	 * MAPS[1]. There must be the same amount of readable names as map
	 * locations.
	 */
	public static final String[][] MAPS = new String[][] {
		{ "lobby", "fort", "pshield" }, // HUMAN
		{ "/levels/lobby.png", "/levels/fort.png", "/levels/pshield.png" } };// SYSTEM

	/**
	 * Array of song <i>locations</i> that can be played. The first song to be
	 * played from this list is random, then all other songs are played in
	 * order. When the end is reached, the first song in the list is played.
	 */
	public static final String[] songs = new String[] { "/music/All_This.mp3",
			"/music/Hitman.mp3", "/music/The_Complex.mp3" };

	/**
	 * The {@link Menu} used in game.
	 */
	public static final Menu systemMenu = new Menu(new String[] { "Quit",
			"Controls", "Start Game" }, false, "System Menu");

	/**
	 * The {@link Menu} used in game to select the game mode. Used only by the
	 * host of the game.
	 *
	 * @see Game#systemMenu
	 */
	public static final Menu gameModeMenu = new Menu(Game.GAME_MODES, false,
			"Mode");

	/**
	 * The {@link Menu} used in game to select the map. Used only by the host of
	 * the game.
	 *
	 * @see Game#systemMenu
	 */
	public static final Menu mapsMenu = new Menu(Game.MAPS[0], false, "Map");

	/**
	 * This instance of {@link Game}.
	 */
	public static Game game;

	/**
	 * The {@link JFrame} window the game is rendered to.
	 */
	public JFrame frame;

	/**
	 * The {@link Game}'s running thread.
	 */
	public boolean running = false;

	/**
	 * Used to determine where the program is currently running. When where is
	 * 0, the game renders the menu. When where is 1, the game renders the game.
	 */
	public int where = 0;
	private int onClip = (int) (Math.random() * songs.length);
	/**
	 * The {@link BufferedImage} that is rendered to the JFrame.
	 */
	private static BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);

	/**
	 * An array of ints that is used to draw the picture on the JFrame.
	 */
	private final int[] pixels = ((DataBufferInt) image.getRaster()
			.getDataBuffer()).getData();
	private final int[] colors = new int[6 * 6 * 6]; // ALL colors

	private Screen screen;
	public InputHandler input;
	public WindowHandler windowHandler;
	public Level level;
	public Level levelGui;

	/**
	 * The {@link Player} that is used in the game. The {@link Game}
	 * {@link Player} <code> player</code> is used locally. Use {@link PlayerMP}
	 * to create a new Multiplayer player.
	 *
	 * @see PlayerMP
	 */
	public Player player;

	/**
	 * The {@link GameClient} is used to handle all incoming packets to the
	 * client. A client only runs one {@link GameClient}.
	 */
	public GameClient socketClient;

	/**
	 * The {@link GameServer} is used to handle all incoming packets to the
	 * server. The port used by default on a {@link GameServer} is 9714. Only
	 * one instance of {@link GameServer} can be created. If a player does not
	 * host a server, this value will be null.
	 */
	private GameServer socketServer;

	private final Chat chat = new Chat();

	/**
	 * The locally stored username of the player.
	 */
	private String username = "";

	/**
	 * The locally stored ID of the player.
	 */
	private int id = -1;

	/**
	 * The {@link String} of the team the player is on.
	 */
	private String team = "";

	/**
	 * The {@link MenuAt} where {@link Menu} "menu" should be. This variable is
	 * used to guide players through the selection process of the game's main
	 * menu.
	 */
	private MenuAt at = MenuAt.RESET;

	/**
	 * The {@link TypeSystem} where {@link Menu} "systemMenu" should be. The
	 * system menu is a menu that appears in game.
	 *
	 * @see Game#at
	 */
	private TypeSystem atSystem = TypeSystem.CLOSED;

	private boolean quickFlip = false;

	/**
	 * The original spawn positions of the player when they connect to the
	 * server <i>for the first time</i>.
	 */
	private int tempX = 0, tempY = 0;

	/**
	 * The {@link MP3} player that sets up the sound system for audio.
	 */
	private final MP3 mp3player = new MP3();

	/**
	 * States if a game mode is running or not.
	 */
	private boolean gameRunning = false;

	/**
	 * Position of the camera. Follows the player when not pressing space.
	 */
	private int camX = 0, camY = 0;

	private double credits = 400;

	/**
	 * Time it takes to re-spawn in the game.
	 */
	private int respawnTimer = 1000;

	/**
	 * Selected options for when the host starts the game.
	 */
	private int selectedMode = -1, selectedMap = -1;

	/**
	 * Prevents the menu from proceeding to an unwanted option.
	 */
	private boolean menuJustPressed = false;

	/**
	 * The {@link Menu} used in the title screen.
	 */
	private Menu menu = new Menu(new String[] { "Host", "Join", "Credits",
	"Quit" }, false, "Welcome to " + NAME + " version " + version);
	
	/**
	 * The running game mode of the game.
	 */
	public String currentRunningGamemode;

	/**
	 * Creates the {@link Game} object.
	 */
	public Game() {
		Debug.out(Type.INFO, CLASS, "You are running " + version);
		Debug.out(Type.DEBUG, CLASS, "Starting game.");

		// Creates the minimum, maximum, and preferred size of the canvas.
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		frame = new JFrame(NAME); // Title of the game.

		// Sets up how the game closes and runs.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// Keeps everything sized correctly;
		frame.add(this, BorderLayout.CENTER);

		// Make sure we don't pull a "Mr. Miller"
		frame.setResizable(false);

		// Icons that appear in the task bar of the OS.
		final ArrayList<Image> icons = new ArrayList<Image>();
		icons.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/smallIcon.png")));
		icons.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/icon.png")));
		frame.setIconImages(icons);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}

	/**
	 * Things to run when the game is initialized. The stuff here is called in
	 * the run method.
	 */
	public void init() {
		game = this;
		int index = 0;

		// indexes every single color in the game.
		for (int r = 0; r < 6; r++) {
			for (int g = 0; g < 6; g++) {
				for (int b = 0; b < 6; b++) {
					final int rr = ((r * 255) / 5);
					final int gg = ((g * 255) / 5);
					final int bb = ((b * 255) / 5);

					// Format 770011 or something like that.
					colors[index++] = (rr << 16) | (gg << 8) | bb;
				}
			}
		}
		input = new InputHandler(this, 1); // sets up keyboard control.
		levelGui = new Level(this, null); // Used for the GUI.

		// Sets up our screen with the right sprite map.
		screen = new Screen(WIDTH, HEIGHT, new Sprites("/sprites.png"));

		level = new Level(this, null);
		frame.setVisible(true); // After init, show the frame.
	}

	/**
	 * Also known as "The game loop". This method is called because this class
	 * extends {@link Runnable}.
	 *
	 * @see Runnable#run()
	 */
	@Override
	public void run() {
		long lastTime = System.nanoTime(); // long time in nanoseconds.
		final double nsPerTick = 1000000000D / 60D; // limits updates to 60 ups
		int frames = 0; // frames drawn
		int ticks = 0; // updates drawn
		long lastTimer = System.currentTimeMillis();
		double wait = 0; // unprocessed nanoseconds.

		init();

		while (running) {
			final long now = System.nanoTime();
			wait += (now - lastTime) / nsPerTick;
			lastTime = now;
			while (wait >= 1D) {
				ticks++; // Update drawn ticks.
				if (where == 0) {
					tickGui();
				} else {
					tick();
				}
				wait = wait - 1D; // Do we need to catch up from the last tick's
				// computation?
			}

			frames++;
			if (where == 0) {
				renderGui();
			} else {
				render();
			}

			if ((System.currentTimeMillis() - lastTimer) > 1000) {
				lastTimer += 1000;
				final int entities = level.getEntities().size();
				frame.setTitle(NAME + " - " + ticks + " ticks, " + frames
						+ " frames, " + entities
						+ ((entities == 1) ? " entity." : " entities."));
				frames = 0;
				ticks = 0;
			}
		}
		stop();
	}

	/**
	 * Starts up the game and creates a thread to run on.
	 */
	private synchronized void start() {
		Debug.out(Type.INFO, CLASS, "Thread started. Game running.");
		running = true; // Initializes the running boolean
		new Thread(this).start();
	}

	/**
	 * Shuts down the tread and game.
	 */
	private synchronized void stop() {
		running = false;
		Debug.out(Type.INFO, CLASS, "Game quit.");
		Runtime.getRuntime().halt(0);
	}

	/**
	 * Updates the logic of the main menu. Most of the main menu logic is
	 * handled in the {@link Game#renderGui()} method.
	 */
	public void tickGui() {
		levelGui.tick(); // does some level specific game logic.
		if (credits <= 499) {
			credits -= 1.25;
		}
		tickGuiMenu();
	}

	/**
	 * This method computes and draws the main menu.
	 */
	public void renderGui() {
		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // Triple buffering!
			return;
		}

		levelGui.renderTiles(screen, camX, camY); // render the tiles, please.
		levelGui.renderEntities(screen); // render ents.
		menu.render(screen, 0, 0);

		switch (at) {
		default:
		case RESET:
			break;
		case CREDITS:
			if (((Credits.render(screen, (int) credits) + credits) + 100) < 0) {
				at = MenuAt.RESET;
			}
			break;
		case MAIN:
			break;
		case USERNAME:
			break;
		case TEAM:
			break;
		case PRE:
			break;
		case CONNECT:
			break;
		case POST:
			break;
		}

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				final int colorCode = screen.pixels[x + (y * screen.width)];
				if (colorCode < 255) {
					pixels[x + (y * WIDTH)] = colors[colorCode];
				}
			}
		}
		final Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}

	/**
	 * Updates the logic of the game. This is to sync faster computers with
	 * slower ones. If there was no tick, then slow computers would fall behind.
	 */
	public void tick() {
		chat.tick();

		if (mp3player.isIdle()) {
			mp3player.changeMusic(songs[onClip]);
			mp3player.play();
			onClip++;
			if (onClip >= songs.length) {
				onClip = 0;
			}
		}

		garbageCollect();

		final String message = input.getSendableMessage();
		if (message != null) {
			if (!message.trim().equals("")) {
				final Packet04Chat chatter = new Packet04Chat(
						player.getUsername(), 555, ": " + message);
				chatter.writeData(socketClient);
			}
		}

		if (player.canRegenHealth()) {
			final Packet05Damage packet = new Packet05Damage(username, -1);
			packet.writeData(socketClient);
		}

		if (!player.isAlive()) {
			respawnTimer--;
			if ((respawnTimer % 100) > 60) {
				respawnTimer = (((int) (respawnTimer / 100)) * 100) + 60;
			}
			if (respawnTimer == 950) {
				final Packet04Chat packet = new Packet04Chat(
						username,
						005,
						" "
								+ DEATH_MESSAGE[(int) (Math.random() * DEATH_MESSAGE.length)]);
				packet.writeData(socketClient);
			}
			if (respawnTimer < 1) {
				final Packet05Damage resetHealth = new Packet05Damage(
						player.getUsername(), -20);
				resetHealth.writeData(socketClient);
				if (team.equals("RED")) {
					final Packet02Move moveToSpawn = new Packet02Move(username,
							(level.width * 32) - 100,
							(level.height * 32) - 100, false, 0, false);
					moveToSpawn.writeData(socketClient);
					player.x = (level.width * 32) - 100;
					player.y = (level.height * 32) - 100;
				} else {
					final Packet02Move moveToSpawn = new Packet02Move(username,
							100, 100, false, 0, false);
					moveToSpawn.writeData(socketClient);
					player.x = 100;
					player.y = 100;
				}
				respawnTimer = 1010;
				player.setFlag(false);
			}
		}

		tickMenu();
		tickCamera();
		level.tick(); // does some level specific game logic.
	}

	/**
	 * Renders the logic of the game. Draws (with a crayon, of course) the game.
	 */
	public void render() {

		final BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // Triple buffering!
			return;
		}

		level.renderTiles(screen, camX, camY); // render the tiles, please.
		level.renderEntities(screen); // render ents.

		if (input.isTyping()) { // render chat
			final String msg = input.getMessage() + "|";
			chat.renderChat(screen, true, msg);
		} else {
			chat.renderChat(screen, false, "");
		}

		if (!player.isAlive()) {
			String render = "YOU DIED";
			Font.render(render, screen, (screen.xOffset + (WIDTH / 2))
					- (((render.length() * 22) * 3) / 2), screen.yOffset,
					Colors.get(-1, -1, -1, 500), 3);
			render = "Respawn in: " + (int) (respawnTimer / 100) + ":"
					+ ((respawnTimer % 100) < 10 ? "0" : "")
					+ (respawnTimer % 100);
			Font.render(render, screen, (screen.xOffset + (WIDTH / 2))
					- (((render.length() * 22) * 1) / 2), screen.yOffset + 168,
					Colors.get(-1, -1, -1, 000), 1);
		}

		switch (atSystem) {
		default:
		case CLOSED:
			break;
		case SYSTEM:
			systemMenu.render(screen, 0, 32);
			if (!gameRunning) {
				Font.render("Welcome to the Lobby. Please", screen,
						screen.xOffset + 200, screen.yOffset + 32,
						Colors.get(-1, -1, -1, 542), 0.5);
				Font.render("wait for the host to the game!", screen,
						screen.xOffset + 200, screen.yOffset + 48,
						Colors.get(-1, -1, -1, 542), 0.5);
			}
			break;
		case MODE:
			gameModeMenu.render(screen, 0, 32);
			break;
		case MAP:
			mapsMenu.render(screen, 0, 32);
			break;
		case CONTROLS:
			int controlDex = 16;
			for (final String s : CONTROLS) {
				Font.render(s, screen, screen.xOffset, screen.yOffset
						+ (controlDex += 16), Colors.get(-1, -1, -1, 555), 0.5);
			}
			break;
		}

		for (int y = 0; y < screen.height; y++) {
			for (int x = 0; x < screen.width; x++) {
				final int colorCode = screen.pixels[x + (y * screen.width)];
				if (colorCode < 255) {
					pixels[x + (y * WIDTH)] = colors[colorCode];
				}
			}
		}
		final Graphics g = bs.getDrawGraphics();
		if (player.getDamageHit() > 0) {
			g.drawImage(image, (int) ((Math.random() * 20) - 10),
					(int) ((Math.random() * 20) - 10), getWidth(), getHeight(),
					null);
		} else {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		}
		g.dispose();
		bs.show();
	}

	/**
	 * Helper method for rendering the position of the camera.
	 */
	private void tickCamera() {
		// For the keys.
		if (camY > 0) {
			if (input.camup.isPressed()) {
				camY -= 4;
			}
		} else {
			camY = 0;
		}

		if (camY < ((level.height * 32) - HEIGHT)) {
			if (input.camdown.isPressed()) {
				camY += 4;
			}
		} else {
			camY = (level.height * 32) - HEIGHT;
		}

		if (camX > 0) {
			if (input.camleft.isPressed()) {
				camX -= 4;
			}
		} else {
			camX = 0;
		}

		if (camX < ((level.width * 32) - WIDTH)) {
			if (input.camright.isPressed()) {
				camX += 4;
			}
		} else {
			camX = (level.width * 32) - WIDTH;
		}

		if (!input.camcenter.isPressed()) {
			camX = player.x - (screen.width / 2);
			camY = player.y - (screen.height / 2);
		}
	}

	private void tickGuiMenu() {
		final String[] typedFields = menu.getTypedFields(input.textmessage
				.isPressed());

		if (menu.requestSet() != null) {
			input.setMessage(menu.requestSet());
			menu.setRequestSet();
		}

		if (menu.requestClear()) {
			input.clearMessage();
			menu.setRequestClear();
		}

		menu.setTypedFields(menu.selected(), input.getMessage());
		menu.nextItem(input.camdown.isPressed());
		menu.previousItem(input.camup.isPressed());

		switch (at) {
		default:
		case RESET:
			menu = new Menu(new String[] { "Host", "Join", "Credits", "Quit" },
					false, "Welcome to " + NAME + " version " + version);
			credits = 400;
			at = MenuAt.MAIN;
			break;
		case CREDITS:
			if (input.textmessage.isPressed()) {
				if (menu.selected() == 0) {
					quickFlip = true;
				}
			}
			if (quickFlip && (menu.selected == 0)
					&& !input.textmessage.isPressed()) {
				quickFlip = false;
				mp3player.stopMusic();
				at = MenuAt.RESET;
			}
			break;
		case MAIN:
			if (input.textmessage.isPressed()) {
				if (menu.selected() == 0) {
					input.setTyping(true);
					menu = new Menu(new String[] { "Username: " }, true,
							"Host a game...");
					at = MenuAt.USERNAME;
				} else if (menu.selected() == 1) {
					input.setTyping(true);
					menu = new Menu(new String[] { "Username: ",
					"Server Host: " }, true, "Join a game...");
					at = MenuAt.USERNAME;
				} else if (menu.selected() == 2) {
					quickFlip = true;
				} else if (menu.selected() == 3) {
					stop();
				}
			}
			if (quickFlip && (menu.selected == 2)
					&& !input.textmessage.isPressed()) {
				menu = new Menu(new String[] { "Return" }, false, "Credits");
				credits = 399; // Start the credits (See GuiTick())
				quickFlip = false;
				mp3player.changeMusic("/music/Evening_Melodrama.mp3");
				mp3player.play();
				at = MenuAt.CREDITS;
			}
			break;
		case USERNAME:
			if (input.textmessage.isPressed()
					&& ((menu.selected == 2) || (menu.selected == 3))) {
				quickFlip = true;
			}
			if (quickFlip && ((menu.selected == 2) || (menu.selected == 3))
					&& !input.textmessage.isPressed()) {
				quickFlip = false;
				at = MenuAt.RESET;
			}
			if (typedFields != null) {
				if (typedFields.length == 1) { // CODE FOR HOSTING A GAME
					menu = new Menu(new String[] { "Abort!" }, false,
							"Starting server...");
					socketServer = new GameServer(this);
					socketServer.start();
					createUsername(typedFields[0]);
					socketClient = new GameClient(this, "127.0.0.1");
					socketClient.start();
					final Packet03TestLogin packet = new Packet03TestLogin(
							username, -1, version); // Will request the teams
					// from the server.
					packet.writeData(socketClient);
					level.regenLevel("/levels/lobby.png");
					at = MenuAt.TEAM;
				} else if (typedFields.length == 2) {// CODE FOR JOINING A GAME
					menu = new Menu(new String[] { "Abort!" }, false,
							"CONNECTING...");
					createUsername(typedFields[0]);
					socketClient = new GameClient(this, typedFields[1]);
					socketClient.start();
					final Packet03TestLogin packet = new Packet03TestLogin(
							username, -1, version); // Will request the teams
					// from the server.
					packet.writeData(socketClient);
					at = MenuAt.TEAM;
				}
			}
			break;
		case TEAM:
			if (input.textmessage.isPressed()) {
				if (menu.selected() == 0) {
					stop();
				}
			}
			if ((socketClient.getRed() != -1)
					&& (socketClient.getGreen() != -1)) {
				menu = new Menu(
						new String[] {
								"Join team Green ("
										+ new Integer(socketClient.getGreen()).toString()
										+ ")",
								"Join team Red   ("
												+ new Integer(socketClient.getRed()).toString()
												+ ")" }, false, "Select a team!");
				at = MenuAt.PRE;
			}
			break;
		case PRE:
			if (input.textmessage.isPressed()) {
				if (menu.selected() == 0) {
					final Packet03TestLogin packet = new Packet03TestLogin(
							username, -1, version, -1, 0); // Will request the
					// teams from the server.
					packet.writeData(socketClient);
					team = "GREEN";
					at = MenuAt.CONNECT;
				} else if (menu.selected() == 1) {
					final Packet03TestLogin packet = new Packet03TestLogin(
							username, -1, version, 0, -1); // Will request the
					// teams from the server.
					packet.writeData(socketClient);
					team = "RED";
					at = MenuAt.CONNECT;
				}
			}
			break;
		case CONNECT:
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			if (socketClient.getLoginable() == 1) {
				at = MenuAt.POST;
				Debug.out(Type.INFO, CLASS,
						"Your ID is: " + socketClient.getID());
				id = socketClient.getID();
			}
			if (socketClient.getLoginable() == 2) {
				Debug.out(Type.SEVERE, CLASS, "The version you are using,  "
						+ version + " is not the same as the server version "
						+ socketClient.getVersion());
			}
			if (socketClient.getLoginable() == 3) {
				Debug.out(Type.SEVERE, CLASS, "The username " + username
						+ " is already taken!");
			}
			break;
		case POST:
			windowHandler = new WindowHandler(this);// Sends disconnect on close
			player = new PlayerMP(level, id, 100, 100, input, username,
					null, -1, team);
			level.addEntity(player);
			final Packet00Login loginPacket = new Packet00Login(username, id,
					player.x, player.y, team);
			if (socketServer != null) {
				socketServer.addConnection((PlayerMP) player, loginPacket);
			}
			loginPacket.writeData(socketClient);
			while(level.getPath() == null) {
				try {
					Thread.sleep(100);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if(team.equals("GREEN")) {
				tempX = 100;
				tempY = 100;
			} else {
				tempX = (level.width * 32) - 100;
				tempY = (level.height * 32) - 100;
			}
			
			final Packet02Move moveToSpawn = new Packet02Move(username,
					tempX, tempY, false, 0, false);
			player.setPos(tempX, tempY);
			moveToSpawn.writeData(socketClient);
			input.setMode(0);
			input.setTyping(false);
			mp3player.changeMusic("/music/Light_Sting.mp3");
			mp3player.play();
			try {
				Thread.sleep(100);
			} catch (final Exception e) {
				e.printStackTrace();
			}
			final Packet13Score score = new Packet13Score(username, team, 0);
			score.writeData(level.getGame().socketClient);
			where = 1;
			break;
		}
	}

	/**
	 * Helper method for tiplcking the logic of the System Menu.
	 */
	private void tickMenu() {
		if (input.escape.isPressed() && (atSystem == TypeSystem.CLOSED)
				&& !menuJustPressed) {
			atSystem = TypeSystem.SYSTEM;
			menuJustPressed = true;
		}
		if (!input.escape.isPressed() && !input.textmessage.isPressed()) {
			menuJustPressed = false;
		}
		if (input.escape.isPressed() && !(atSystem == TypeSystem.CLOSED)
				&& !menuJustPressed) {
			atSystem = TypeSystem.CLOSED;
			menuJustPressed = true;
		}
		switch (atSystem) {
		default:
		case CLOSED:
			systemMenu.selected = 0;
			break;
		case SYSTEM:
			if (input.isTyping()) {
				atSystem = TypeSystem.CLOSED;
				break;
			}
			if (input.textmessage.isPressed()) {
				if (systemMenu.selected() == 0) {
					Debug.out(Type.INFO, CLASS, "Quit the game!");
					frame.dispatchEvent(new WindowEvent(frame,
							WindowEvent.WINDOW_CLOSING));
				}
				if (systemMenu.selected() == 1) {
					atSystem = TypeSystem.CONTROLS;
					menuJustPressed = true;
				}
				if (systemMenu.selected() == 2) {
					if (socketServer == null) {
						chat.addMessageToHistory("",
								"You are not the server host!", 500);
						atSystem = TypeSystem.CLOSED;
						menuJustPressed = true;
					} else {
						atSystem = TypeSystem.MODE;
						menuJustPressed = true;
					}
				}
			}
			systemMenu.nextItem(input.camdown.isPressed());
			systemMenu.previousItem(input.camup.isPressed());
			break;
		case MODE:
			if (input.textmessage.isPressed() && !menuJustPressed) {
				selectedMode = gameModeMenu.selected();
				atSystem = TypeSystem.MAP;
				menuJustPressed = true;
			}
			gameModeMenu.nextItem(input.camdown.isPressed());
			gameModeMenu.previousItem(input.camup.isPressed());
			break;
		case MAP:
			if (input.textmessage.isPressed() && !menuJustPressed) {
				selectedMap = mapsMenu.selected();
				atSystem = TypeSystem.CLOSED;
				menuJustPressed = true;
				socketServer.startGame(getMode(), getMap(), getSystemMap());
				final Packet04Chat message2 = new Packet04Chat(username, 050,
						": The host changed the level!");
				message2.writeData(socketClient);
			}
			mapsMenu.nextItem(input.camdown.isPressed());
			mapsMenu.previousItem(input.camup.isPressed());
			break;
		}
	}

	/**
	 * Makes sure that a username makes certain specifications.
	 *
	 * @param username
	 *            {@link String} Raw username.
	 * @return {@link String} A structured username. Also sets the actual
	 *         {@link Game#username}.
	 */
	private String createUsername(String username) {
		if (username == null) {
			Debug.out(Type.SEVERE, CLASS, "Null username!");
		}
		username = username.trim();
		if (username.length() > 10) {
			username = username.substring(0, 10);
		}
		for (int i = 0; i < username.length(); i++) {
			if (username.substring(i, i + 1).equals(",")) {
				username = username.substring(0, i) + username.substring(i + 1);
				i--;
			}
		}
		if (username.equalsIgnoreCase("")
				|| username.substring(0, 1).equals("!")
				|| username.equalsIgnoreCase("server")
				|| username.equals("nbSpTst")) {
			Debug.out(Type.SEVERE, CLASS,
					"This is a reserved or invalid username!");
		}
		Debug.out(Type.INFO, CLASS, "Username: " + username);
		this.username = username;
		return username;
	}

	/**
	 * Collects and removes garbage from the game.
	 */
	private synchronized void garbageCollect() {
		int i = 0;
		for (final Entity e : level.getEntities()) {
			if (e.isMarkedForDelete()) {
				break;
			}
			i++;
		}
		if (i < level.getEntities().size()) {
			level.getEntities().remove(i);
		}
	}

	/**
	 * Adds a new message to the chatter from an external source.
	 *
	 * @param username
	 *            {@link String} Username that said the message.
	 * @param message
	 *            {@link String} Message
	 * @param color
	 *            Hex based int from 000 to 555.
	 */
	public void addMessageToHistory(String username, String message, int color) {
		chat.addMessageToHistory(username, message, color);
	}

	/**
	 * Gets the {@link Game#username}.
	 *
	 * @return A {@link String} username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the {@link Game#team}.
	 *
	 * @return A {@link String} team.
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * Gets the game mode that was selected by the host. Game MUST be selected
	 * before a call to this method!
	 *
	 * @return The {@link String} selected game mode.
	 */
	public String getMode() {
		return GAME_MODES[selectedMode];
	}

	/**
	 * Gets the map that was selected by the host. Game MUST be selected before
	 * a call to this method!
	 *
	 * @return {@link String} map.
	 */
	public String getMap() {
		return MAPS[0][selectedMap];
	}

	/**
	 * Gets the system path of a map that was selected by the host. Game MUST be
	 * selected before a call to this method!
	 *
	 * @return {@link String} system map.
	 */
	public String getSystemMap() {
		return MAPS[1][selectedMap];
	}

	/**
	 * Sets if a game mode is running or not. This is called when a host starts
	 * the game mode.
	 *
	 * @param gameRunning
	 *            boolean
	 */
	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}

	/**
	 * The main method. This is called to start the java application.
	 *
	 * @param args
	 *            unused.
	 */
	public static void main(String[] args) {
		new Game().start();
	}

	/**
	 * Checks if the socketServer is null or not.
	 * @return <code>True</code> if the server is null.
	 */
	public boolean socketServerIsNull() {
		return socketServer == null;
	}
}