package game.level.tiles;

/**
 * A solid tile.
 * 
 * @author AJ Walter
 * @see BaseTile
 * @see Tile
 */
public class BaseSolidTile extends BaseTile {

	/**
	 * Creates a new BaseSolidTile.
	 * 
	 * @param id
	 *            ID of the tile.
	 * @param x
	 *            X position of the tile.
	 * @param y
	 *            Y position of the tile.
	 * @param tileColor
	 *            Physical color of the tile.
	 * @param levelColor
	 *            PNG color of the tile.
	 * @see BaseSolidTile
	 */
	public BaseSolidTile(int id, int x, int y, int tileColor, int levelColor) {
		super(id, x, y, tileColor, levelColor);
		solid = true;
	}
}
