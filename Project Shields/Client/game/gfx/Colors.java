package game.gfx;

/**
 * This class takes values of 4 color integers and converts them into a single
 * integer.
 *
 * @author AJ
 *
 */
public class Colors {

	/**
	 * Returns a long number for the colors.
	 *
	 * @param color1
	 *            Darkest color
	 * @param color2
	 *            Dark grey
	 * @param color3
	 *            Light grey
	 * @param color4
	 *            White
	 * @return the color int.
	 */
	public static int get(int color1, int color2, int color3, int color4) {
		return (get(color4) << 24) + (get(color3) << 16) + (get(color2) << 8)
				+ (get(color1));
	}

	/**
	 * Calculate the color from the other method
	 *
	 * @param color
	 *            Color int (Usually something like 505 or 555)
	 * @return a color.
	 */
	private static int get(int color) {
		if (color < 0) {
			return 255;
		}
		final int r = (color / 100) % 10;
		final int g = (color / 10) % 10;
		final int b = color % 10;
		return (r * 36) + (g * 6) + b;
	}
}
