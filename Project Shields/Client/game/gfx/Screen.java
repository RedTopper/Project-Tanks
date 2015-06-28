package game.gfx;

import game.utils.Debug;
import game.utils.Type;

/**
 * Anything that is rendered ever will use a screen. A screen is a collection of
 * pixels and colors, along with a collection of rendering methods to draw to
 * the {@link game.Game}'s window.
 *
 * @author AJ Walter
 *
 */
public class Screen {

	/**
	 * Name of the class.
	 */
	public static final String CLASS = "Screen";

	/**
	 * Width of the sprite sheet (should be square).
	 */
	public static final int MAP_WIDTH = 512, MAP_WIDTH_MASK = MAP_WIDTH - 1;

	/**
	 * A one dimensional array containing ints that represent pixels on the
	 * screen.
	 */
	public int[] pixels;

	/**
	 * Variables used to render the camera.
	 */
	public int xOffset = 0, yOffset = 0;

	/**
	 * Dimensions of this {@link Screen}.
	 */
	public int width, height;

	/**
	 * The sprite sheet to obtain the graphics from.
	 */
	public Sprites sheet;

	/**
	 * Creates the Screen. When it is initialized, it colorizes every available
	 * pixel.
	 *
	 * @param width
	 *            width of the {@link Screen}.
	 * @param height
	 *            height of the {@link Screen}.
	 * @param sheet
	 *            {@link Sprites} to use.
	 */
	public Screen(int width, int height, Sprites sheet) {
		Debug.out(Type.DEBUG, CLASS, "Created a Screen with the instance of "
				+ sheet.path);
		this.width = width;
		this.height = height;
		this.sheet = sheet;
		pixels = new int[width * height];
	}

	/**
	 * Sets the offset of the camera.
	 *
	 * @param xOffset
	 *            Offset for X
	 * @param yOffset
	 *            Offset for Y
	 */
	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * A shortcut for our render tile.
	 *
	 * @param xPos
	 *            X position
	 * @param yPos
	 *            Y position
	 * @param tile
	 *            Sprite to render from the sprite sheet.
	 * @param color
	 *            Color of the sprite.
	 */
	public void render(int xPos, int yPos, int tile, int color) {
		render(xPos, yPos, tile, color, false, false, 1);
	}

	/**
	 * A shortcut for our render tile.
	 *
	 * @param xPos
	 *            X position
	 * @param yPos
	 *            Y position
	 * @param tile
	 *            Sprite to render from the sprite sheet.
	 * @param color
	 *            Color of the sprite.
	 * @param scale
	 *            Size of the sprite (0.0 - 1.0 double, int larger than 1)
	 */
	public void render(int xPos, int yPos, int tile, int color, int scale) {
		render(xPos, yPos, tile, color, false, false, scale);
	}

	/**
	 * Full method for rendering a sprite to the screen! Wow, this method got
	 * complicated fast.
	 *
	 * @param xPos
	 *            X position to render
	 * @param yPos
	 *            Y position to render
	 * @param tile
	 *            The tile to render
	 * @param color
	 *            the color to render ( generally made by the Colors class)
	 * @param mirrorX
	 *            Mirror x?
	 * @param mirrorY
	 *            Mirror y?
	 * @param scale
	 *            Scales the model. Double 0.0-1.0 and "int like" double for
	 *            anything higher
	 */
	public void render(int xPos, int yPos, int tile, int color,
			boolean mirrorX, boolean mirrorY, double scale) {
		double strecher = 0.0;
		boolean small = false;

		if (scale >= 1.0) {
			scale = (int) scale;
		} else {
			strecher = scale;
			scale = 1;
			small = true;
		}
		// Sets where we currently are
		xPos -= xOffset;
		yPos -= yOffset;
		final int scaleMap = (int) scale - 1;

		final int xTile = tile % 16; // We have 16 rows
		final int yTile = tile / 16; // And 16 cols.

		// Shift 5 because the tiles are 32
		final int tileOffset = (xTile << 5) + ((yTile << 5) * sheet.width);
		// Begin rendering.
		for (int y = 0; y < 32; y++) {
			int ySheet = y;
			if (mirrorY) { // Do we need to mirror?
				ySheet = 31 - y;
			}

			final int yPixel = (y + yPos + (y * scaleMap))
					- ((scaleMap << 5) / 2);

			for (int x = 0; x < 32; x++) {
				int xSheet = x;
				if (mirrorX) { // Do we need to mirror?
					xSheet = 31 - x;
				}

				final int xPixel = (x + xPos + (x * scaleMap))
						- ((scaleMap << 5) / 2);

				final int col = (color >> (sheet.pixels[xSheet
				                                        + (ySheet * sheet.width) + tileOffset] * 8)) & 255; // Colorize
				// pixels
				if (col < 255) {
					for (int yScale = 0; yScale < scale; yScale++) {
						if (((yPixel + yScale) < 0)
								|| ((yPixel + yScale) >= height)) {
							continue; // Continue if not out of bounds.
						}
						for (int xScale = 0; xScale < scale; xScale++) {
							if (((xPixel + xScale) < 0)
									|| ((xPixel + xScale) >= width)) {
								continue; // Continue if not out of bounds.
							}
							if (!small) {
								pixels[(xPixel + xScale)
								       + ((yPixel + yScale) * width)] = col; // Set
								// colors.
							} else {
								final int temp = (xPixel - (int) (xSheet * (1 - strecher)))
										+ ((yPixel - (int) (ySheet * (1 - strecher))) * width);
								if ((temp >= 0)
										&& ((xPixel - (int) (xSheet * (1 - strecher))) >= 0)) {
									pixels[temp] = col; // Set colors.
								} // Favorite lines of code from all time
							}
						}
					}
				}
			}
		}
	}
}
