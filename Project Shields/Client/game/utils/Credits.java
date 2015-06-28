package game.utils;

import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;

/**
 * Simple class used to render the credits in the credits menu at the
 * main menu.
 * @author AJ Walter
 */
public class Credits {
	/**
	 * Renders the credits to the screen.
	 * @param screen {@link Screen} to render to.
	 * @param start Position to render to.
	 * @return The height of the credits.
	 */
	public static int render(Screen screen, int start) {
		final String[][] things = new String[][] { { "Manager", "title" },
				{ "Aaron Walter", "lastperson" }, { "Assistant", "title" },
				{ "Aaron Walter", "lastperson" }, { "Director", "title" },
				{ "Aaron Walter", "lastperson" }, { "Code Team", "title" },
				{ "Lead Programmer", "subtitle" },
				{ "Aaron Walter", "person" }, { "Gameplay", "subtitle" },
				{ "Aaron Walter", "person" }, { "UI", "subtitle" },
				{ "Aaron Walter", "person" }, { "Controls", "subtitle" },
				{ "Aaron Walter", "person" }, { "Input", "subtitle" },
				{ "Aaron Walter", "lastperson" }, { "Art Team", "title" },
				{ "Lead Artist", "subtitle" }, { "Aaron Walter", "person" },
				{ "Level Design", "subtitle" }, { "Aaron Walter", "person" },
				{ "Sprites", "subtitle" }, { "Aaron Walter", "person" },
				{ "Tiles", "subtitle" }, { "Aaron Walter", "lastperson" },
				{ "Networking", "title" }, { "Aaron Walter", "lastperson" },
				{ "Credits", "title" }, { "Aaron Walter", "lastperson" },
				{ "Misc", "title" }, { "Aaron Walter", "lastperson" },
				{ "Music", "title" }, { "Kevin MacLeod", "person" },
				{ "(incompetech.com)", "lastperson" },
				{ "Debug Team", "title" },
				{ "Since the beginning", "subtitle" },
				{ "Aaron Walter", "lastperson" }, { "Since U1", "subtitle" },
				{ "Kurt Bowen", "lastperson" },
				{ "Since v0.0.13", "subtitle" }, { "Sam Dhuse", "person" },
				{ "Kyle Stalcup", "lastperson" },
				{ "Since v0.0.15", "subtitle" },
				{ "Shea Hymers", "lastperson" },
				{ "Since v0.0.16", "subtitle" },
				{ "Ryan Pizzo", "lastperson" }, { "Thanks To", "title" },
				{ "Aaron Walter", "person" }, { "Edric Yu", "person" },
				{ "JLayer - javazoom.net", "person" },
				{ "DesignsbyZephyr", "person" }, { "Mr. Miller", "lastperson" } };

		int on = 0;
		for (final String[] arr : things) {
			if (arr[1].equals("title")) {
				Font.render(arr[0], screen, 100, start + on,
						Colors.get(-1, -1, -1, 511), 2.0);
				on += 64;
			} else if (arr[1].equals("subtitle")) {
				Font.render(arr[0], screen, 120, start + on,
						Colors.get(-1, -1, -1, 421), 1.0);
				on += 32;
			} else if (arr[1].equals("person")) {
				Font.render(arr[0], screen, 160, start + on,
						Colors.get(-1, -1, -1, 555), 1.0);
				on += 32;
			} else if (arr[1].equals("lastperson")) {
				Font.render(arr[0], screen, 160, start + on,
						Colors.get(-1, -1, -1, 555), 1.0);
				on += 64;
			} else {
				Debug.out(Type.SEVERE, "Credits", "Could not find a type!");
			}
		}
		return on;
	}
}
