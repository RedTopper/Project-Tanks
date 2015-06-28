package game.level.tiles;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;

/**
 * This class represents Tiles that exist within the map of a game. A tile is an
 * object that is static and moves with the map. Some tiles have the ability to
 * stop the player, while others act as a floor to the map.
 *
 * @author AJ Walter
 */
public abstract class Tile {

	/**
	 * Array of {@link Tile}s that represent the available tiles within the
	 * game.
	 */
	public static final Tile[] tiles = new Tile[256];

	/**
	 * Void tile for when no specific tile can be found. Creates weird graphical
	 * glitches.
	 */
	public static final Tile VOID = new BaseSolidTile(0, 0, 0, Colors.get(000,
			-1, -1, -1), 0xFFAF0075);
	/**
	 * Standard floor for a player to stand on.
	 */
	public static final Tile FLOOR = new BaseTile(1, 1, 0, Colors.get(222, 110,
			210, 320), 0xFFFFFFFF);

	/**
	 * Standard wall for the player to collide into.
	 */
	public static final Tile WALL = new BaseSolidTile(2, 2, 0, Colors.get(100,
			200, 300, 200), 0xFF000000);

	/**
	 * A graphical variant of a wall.
	 *
	 * @see Tile#WALL
	 */
	public static final Tile WALLV1 = new BaseSolidTile(3, 3, 0, Colors.get(
			000, 555, 555, 555), 0xFF202020), 
			WALLV2 = new BaseSolidTile(4, 4, 0, Colors.get(000, 555, 550, 555), 
					0xFF404040),
			WALLV3 = new BaseSolidTile(5, 5, 0, Colors.get(000, 555, 555, 555),
					0xFF606060), 
			WALLV4 = new BaseSolidTile(6, 6, 0, Colors.get(000, 555, 555, 555),
					0xFF818181);

	/**
	 * A graphical variant of a floor.
	 *
	 * @see Tile#FLOOR
	 */
	public static final Tile FLOOR1 = new BaseTile(7, 7, 0, Colors.get(111,
			110, 210, 320), 0xFFe0e0e0), 
			FLOOR2 = new BaseTile(8, 8, 0, Colors.get(300, 110, 210, 320), 
					0xFFc0c0c0), 
			FLOOR3 = new BaseTile(9, 9, 0, Colors.get(222, 110, 210, 320), 
					0xFFa0a0a0),
			FLOOR4 = new BaseTile(10, 10, 0, Colors.get(222, 110, 210, 320),
					0xFF808080);

	/**
	 * Tile only specific to the main menu. Rendered behind the title screen.
	 */
	public static final Tile GUI = new BaseTile(11, 0, 1, Colors.get(000, 111,
			222, 555), 0xFF808080);

	/**
	 * Unique ID of the type of tile this is.
	 */
	protected byte id;

	/**
	 * <code>True</code> if the tile is solid, <code>False</code> otherwise.
	 */
	protected boolean solid;

	/**
	 * Color of the tile in the PNG image.
	 */
	private final int levelColor;

	/**
	 * Creates a new tile.
	 * 
	 * @param id
	 *            ID of the tile.
	 * @param isSolid
	 *            <code>True</code> if the tile is solid.
	 * @param levelColor
	 *            Color of the tile in the PNG image.
	 */
	public Tile(int id, boolean isSolid, int levelColor) {
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Duplicate tile ID on " + id);
		}
		solid = isSolid;
		this.levelColor = levelColor;
		tiles[id] = this;
	}

	/**
	 * Gets the ID of this tile.
	 * @return ID of this tile.
	 */
	public byte getId() {
		return id;
	}

	/**
	 * Gets if this tile is solid or not.
	 * @return <code>True</code> if the tile is solid.
	 */
	public boolean isSolid() {
		return solid;
	}

	/**
	 * Gets the color of the tile in the PNG image.
	 * @return Hex based in of the color in the PNG image.
	 */
	public int getLevelColor() {
		return levelColor;
	}

	/**
	 * Draws the tile to the {@link Screen}.
	 * @param screen 
	 * 				{@link Screen} to render on.
	 * @param level 
	 * 				{@link Level} the tile is in.
	 * @param x 
	 * 				X position.
	 * @param y 
	 * 				Y position.
	 */
	public abstract void render(Screen screen, Level level, int x, int y);
}
