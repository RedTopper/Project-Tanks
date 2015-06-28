package game.entities;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;
import game.net.packets.Packet05Damage;

/**
 * A bullet is an {@link Entity} that extends {@link Mob}. It is fired from
 * player's tanks and damages players that are on the opposite team.
 *
 * @author AJ Walter
 */
public class Bullet extends Mob {

	private int direction = 0;
	private boolean immune;

	/**
	 * Used to create a bullet in a level
	 *
	 * @param level
	 *            {@link Level} to create a bullet in
	 * @param id
	 *            Id of the bullet
	 * @param x
	 *            X value of a bullet
	 * @param y
	 *            Y value of a bullet
	 * @param direction
	 *            Direction of the bullet
	 * @param immune
	 *            Is the bullet immune to the player?
	 */
	public Bullet(Level level, int id, int x, int y, int direction,
			boolean immune) {
		super(level, id, "Bullet", x, y, 6);
		this.x = x;
		this.y = y - 4;
		this.direction = direction;
		this.immune = immune;
	}

	@Override
	public void tick() {
		int xa = 0;
		int ya = 0;
		if (direction == 0) {
			ya--;
		}
		if (direction == 1) {
			ya++;
		}
		if (direction == 2) {
			xa--;
		}
		if (direction == 3) {
			xa++;
		}
		if ((xa != 0) || (ya != 0)) {
			move(xa, ya);
			isMoving = true;
		} else {
			isMoving = false;
		}

		for (int i = 0; i < level.getEntities().size(); i++) {
			final Entity e = level.getEntities().get(i);
			if (e instanceof Player) {
				final Player p = (Player) e;
				if (p.isLocal() && !isImmune()) {
					if ((x >= (p.x - 15)) && (x < (p.x + 47))
							&& (y >= (p.y - 15)) && (y < (p.y + 47))) {
						final Packet05Damage packet = new Packet05Damage(
								p.getUsername(), 4);
						packet.writeData(level.getGame().socketClient);
						markAsImmune();
					}
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {
		screen.render(x - 16, y - 16, 15, Colors.get(-1, 000, 333, 555));
		super.debugRender(screen);
	}

	@Override
	public boolean hasCollided(int xa, int ya) {
		final int xMin = 0;
		final int xMax = 4;
		final int yMin = 0;
		final int yMax = 4;
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				markForDelete = true;
				return true;
			}
		}
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				markForDelete = true;
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				markForDelete = true;
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				markForDelete = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public void move(int xa, int ya) {
		if ((xa != 0) && (ya != 0)) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		if (!hasCollided(xa, ya)) {
			x += xa * speed;
			y += ya * speed;
		}
	}

	/**
	 * Marks this bullet as immune to the player.
	 */
	public void markAsImmune() {
		immune = true;
	}

	/**
	 * Checks if the bullet is immune to the player
	 *
	 * @return <code> true </code> if the player is immune to the {@link Bullet}
	 *         , <code> false </code> otherwise.
	 */
	public boolean isImmune() {
		return immune;
	}
}
