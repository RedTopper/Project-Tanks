package game.entities;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;
import game.level.tiles.Tile;

/**
 * A Mob is an {@link Entity} that contains a position and the ability to move.
 * A Mob could be a player or a flag.
 *
 * @author AJ Walter
 */
public abstract class Mob extends Entity {

	/**
	 * Name of the contained {@link Mob}.
	 */
	protected String name;

	/**
	 * The speed of the {@link Mob}.
	 */
	protected int speed;

	/**
	 * Boolean <code> true </code> if the {@link Mob} is moving, false
	 * otherwise.
	 */
	public boolean isMoving;

	/**
	 * The direction that this {@link Mob} is facing.
	 * <p>
	 * <b>0 = Up</b> - The mob is in the first position and is facing upwards on
	 * the sprite map.
	 * </p>
	 * <p>
	 * <b>1 = Down</b> - The mob is in the second position and is facing
	 * downwards on the sprite map.
	 * </p>
	 * <p>
	 * <b>2 = Left</b> - The mob is in the third position and is facing to the
	 * left on the sprite map.
	 * </p>
	 * <p>
	 * <b>3 = Right</b> - The mob is in the last position and is facing to the
	 * right on the sprite map.
	 * </p>
	 */
	protected int movingDir = 1;

	/**
	 * Size of the {@link Mob}.
	 */
	protected int scale = 1;

	/**
	 * Creates a new mob
	 *
	 * @param level
	 *            Level to create the mob
	 * @param id
	 *            ID of the mob
	 * @param name
	 *            Name of the mob (Should be set in child classes in
	 *            constructors)
	 * @param x
	 *            X cord
	 * @param y
	 *            Y cord
	 * @param speed
	 *            Speed of the {@link Mob}.
	 */
	public Mob(Level level, int id, String name, int x, int y, int speed) {
		super(level, id);
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.name = name;
	}

	/**
	 * Moves the entity in a direction relative to its previous direction. Also
	 * checks to see if the mob can move into the tile.
	 *
	 * @param xa
	 *            1, 0, or -1. The direction the mob will move on the X plain.
	 * @param ya
	 *            1, 0, or -1. The direction the mob will move on the Y plain.
	 */
	public void move(int xa, int ya) {
		if ((xa != 0) && (ya != 0)) {
			move(xa, 0); // Check X first
			move(0, ya); // Check Y second.
			return;
		}
		if (!hasCollided(xa, ya)) {
			if (ya < 0) {
				movingDir = 0;
			}
			if (ya > 0) {
				movingDir = 1;
			}
			if (xa < 0) {
				movingDir = 2;
			}
			if (xa > 0) {
				movingDir = 3;
			}
			x += xa * speed;
			y += ya * speed;
		}
	}

	/**
	 * Adds a red dot in the X and Y position of this {@link Mob}.
	 *
	 * @param screen
	 *            {@link Screen} to render on.
	 */
	public void debugRender(Screen screen) {
		screen.render(x, y, 0, Colors.get(500, 500, 500, 500), false, false,
				0.01);
	}

	/**
	 * Checks to see if the {@link Mob} has collided with a solid tile.
	 *
	 * @param xa
	 *            1, 0, or -1 direction X.
	 * @param ya
	 *            1, 0, or -1 direction Y.
	 * @return <code> True </code> if the {@link Mob} has collided with a tile,
	 *         <code> False </code> otherwise.
	 */
	public abstract boolean hasCollided(int xa, int ya);

	/**
	 * Checks to see if the boundary of a {@link Mob} has a solid tile below it.
	 *
	 * @param xa
	 *            1, 0, or -1 direction X.
	 * @param ya
	 *            1, 0, or -1 direction Y.
	 * @param x
	 *            X cord to be checking on. If the difference between this cord
	 *            and this chord + <code> xa</code> is solid, it returns true.
	 * @param y
	 *            Y cord to be checking on. If the difference between this cord
	 *            and this chord + <code> ya</code> is solid, it returns true.
	 * @return Returns <code> True</code> if the next tile is solid,
	 *         <code> False</code> otherwise.
	 */
	protected boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null) {
			return false;
		}
		final Tile lastTile = level.getTile((this.x + x) >> 5,
				(this.y + y) >> 5);
		final Tile newTile = level.getTile((this.x + x + xa) >> 5,
				(this.y + y + ya) >> 5);
		if (!lastTile.equals(newTile) && newTile.isSolid()) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the name of this {@link Mob}.
	 *
	 * @return A {@link String} representation of this {@link Mob}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets if the {@link Mob} is moving or not.
	 *
	 * @param isMoving
	 *            <code>True</code> if the mob is moving.
	 */
	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	/**
	 * Gets the current moving direction of this {@link Mob}.
	 *
	 * @return int Moving Direction.
	 * @see Mob#movingDir
	 */
	public int getMovingDir() {
		return movingDir;
	}

	/**
	 * Sets the current moving direction.
	 *
	 * @param movingDir
	 *            int Moving Direction.
	 * @see Mob#movingDir
	 */
	public void setMovingDir(int movingDir) {
		this.movingDir = movingDir;
	}
}
