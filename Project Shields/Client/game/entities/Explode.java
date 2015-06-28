/**
 *
 */
package game.entities;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;

/**
 * An {@link Explode} is a short animation that is played when a {@link Player}
 * takes damage.
 *
 * @author AJ Walter
 */
public class Explode extends Mob {

	/**
	 * Ticks until the animation goes to the next frame.
	 */
	public static final int TICKS_UNTIL_CHANGE = 3;

	/**
	 * The frame number that the {@link Explode} object is on.
	 */
	int onFrame = 0;

	private int ticksUntilChange = 0;

	/**
	 * Creates a new explosion animation.
	 *
	 * @param level
	 *            {@link Level} the {@link Explode} is created in.
	 * @param id
	 *            ID of the {@link Explode}
	 * @param x
	 *            X cord
	 * @param y
	 *            Y cord
	 */
	public Explode(Level level, int id, int x, int y) {
		super(level, id, "Explode", x, y, 0);
		this.x = x;
		this.y = y - 4;
	}

	@Override
	public boolean hasCollided(int xa, int ya) {
		return false;
	}

	@Override
	public void tick() {
		ticksUntilChange++;
		if (ticksUntilChange > TICKS_UNTIL_CHANGE) {
			onFrame++;
			ticksUntilChange = 0;
		}
		if (onFrame == 8) {
			markForDelete();
		}
	}

	@Override
	public void render(Screen screen) {
		final int xTile = 8;
		final int yTile = 11;
		screen.render(x, y, (xTile + (yTile * 16)) + onFrame,
				Colors.get(-1, 000, 555, 555), false, false, 2);
	}

}
