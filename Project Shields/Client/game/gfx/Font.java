package game.gfx;

/**
 * Renders a font.
 *
 * @author AJ Walter
 */
public class Font {

	/**
	 * list of the characters on the character sheet.
	 */
	public static final String CHARS = "0123456789.,:;'\"!?$%()-=+/<>    ABCDEFGHIJKLMNOPQRSTUVWXYZ|";

	/**
	 * Renders the string
	 *
	 * @param msg
	 *            Message to render
	 * @param screen
	 *            Where to render.
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @param color
	 *            The color of the font.
	 */
	public static void render(String msg, Screen screen, int x, int y, int color) {
		msg = msg.toUpperCase();

		for (int i = 0; i < msg.length(); i++) {
			final int charIndex = CHARS.indexOf(msg.charAt(i));
			if (charIndex >= 0) {
				screen.render(x + (i * 22), y, charIndex + (12 * 16), color);
			}
		}
	}

	/**
	 * Extra render method that allows the user to stretch or compress a font.
	 *
	 * @param msg
	 *            Message to render
	 * @param screen
	 *            Where to render.
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @param color
	 *            The color of the font.
	 * @param scale
	 *            The size (double 0.0-1.0, and int larger than 1) of the font.
	 */
	public static void render(String msg, Screen screen, int x, int y,
			int color, double scale) {
		msg = msg.toUpperCase();

		double strecher = 0.0;
		double hstrecher = 0.0;

		if (scale >= 1.0) {
			scale = (int) scale;
			strecher = (int) scale;
			hstrecher = 1;
		} else {
			strecher = scale;
			hstrecher = scale;
			scale = 1;
		}

		for (int i = 0; i < msg.length(); i++) {
			final int charIndex = CHARS.indexOf(msg.charAt(i));
			if (charIndex >= 0) {
				screen.render(
						(int) (x + ((i * 22 * hstrecher) * scale) + ((scale - 1) * 16)),
						(int) (y + ((scale - 1) * 16)), charIndex + (12 * 16),
						color, false, false, strecher);
			}
		}
	}
}
