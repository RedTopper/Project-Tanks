package game.entities;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;
import game.net.packets.Packet05Damage;
import game.net.packets.Packet10Destroy;

/**
 * A {@link Landmine} is an {@link Entity} dropped by players that sits on the
 * ground until collided with. They do a lot of damage, however, a player should
 * only be able to place down three at any given moment.
 *
 * @author AJ Walter
 */
public class Landmine extends Mob {

	private boolean immune;
	private final String thisTeam;
	private final String username;

	/**
	 * Used to create a {@link Landmine} in a level.
	 *
	 * @param level
	 *            {@link Level} to create a {@link Landmine} in.
	 * @param id
	 *            ID of the {@link Landmine}.
	 * @param username
	 *            Username of the owner of the {@link Landmine}.
	 * @param x
	 *            X chord
	 * @param y
	 *            Y chord
	 * @param immune
	 *            Is the {@link Landmine} immune to the player?
	 * @param thisTeam
	 *            team of {@link Landmine}.
	 */
	public Landmine(Level level, int id, String username, int x, int y,
			boolean immune, String thisTeam) {
		super(level, id, "Landmine", x, y, 0);
		this.x = x;
		this.y = y - 4;
		this.immune = immune;
		this.thisTeam = thisTeam;
		this.username = username;
	}

	@Override
	public void tick() {
		for (int i = 0; i < level.getEntities().size(); i++) {
			final Entity e = level.getEntities().get(i);
			if (e instanceof Player) {
				final Player p = (Player) e;
				if (p.isLocal() && !isImmune()) {
					if ((x >= (p.x - 31)) && (x < (p.x + 31))
							&& (y >= (p.y - 31)) && (y < (p.y + 31))) {
						final Packet05Damage packet = new Packet05Damage(
								p.getUsername(), 8);
						packet.writeData(level.getGame().socketClient);
						final Packet10Destroy destroy = new Packet10Destroy(
								p.getUsername(), getID());
						destroy.writeData(level.getGame().socketClient);
						markAsImmune();
					}
				}
			}
		}
	}

	@Override
	public void render(Screen screen) {
		screen.render(x, y, 17, Colors.get(-1, 100, 200,
				(thisTeam.equals("GREEN") == true ? 121 : 211)));
		super.debugRender(screen);
	}

	/**
	 * Unused, as Landmines do not move.
	 */
	@Override
	public boolean hasCollided(int xa, int ya) {
		return false;
	}

	/**
	 * Unused, as Landmines do not move.
	 */
	@Override
	public void move(int xa, int ya) {

	}

	/**
	 * Marks this {@link Landmine} as immune to the player.
	 */
	public void markAsImmune() {
		immune = true;
	}

	/**
	 * Checks if the bullet is immune to the player
	 *
	 * @return <code> true </code> if the player is immune to the
	 *         {@link Landmine}, <code> false </code> otherwise.
	 */
	public boolean isImmune() {
		return immune;
	}

	/**
	 * Gets the username of the player that placed the mine.
	 *
	 * @return A {@link String} representation of the Username.
	 */
	public String getUsername() {
		return username;
	}
}
