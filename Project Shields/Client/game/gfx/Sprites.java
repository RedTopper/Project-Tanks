package game.gfx;

import game.utils.Debug;
import game.utils.Type;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * Class that contains the information of a sprite sheet.
 *
 * @author AJ Walter
 */
public class Sprites {

	/**
	 * Name of the class.
	 */
	public static final String CLASS = "SpriteSheet";

	/**
	 * The local path of the sprite sheet within the JAR file.
	 */
	public String path;

	/**
	 * Dimensions of the sprite sheet.
	 */
	public int width, height;

	/**
	 * One dimensional array containing all of the pixel data from the sprite
	 * sheet.
	 */
	public int[] pixels;

	/**
	 * Creates a {@link Sprites} object.
	 *
	 * @param path
	 *            {@link String} location of the file in the JAR.
	 */
	public Sprites(String path) {
		BufferedImage image = null;

		// try to load image (not found throws an exception).
		try {
			image = ImageIO.read(Sprites.class.getResourceAsStream(path));
			Debug.out(Type.DEBUG, CLASS, "Sprite map " + path + " loaded.");
		} catch (final Exception e) {
			e.printStackTrace();
			Debug.out(Type.SEVERE, CLASS, "Failed to load " + path + "!");
		}

		if (image == null) {
			return;
		}

		// sets up path and vars.
		this.path = path;
		width = image.getWidth();
		height = image.getHeight();

		// makes the RGB image contain an RGB image
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

		// inserts a 0, 1, 2 or 3 into the spritesheet
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (pixels[i] & 0xff) / 64;
		}
	}
}
