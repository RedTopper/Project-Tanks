package game.entities;

import game.gfx.Screen;
import game.level.Level;
import game.level.tiles.Tile;
import game.utils.Debug;
import game.utils.Type;

/**
 * An entity is a movable object within the game. An entity can range from a
 * player to a bullet to a flag. This is not to be confused with the
 * {@link Tile} class. All entities have an ID and a position they are currently
 * at.
 *
 * @author AJ Walter
 *
 */
public abstract class Entity {

	/**
	 * ID of this {@link Entity}. WARNING! The ID cannot be -1 (default value).
	 * It must be changed during runtime. All IDs (excluding game objects, which
	 * are -2) MUST be unique.
	 */
	protected int id = -1;

	/**
	 * Position of the {@link Entity}.
	 */
	public int x, y;

	/**
	 * The level which the {@link Entity} exists in.
	 */
	protected Level level;

	/**
	 * An {@link Entity} that is marked for delete will be garbage collected by
	 * the users client. This makes sure that the entities array in the
	 * {@link Level} class is not read from and written to in the same instance.
	 */
	protected boolean markForDelete = false;

	/**
	 * Name of the class.
	 */
	public static final String CLASS = "Entity";

	/**
	 * Creates a new {@link Entity}.
	 *
	 * @param level
	 *            {@link Level} to create the entity in.
	 * @param id
	 *            ID of the entity.
	 */
	public Entity(Level level, int id) {
		if (id == -1) {
			Debug.out(Type.SEVERE, CLASS, "That ID is wrong!");
		}
		this.id = id;
		init(level);
	}

	/**
	 * Initializes the entity to the level.
	 *
	 * @param level
	 *            {@link Level} to create the entity in.
	 */
	public final void init(Level level) {
		this.level = level;
	}

	/**
	 * Updates an {@link Entity}.
	 */
	public abstract void tick();

	/**
	 * Renders an {@link Entity}.
	 *
	 * @param screen
	 *            {@link Screen} the {@link Entity} is on.
	 */
	public abstract void render(Screen screen);

	/**
	 * Checks to see if an {@link Entity} is {@link Entity#markForDelete}.
	 *
	 * @return <code> true </code> if the {@link Entity} is marked for deletion,
	 *         <code> false </code> otherwise.
	 */
	public boolean isMarkedForDelete() {
		return markForDelete;
	}

	/**
	 * Makes an {@link Entity} {@link Entity#markForDelete}.
	 */
	public void markForDelete() {
		markForDelete = true;
	}

	/**
	 * Gets the ID of an entity.
	 *
	 * @return the ID of this entity.
	 */
	public int getID() {
		return id;
	}
}
