package game.utils;

import game.Game;

import javax.swing.JOptionPane;

/**
 * The Debug class handles the output of the game. It is used to format and
 * (if necessary) crash the game if the output requests it to.
 * @author AJ Walter
 * @see Type
 */
public class Debug {

	/**
	 * System output.
	 *
	 * @param level
	 *            Enum Type level
	 * @param name
	 *            The name of the class (Variable should be called CLASS!)
	 * @param msg
	 *            Message to send to the log.
	 */
	public static void out(Type level, String name, String msg) {
		switch (Game.debugLevel) {
		default:
		case TRACE:
			if (level == Type.TRACE) {
				System.out.println("[" + name + "][MOVE] " + msg);
			}
		case DEBUG:
			if (level == Type.DEBUG) {
				System.out.println("[" + name + "][DEBUG] " + msg);
			}
		case INFO:
			if (level == Type.INFO) {
				System.out.println("[" + name + "][INFO] " + msg);
			}
		case WARNING:
			if (level == Type.WARNING) {
				System.out.println("[" + name + "][WARNING] " + msg);
			}
		case SEVERE:
			if (level == Type.SEVERE) {
				System.err.println();
				System.err.println("Oh no! The game has crashed. Here is some"
						+ " debug information to send to the devs.");
				System.err.println("[" + name + "][SEVERE] " + msg);
				JOptionPane.showMessageDialog(null, "[" + name + "][SEVERE] "
						+ msg, "ERROR: " + "Crash!",
						JOptionPane.INFORMATION_MESSAGE);
				Runtime.getRuntime().halt(0); // CRASH IT
			}
		}
	}
}
