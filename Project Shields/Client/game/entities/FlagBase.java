package game.entities;

import game.gfx.Colors;
import game.gfx.Screen;
import game.level.Level;
import game.net.packets.Packet02Move;
import game.net.packets.Packet04Chat;
import game.net.packets.Packet13Score;

/**
 * A {@link FlagBase} is used in the game mode Capture The Flag. It contains the
 * colored flag for a team. The flag may be taken from the base and added to the
 * player, or returned to the {@link FlagBase}. There are (usually) only two
 * {@link FlagBase}s that exist in a game of Capture The Flag.
 *
 * @author AJ Walter
 *
 */
public class FlagBase extends Mob {

	/**
	 * Boolean variable that states if the flag is in its post or not.
	 */
	private boolean flagIsPresent = true;

	/**
	 * The team the {@link FlagBase} is on.
	 */
	private final String team;
	private int color;

	/**
	 * Creates a {@link FlagBase}
	 *
	 * @param level
	 *            {@link Level} the {@link FlagBase} is in.
	 * @param id
	 *            ID of the {@link FlagBase}
	 * @param x
	 *            X cord
	 * @param y
	 *            Y cord
	 * @param team
	 *            {@link String} "RED" or "GREEN".
	 */
	public FlagBase(Level level, int id, int x, int y, String team) {
		super(level, id, "Flag", x, y, 0);
		this.team = team;
		if (team.equals("GREEN")) {
			color = Colors.get(-1, 023, 121, 000);
		} else {
			color = Colors.get(-1, 023, 211, 000);
		}
	}

	@Override
	public boolean hasCollided(int xa, int ya) {
		return false;
	}

	@Override
	public void tick() {
		for (int i = 0; i < level.getEntities().size(); i++) {
			final Entity e = level.getEntities().get(i);
			if (e instanceof Player) {
				final Player p = (Player) e;
				if (p.isLocal()) {
					if (!team.equals(p.getTeam())) {
						if (flagIsPresent) {
							if ((x >= (p.x - 31)) && (x < (p.x + 31))
									&& (y >= (p.y - 31)) && (y < (p.y + 31))) {
								flagIsPresent = false;
								p.setFlag(true);
								final Packet02Move packet = new Packet02Move(
										p.getUsername(), p.x, p.y, p.isMoving,
										p.getMovingDir(), p.getFlag()); // Just
								// to
								// sync
								// getting
								// the flag a little
								// more.
								packet.writeData(level.getGame().socketClient);
							}
						}
					} else {
						if (p.getFlag() && (x >= (p.x - 31))
								&& (x < (p.x + 31)) && (y >= (p.y - 31))
								&& (y < (p.y + 31))) {
							p.setFlag(false);
							final Packet04Chat msg = new Packet04Chat(
									p.getUsername(), 534, ": Team " + team
									+ " captured the flag!!");
							msg.writeData(level.getGame().socketClient);
							final Packet13Score score = new Packet13Score(p.getUsername(), p.getTeam(), 1);
							score.writeData(level.getGame().socketClient);
						}
					}
				}
			}
		}
		if (team.equals("RED")) {
			boolean someoneHasFlag = false;
			for (int j = 0; j < level.getEntities().size(); j++) {
				final Entity othere = level.getEntities().get(j);
				if (othere instanceof Player) {
					final Player otherp = (Player) othere;
					if (otherp.getTeam().equals("GREEN")) {
						if (otherp.getFlag() == true) {
							someoneHasFlag = true;
							flagIsPresent = false;
							break;
						}
					}
				}
			}
			if (!someoneHasFlag) {
				flagIsPresent = true;
			}
		}
		if (team.equals("GREEN")) {
			boolean someoneHasFlag = false;
			for (int j = 0; j < level.getEntities().size(); j++) {
				final Entity othere = level.getEntities().get(j);
				if (othere instanceof Player) {
					final Player otherp = (Player) othere;
					if (otherp.getTeam().equals("RED")) {
						if (otherp.getFlag() == true) {
							someoneHasFlag = true;
							flagIsPresent = false;
							break;
						}
					}
				}
			}
			if (!someoneHasFlag) {
				flagIsPresent = true;
			}
		}
	}

	@Override
	public void render(Screen screen) {
		if (flagIsPresent) {
			final int xTile = 0;
			final int yTile = 8;
			screen.render(x - 32, y - 32, xTile + (yTile * 16), color, scale);
			screen.render(x, y - 32, (xTile + 1) + (yTile * 16), color, scale);
			screen.render(x - 32, y, xTile + ((yTile + 1) * 16), color, scale);
			screen.render(x, y, (xTile + 1) + ((yTile + 1) * 16), color, scale);
		} else {
			final int xTile = 2;
			final int yTile = 8;
			screen.render(x - 32, y, xTile + (yTile * 16), color, scale);
			screen.render(x, y, (xTile + 1) + (yTile * 16), color, scale);
		}
		// super.debugRender(screen);
	}

	/**
	 * Gets a {@link String} of the team this {@link FlagBase} is on.
	 *
	 * @return the {@link String} representation of team the flag is on.
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * Sets if the {@link FlagBase} has a flag or not.
	 *
	 * @param flag
	 *            boolean flag.
	 */
	public void setFlag(boolean flag) {
		flagIsPresent = flag;
	}

	/**
	 * Gets if the flag is in {@link FlagBase}.
	 *
	 * @return <code> true </code> if the {@link FlagBase} has a flag,
	 *         <code> false </code> otherwise.
	 */
	public boolean getFlag() {
		return flagIsPresent;
	}
}
