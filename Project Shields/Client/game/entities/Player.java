package game.entities;

import game.Game;
import game.InputHandler;
import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;
import game.level.Level;
import game.net.packets.Packet02Move;
import game.net.packets.Packet06Bullet;
import game.net.packets.Packet07Mine;
import game.utils.Debug;
import game.utils.Type;

/**
 * A {@link Player} is an Object that is either controlled by a Keyboard or by
 * the network. A user controls the player.
 *
 * @author AJ Walter
 */
public class Player extends Mob {

	/**
	 * The amount of ticks it takes to update a players position over the
	 * network. Lower is always better, but sends more packets.
	 */
	public static final int UPDATE_RATE = 7; // Lower=better

	/**
	 * The amount of frames it takes to move to the next move animation. It's
	 * the bouncy one.
	 */
	public static final int FRAME_RATE_OF_MOVE = 8;

	/**
	 * The amount of ticks it takes to loose energy.
	 */
	public static final int STARTING_ENERGY_LOSS_RATE = 8;

	/**
	 * The amount of ticks it takes to gain new energy.
	 */
	public static final int STARTING_ENERGY_CHARGE_RATE = STARTING_ENERGY_LOSS_RATE * 2;

	/**
	 * The amount of ticks it takes to begin acquiring new energy.
	 */
	public static final int STARTING_ENERGY_STALL = 60;

	/**
	 * How much energy it takes to fire a bullet.
	 */
	public static final int STARTING_FIRE_ENERGY = 4;

	/**
	 * The amount of ticks it takes to shoot another bullet.
	 */
	public static final int STARTING_FIRE_STALL = 30;

	/**
	 * How much energy it takes to place a mine.
	 */
	public static final int STARTING_MINE_ENERGY = 10;

	/**
	 * The amount of ticks it takes to place a new landmine.
	 */
	public static final int STARTING_MINE_STALL = 180;

	/**
	 * The amount of health a player starts with.
	 */
	public static final int STARTING_HEALTH = 20;

	/**
	 * The amount of ticks it takes to generate one new heart.
	 */
	public static final int STARTING_HEALTH_REGEN_RATE = 300;

	/**
	 * The amount of energy the player starts with.
	 */
	public static final int STARTING_ENERGY = 20; // player energy

	/**
	 * The last chord that the Player was rendered. These are updated every
	 * frame until {@link Player#UPDATE_RATE} has been reached. This "smoothes"
	 * the movement over the server, so the players appear to move without
	 * shaking or sending an excessive amount of packets.
	 */
	private double lastX = 0, lastY = 0;

	/**
	 * The position before the most current position the player is. The Player
	 * is moved from this position to {@link Entity#x} and {@link Entity#y}
	 * using {@link Player#lastX} and {@link Player#lastY}
	 */
	private int lastTweenX = 0, lastTweenY = 0;

	/**
	 * The frame the tween is currently on.
	 */
	private int onTweenFrame = 0;

	/**
	 * The frame the move animation is on (Up or down).
	 */
	private int onMoveFrame = 0;

	/**
	 * Team the player is on.
	 */
	private final String team;

	/**
	 * Username of this player.
	 */
	private final String username;

	/**
	 * If the player is local, there is an input handler attached to it.
	 */
	private final InputHandler handler;

	/**
	 * Amount of ticks passed since the player was created.
	 */
	private int ticks = 0;

	/**
	 * Current color of the body of the player.
	 */
	private int color;

	/**
	 * True if the player is local to the computer, false if the player is
	 * remote.
	 */
	protected boolean isLocal = false;

	/**
	 * True if the player is holding a flag.
	 */
	private boolean flag = false;

	/**
	 * Bounce animation when the player is moving.
	 */
	private int stepOffsetY = 0, stepOffsetX = 0;

	/**
	 * Sets the animation to be in the "down" position when the character is
	 * moving.
	 */
	private boolean isDown = false;

	/**
	 * TODO: ?
	 */
	private int damageHit = 0;

	/**
	 * When the player stops moving, this variable is set to true. Then, after
	 * the time it takes to send a packet, a new packet is sent. Packets are not
	 * sent after this packet until the player moves again.
	 */
	private boolean canSendLastPacket = false;

	/**
	 * See the respective static value in the {@link Player} class.
	 */
	private int health = STARTING_HEALTH,
			healthRegen = STARTING_HEALTH_REGEN_RATE, energy = STARTING_ENERGY,
			energyLossRate = STARTING_ENERGY_LOSS_RATE,
			energyStall = STARTING_ENERGY_STALL,
			fireStall = STARTING_FIRE_STALL, mineStall = STARTING_MINE_STALL;

	/**
	 * True if the player is moving fast, false otherwise.
	 */
	private boolean speedy = false;

	/**
	 * True if the player can regenerate a bit of health.
	 */
	private boolean canRegenHealth = false;

	/**
	 * True if the player recently set a mine.
	 */
	private boolean mined = false;

	/**
	 * True if the player recently shot a bullet.
	 */
	private boolean fired = false;
	
	/**
	 * Score of this player.
	 */
	private int score = 0;

	/**
	 * Creates a new normal player
	 *
	 * @param level
	 *            Level to create the player
	 * @param id
	 *            ID of the player
	 * @param x
	 *            X of the player
	 * @param y
	 *            Y of the player
	 * @param handler
	 *            InputHandler for control
	 * @param username
	 *            Username of the player
	 * @param team
	 *            Team the player is on
	 */
	public Player(Level level, int id, int x, int y, InputHandler handler,
			String username, String team) {
		super(level, id, "Player", x, y, 2);
		this.handler = handler;
		this.username = username;
		this.team = team;
		if (team.equals("GREEN")) {
			color = Colors.get(-1, 121, 050, 000);
		} else {
			color = Colors.get(-1, 211, 050, 000);
		}
	}

	@Override
	public void tick() {
		ticks++;
		if (ticks > 10000) {
			ticks = 0; // Makes sure that the ticks does not overflow.
		}

		int xa = 0;
		int ya = 0;
		if ((handler != null) && isAlive()) {
			if (handler.up.isPressed()) {
				ya--;
			}
			if (handler.down.isPressed()) {
				ya++;
			}
			if (handler.left.isPressed()) {
				xa--;
			}
			if (handler.right.isPressed()) {
				xa++;
			}
			if (handler.mod.isPressed()) {
				energyStall = STARTING_ENERGY_STALL;
				energyLossRate--;
				if (energy > 0) {
					speedy = true;
				} else {
					speedy = false;
				}
				if (energyLossRate < 1) {
					if (energy > 0) {
						energy--;
						energyLossRate = STARTING_ENERGY_LOSS_RATE;
					}
				}
			} else {
				speedy = false;
			}
			if (handler.fire.isPressed() && !fired
					&& (energy >= STARTING_FIRE_ENERGY) && isAlive()) {
				fired = true;
				fireStall = STARTING_FIRE_STALL;
				energyStall = STARTING_ENERGY_STALL;
				energy -= STARTING_FIRE_ENERGY;
				final Packet06Bullet packet = new Packet06Bullet(username, -1,
						x + 16, y + 16, getMovingDir(), team);
				packet.writeData(level.getGame().socketClient);
			}
			if (handler.mine.isPressed() && !mined
					&& (energy >= STARTING_MINE_ENERGY) && isAlive()) {
				mined = true;
				mineStall = STARTING_MINE_STALL;
				energyStall = STARTING_ENERGY_STALL;
				energy -= Player.STARTING_MINE_ENERGY;
				Debug.out(Type.DEBUG, CLASS, "Mine set on team " + team);
				final Packet07Mine packet = new Packet07Mine(username, -1, x,
						y, team);
				packet.writeData(level.getGame().socketClient);
			}
		}

		if (isLocal) {
			lastX = x; // Makes sure that if we are local, we don't smooth.
			lastY = y;
			if (speedy) {
				if (!flag) {
					speed = 4;
				} else {
					speed = 2;
				}
			} else {
				if (!flag) {
					speed = 2;
					if ((x % 2) == 1) {
						x += 1; // Makes sure that the movement isn't off by
						// one.
					}
					if ((y % 2) == 1) {
						y += 1;
					}
				} else {
					speed = 1;
				}
			}
			if (!speedy && (energy < STARTING_ENERGY)) {
				energyStall--;
				if ((energyStall < 1) && (energy < health)) {
					energy++;
					energyStall = STARTING_ENERGY_CHARGE_RATE;
				}
			}
			if ((health < STARTING_HEALTH) && (health != 0)) {
				if (healthRegen == STARTING_HEALTH_REGEN_RATE) {
					canRegenHealth = true;
					healthRegen = 0;
				}
			}
			if (healthRegen < STARTING_HEALTH_REGEN_RATE) {
				healthRegen++;
			}
			if (fired) {
				fireStall--;
				if (fireStall < 1) {
					fired = false;
				}
			}
			if (mined) {
				mineStall--;
				if (mineStall < 1) {
					mined = false;
				}
			}
		} else {
			if (onTweenFrame < UPDATE_RATE) {
				lastX += (((double) x - (double) lastTweenX) / (double) UPDATE_RATE);
				lastY += (((double) y - (double) lastTweenY) / (double) UPDATE_RATE);
				onTweenFrame++;
			} else {
				lastX = x;
				lastY = y;
			}
		}

		if ((ticks % FRAME_RATE_OF_MOVE) == 0) {
			onMoveFrame++;
			if (onMoveFrame > 2) {
				onMoveFrame = 0;
			}
		}

		if (isMoving) {
			if ((ticks % UPDATE_RATE) == 0) {
				if (isDown) {
					stepOffsetY = 1;
					stepOffsetX = 1;
					isDown = !isDown;
				} else {
					stepOffsetY = 0;
					stepOffsetX = 0;
					isDown = !isDown;
				}
			}
		}

		if ((xa != 0) || (ya != 0)) {
			move(xa, ya);
			isMoving = true;
			if (isLocal) {
				canSendLastPacket = true;
				if ((ticks % UPDATE_RATE) == 0) {
					final Packet02Move packet = new Packet02Move(getUsername(),
							x, y, isMoving, movingDir, flag);
					packet.writeData(Game.game.socketClient);
				}
			}
		} else {
			if (isLocal) {
				isMoving = false;
				if (canSendLastPacket) {
					if ((ticks % UPDATE_RATE) == 0) {
						final Packet02Move packet = new Packet02Move(
								getUsername(), x, y, isMoving, movingDir, flag);
						packet.writeData(Game.game.socketClient);
						canSendLastPacket = false;
					}
				}
			}
		}

		if (damageHit > 0) {
			damageHit--;
		}
	}

	@Override
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 10;
		if (movingDir == 0) {
			stepOffsetY = 0;
		} else if (movingDir == 1) {
			xTile += 2;
			stepOffsetY = 0;
		} else if (movingDir == 2) {
			xTile += 4;
			stepOffsetX = 0;
		} else {
			xTile += 6;
			stepOffsetX = 0;
		}
		final int modifier = 32 * scale;
		final int xOffset = (int) (lastX - (modifier / 2));
		final int yOffset = (int) (lastY - (modifier / 2) - 4);
		screen.render(xOffset + stepOffsetX, yOffset + stepOffsetY, xTile
				+ (yTile * 16), color, scale);
		screen.render(xOffset + modifier + stepOffsetX, yOffset + stepOffsetY,
				(xTile + 1) + (yTile * 16), color, scale);
		screen.render(xOffset + stepOffsetX, yOffset + modifier + stepOffsetY,
				xTile + ((yTile + 1) * 16), color, scale);
		screen.render(xOffset + modifier + stepOffsetX, yOffset + modifier
				+ stepOffsetY, (xTile + 1) + ((yTile + 1) * 16), color, scale);

		if (flag) {
			int flagcolor;
			if (team.equals("RED")) {
				flagcolor = Colors.get(-1, 023, 121, 000);
			} else {
				flagcolor = Colors.get(-1, 023, 211, 000);
			}
			xTile = 4 + (onMoveFrame * 2);
			yTile = 8;
			if (!(movingDir == 2)) {
				screen.render((xOffset + stepOffsetX) - 16,
						(yOffset + stepOffsetY) - 24, xTile + (yTile * 16),
						flagcolor, scale);
				screen.render((xOffset + modifier + stepOffsetX) - 16,
						(yOffset + stepOffsetY) - 24, (xTile + 1)
						+ (yTile * 16), flagcolor, scale);
				screen.render((xOffset + stepOffsetX) - 16,
						(yOffset + modifier + stepOffsetY) - 24, xTile
						+ ((yTile + 1) * 16), flagcolor, scale);
				screen.render((xOffset + modifier + stepOffsetX) - 16, (yOffset
						+ modifier + stepOffsetY) - 24, (xTile + 1)
						+ ((yTile + 1) * 16), flagcolor, scale);
			} else {
				screen.render(xOffset + stepOffsetX + 48,
						(yOffset + stepOffsetY) - 24, xTile + (yTile * 16),
						flagcolor, true, false, scale);
				screen.render((xOffset + modifier + stepOffsetX) - 16,
						(yOffset + stepOffsetY) - 24, (xTile + 1)
						+ (yTile * 16), flagcolor, true, false, scale);
				screen.render(xOffset + stepOffsetX + 48,
						(yOffset + modifier + stepOffsetY) - 24, xTile
						+ ((yTile + 1) * 16), flagcolor, true, false,
						scale);
				screen.render((xOffset + modifier + stepOffsetX) - 16, (yOffset
						+ modifier + stepOffsetY) - 24, (xTile + 1)
						+ ((yTile + 1) * 16), flagcolor, true, false, scale);
			}
		}

		if (username != null) {
			Font.render(username, screen,
					(xOffset - ((username.length() * (int) (22 * .5)) / 2))
					+ 32 + (movingDir == 2 ? 12 : 0)
					+ (movingDir == 3 ? -12 : 0), yOffset - 20,
					Colors.get(-1, -1, -1, (team.equals("GREEN") ? 050 : 500)),
					.5);
			if(level.getGame().currentRunningGamemode != null &&
					level.getGame().currentRunningGamemode.equals("CTF")) {
				Font.render(score + "", screen,
						(xOffset - (((score + "").length() * (int) (22 * .5)) / 2))
						+ 32 + (movingDir == 2 ? 12 : 0)
						+ (movingDir == 3 ? -12 : 0), yOffset - 44,
						Colors.get(-1, -1, -1, 555),
						.5);
			}
		}

		if (isLocal) {
			for (int i = 0; i < ((health / 2) + 1); i++) {
				if ((health % 2) == 0) {
					if (i != (health / 2)) {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 11,
								Colors.get(-1, 000, 400, 544));
					}
				} else {
					if (i == (health / 2)) {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 12,
								Colors.get(-1, 000, 400, 544));
					} else {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 11,
								Colors.get(-1, 000, 400, 544));
					}
				}
			}
		}

		if (isLocal) {
			for (int i = 0; i < ((energy / 2) + 1); i++) {
				if ((energy % 2) == 0) {
					if (i != (energy / 2)) {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 13,
								Colors.get(-1, 000, 440, 555));
					}
				} else {
					if (i == (energy / 2)) {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 14,
								Colors.get(-1, 000, 440, 555));
					} else {
						screen.render(screen.xOffset + (i * 32),
								screen.yOffset, 13,
								Colors.get(-1, 000, 440, 555));
					}
				}
			}
		}
		//super.debugRender(screen);
	}

	@Override
	public boolean hasCollided(int xa, int ya) {
		final int xMin = 0;
		final int xMax = 31;
		final int yMin = 0;
		final int yMax = 31;
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				return true;
			}
		}
		for (int x = xMin; x < xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				return true;
			}
		}
		for (int y = yMin; y < yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Makes the player take a certain amount of damage.
	 *
	 * @param damage
	 *            Amount of damage to take.
	 */
	public void takeDamage(int damage) {
		healthRegen = 0;
		if (damage < health) {
			if (damage >= 0) {
				damageHit = 20;
				level.addEntity(new Explode(level, -2, x, y));
			}
			health -= damage;
		} else {
			health = 0;
			level.addEntity(new Explode(level, -2, x, y));
		}
		int teamcolor = 0;
		if (team.equals("GREEN")) {
			teamcolor = 121;
		} else {
			teamcolor = 211;
		}
		if (health >= 11) {
			color = Colors.get(-1, teamcolor, 050, 000);
		}
		if (health < 11) {
			color = Colors.get(-1, teamcolor, 550, 000);
		}
		if (health < 6) {
			color = Colors.get(-1, teamcolor, 500, 000);
		}
		if (health < 2) {
			color = Colors.get(-1, teamcolor, 000, 000);
		}
		if (health < 1) {
			color = Colors.get(-1, 111, 000, 000);
		}
	}

	/**
	 * Gets the username of this player.
	 *
	 * @return {@link String} representation of the username of this player.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the team of this player.
	 *
	 * @return {@link String} representation of the username of this player.
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * Returns the status of the flag of the player
	 *
	 * @return <code>True</code> if the player has a flag, <code>False</code>
	 *         otherwise.
	 */
	public boolean getFlag() {
		return flag;
	}

	/**
	 * Returns if the player is local to this machine.
	 *
	 * @return <code>True</code> if the player is local, <code>False</code>
	 *         otherwise.
	 */
	public boolean isLocal() {
		return isLocal;
	}

	/**
	 * Checks if the player's health is 0.
	 *
	 * @return <code>True</code> if the player's health is not 0,
	 *         <code>False</code> otherwise.
	 */
	public boolean isAlive() {
		return health != 0;
	}

	/**
	 * WTF?
	 *
	 * @return Something.
	 */
	public int getDamageHit() {
		return damageHit;
	}

	/**
	 * Check to see if the player can regenerate some health.
	 *
	 * @return <code>True</code> if the player can, <code>False</code>
	 *         otherwise.
	 */
	public boolean canRegenHealth() {
		final boolean temp = canRegenHealth;
		canRegenHealth = false;
		return temp;
	}

	/**
	 * Set if the player has a flag or not.
	 *
	 * @param flag
	 *            <code>True</code> if the player has a flag.
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * Safely set the position of the player. Using this method allows the use
	 * of tweening between the last point the player was seen and the current
	 * one being set.
	 *
	 * @param x
	 *            X chord
	 * @param y
	 *            Y chord
	 */
	public void setPos(int x, int y) {
		lastTweenX = this.x;
		lastX = this.x;
		this.x = x;
		lastTweenY = this.y;
		lastY = this.y;
		this.y = y;
		onTweenFrame = 0;
	}

	/**
	 * @return the score of this player
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score Sets the score of this player.
	 */
	public void setScore(int score) {
		this.score = score;
	}
}
