package game.level;

import game.Game;
import game.entities.Entity;
import game.entities.PlayerMP;
import game.gfx.Screen;
import game.level.tiles.Tile;
import game.utils.Debug;
import game.utils.Type;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * This class contains the methods needed to set tiles, get tiles, and render
 * the level of the game. A level object also contains a list of entities that
 * exist within the level.
 *
 * @author AJ Walter
 */
public class Level {
	
	/**
	 * Name of the class.
	 */
	public static final String CLASS = "Level";

	/**
	 * A byte array that contains the IDs of the tiles.
	 *
	 * @see Tile
	 */
	private byte[] tiles;

	/**
	 * Dimensions of the level in {@link Tile}s.
	 */
	public int width, height;
	/**
	 * This list contains all of the entities being rendered in the game. If an
	 * {@link Entity} is in this list and it is not {@link Entity#markForDelete}
	 * , it will be rendered to the screen.
	 */
	private final List<Entity> entities = new ArrayList<>();

	/**
	 * Path of the file the level is being rendered from.
	 */
	private String imagePath;

	/**
	 * The {@link BufferedImage} gathered from the path.
	 */
	private BufferedImage image;

	/**
	 * The {@link Game}.
	 */
	private final Game game;

	/**
	 * Creates a new level.
	 *
	 * @param game
	 *            The {@link Game}.
	 * @param imagePath
	 *            {@link String} location of the path. If null, the level will
	 *            attempt to create a standard level.
	 */
	public Level(Game game, String imagePath) {
		if (imagePath != null) {
			this.imagePath = imagePath;
			loadLevelFromFile();
		} else {
			width = 64;
			height = 64;
			tiles = new byte[width * height];
			this.imagePath = null;
			generateLevel();
		}
		this.game = game;
	}

	/**
	 * Recreates this object with a new {@link Level}.
	 *
	 * @param imagePath
	 *            {@link String} location of the path. If null, the level will
	 *            attempt to create a standard level.
	 */
	public synchronized void regenLevel(String imagePath) {
		if (imagePath != null) {
			this.imagePath = imagePath;
			loadLevelFromFile();
		} else {
			width = 64;
			height = 64;
			tiles = new byte[width * height];
			this.imagePath = null;
			generateLevel();
		}
	}
	
	/**
	 * Recreates this object with a new {@link Level}.
	 *
	 * @param imagePath
	 *            {@link String} Actual image file of the thing we need to load.
	 * @param buf
	 * 				Buffered image to regenerate the level with.
	 */
	public synchronized void regenLevel(String imagePath, BufferedImage buf) {
		if (imagePath != null) {
			this.imagePath = imagePath;
			loadLevelFromFile(buf);
			Debug.out(Type.DEBUG, CLASS, "Finished loading network " + imagePath + ".");
		} else {
			width = 64;
			height = 64;
			tiles = new byte[width * height];
			this.imagePath = null;
			generateLevel();
		}
	}

	/**
	 * Helper method that loads a level from a given path.
	 */
	private void loadLevelFromFile() {
		try {
			image = ImageIO.read(Level.class.getResource(imagePath));
			width = image.getWidth();
			height = image.getHeight();
			tiles = new byte[width * height];
			loadTiles();
		} catch (final Exception e) {
			e.printStackTrace();
			Debug.out(Type.SEVERE, CLASS, "Failed to load " + imagePath + "!");
		}
	}
	
	/**
	 * Helper method that loads a level from a given image.
	 */
	private void loadLevelFromFile(BufferedImage buf) {
		try {
			image = buf;
			Debug.out(Type.DEBUG, CLASS, "Loaded network " + imagePath + ".");
			width = image.getWidth();
			height = image.getHeight();
			tiles = new byte[width * height];
			loadTiles();
		} catch (final Exception e) {
			e.printStackTrace();
			Debug.out(Type.SEVERE, CLASS, "Failed to load network " + imagePath + "!");
		}
	}

	/**
	 * Helper method that generates an array of tiles.
	 */
	private void loadTiles() {
		final int[] tileColors = image.getRGB(0, 0, width, height, null, 0,
				width);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tileCheck: for (final Tile t : Tile.tiles) {
					if ((t != null)
							&& (t.getLevelColor() == tileColors[x + (y * width)])) {
						tiles[x + (y * width)] = t.getId();
						break tileCheck;
					}
				}
			}
		}
	}

	/**
	 * Changes a tile at the given location.
	 *
	 * @param x
	 *            X location of a tile.
	 * @param y
	 *            Y location of a tile.
	 * @param newTile
	 *            Tile to change (x,y) to.
	 */
	public void alterTile(int x, int y, Tile newTile) {
		tiles[x + (y * width)] = newTile.getId();
		image.setRGB(x, y, newTile.getLevelColor());
	}

	/**
	 * Generates an empty level.
	 */
	public void generateLevel() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				tiles[x + (y * width)] = Tile.GUI.getId();
			}
		}
	}

	/**
	 * Prevents the list from being accessed and written at the same time. That
	 * would cause problem. This fix problem.
	 *
	 * @return entities.
	 */
	public synchronized List<Entity> getEntities() {
		return entities;
	}

	/**
	 * Updates the level.
	 */
	public synchronized void tick() {
		for (final Entity e : getEntities()) {
			if (!e.isMarkedForDelete()) {
				e.tick();
			}
		}
	}

	/**
	 * Renders the tiles contained within the level.
	 *
	 * @param screen
	 *            The {@link Screen} to render to.
	 * @param xOffset
	 *            X Offset position to render. Used to shift the camera.
	 * @param yOffset
	 *            Y Offset position to render. Used to shift the camera.
	 */
	public void renderTiles(Screen screen, int xOffset, int yOffset) {
		if (xOffset < 0) {
			xOffset = 0;
		}
		if (xOffset > ((width << 5) - screen.width)) {
			xOffset = ((width << 5) - screen.width);
		}
		if (yOffset < 0) {
			yOffset = 0;
		}
		if (yOffset > ((height << 5) - screen.height)) {
			yOffset = ((height << 5) - screen.height);
		}

		screen.setOffset(xOffset, yOffset);

		for (int y = (yOffset >> 5); y < (((yOffset + screen.height) >> 5) + 1); y++) {
			for (int x = (xOffset >> 5); x < (((xOffset + screen.width) >> 5) + 1); x++) {
				getTile(x, y).render(screen, this, x << 5, y << 5);
			}
		}
	}

	/**
	 * Renders the entities contained within the level.
	 *
	 * @param screen
	 *            The {@link Screen} to render to.
	 */
	public synchronized void renderEntities(Screen screen) {
		for (final Entity e : getEntities()) {
			if (!e.isMarkedForDelete()) {
				e.render(screen);
			}
		}
	}

	/**
	 * Gets a {@link Tile} at a the specified location.
	 *
	 * @param x
	 *            X position in the level
	 * @param y
	 *            Y position in the level
	 * @return {@link Tile} at the X and Y position.
	 */
	public Tile getTile(int x, int y) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return Tile.VOID;
		}
		return Tile.tiles[tiles[x + (y * width)]];
	}

	/**
	 * Adds an entity to the top layer of the game. The game will automatically
	 * tick and update entities within this list.
	 *
	 * @param ent
	 *            {@link Entity} added.
	 */
	public synchronized void addEntity(Entity ent) {
		getEntities().add(ent);
	}

	/**
	 * Adds an entity to the bottom layer of the game.
	 *
	 * @param ent
	 *            {@link Entity} added.
	 * @see Level#addEntity(Entity)
	 */
	public synchronized void addLowestEntity(Entity ent) {
		getEntities().add(0, ent);
	}

	/**
	 * Finds a player and destroys it from the game.
	 *
	 * @param username
	 *            {@link String} of the username to remove.
	 */
	public synchronized void removePlayerMP(String username) {
		int index = 0;
		for (final Entity e : getEntities()) {
			if ((e instanceof PlayerMP)
					&& ((PlayerMP) e).getUsername().equals(username)) {
				break;
			}
			index++;
		}
		getEntities().remove(index);
	}

	/**
	 * Damages a player.
	 *
	 * @param username
	 *            {@link String} of the username to damage.
	 * @param damage
	 *            Amount of health to damage.
	 */
	public synchronized void damagePlayer(String username, int damage) {
		final int index = getPlayerMPIndex(username);
		((PlayerMP) (getEntities().get(index))).takeDamage(damage);
	}

	/**
	 * Gets the index of a matching player in the entities list.
	 *
	 * @param username
	 *            {@link String} of the username to look for.
	 * @return int index of the found player. Returns the size of the array if
	 *         not found.
	 */
	private synchronized int getPlayerMPIndex(String username) {
		int index = 0;
		for (final Entity e : getEntities()) {
			if ((e instanceof PlayerMP)
					&& ((PlayerMP) e).getUsername().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	/**
	 * Moves a player to a location.
	 *
	 * @param username
	 *            Player to move.
	 * @param x
	 *            X position to move to.
	 * @param y
	 *            Y position to move to.
	 * @param isMoving
	 *            <code>True</code> if the player is moving.
	 * @param movingDir
	 *            direction of the player. {@link game.entities.Mob#movingDir}
	 * @param hasFlag
	 *            <code>True</code> if the player has a flag.
	 */
	public synchronized void movePlayer(String username, int x, int y,
			boolean isMoving, int movingDir, boolean hasFlag) {
		final int index = getPlayerMPIndex(username);
		final PlayerMP player = (PlayerMP) getEntities().get(index);
		player.setPos(x, y);
		player.setMoving(isMoving);
		player.setMovingDir(movingDir);
		player.setFlag(hasFlag);
	}

	/**
	 * Returns the game object.
	 *
	 * @return {@link Game} object.
	 */
	public synchronized Game getGame() {
		return game;
	}
	
	/**
	 * Get the buffered image object of this level.
	 * @return buffered image.
	 */
	public synchronized BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Gets the path of this object.
	 * @return String representation of this path.
	 */
	public String getPath() {
		return imagePath;
	}
}