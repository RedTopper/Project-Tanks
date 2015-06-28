package game.level.tiles;

import game.gfx.Screen;
import game.level.Level;

/**
 * A standard tile.
 *
 * @author AJ Walter
 * @see Tile
 */
public class BaseTile extends Tile {

	/**
	 * Position of the tile on the spritesheet.
	 */
	protected int tileId;

	/**
	 * Color the tile will be when it is rendered.
	 */
	protected int tileColor;

	/**
	 * Constructs a new BaseTile.
	 * 
	 * @param id
	 *            ID of the tile
	 * @param x
	 *            X position to be rendered to.
	 * @param y
	 *            Y position to be rendered to.
	 * @param tileColor
	 *            Color the tile will be when rendered.
	 * @param levelColor
	 *            Color the tile is in the PNG image.
	 * @see BaseTile
	 */
	public BaseTile(int id, int x, int y, int tileColor, int levelColor) {
		super(id, false, levelColor);
		tileId = x + (y * 16);
		this.tileColor = tileColor;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x, y, tileId, tileColor);
	}
}