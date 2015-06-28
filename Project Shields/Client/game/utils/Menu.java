package game.utils;

import game.gfx.Colors;
import game.gfx.Font;
import game.gfx.Screen;

/**
 * A menu is a simple  menu that enables players to type options
 * or select one option from a list.
 * @author AJ Walter
 *
 */
public class Menu {
	/**
	 * The selectable options in the menu
	 */
	private String[] options;
	
	/**
	 * An array of the fields the user has typed in.
	 */
	private String[] typedFields;

	/**
	 * <code>True</code> if the menu has typing fields.
	 */
	private boolean input;
	
	/**
	 * The selected item.
	 */
	public int selected = 0;
	
	/**
	 * Requests a clear of this menu.
	 */
	private boolean requestClear;
	
	/**
	 * <code>True</code> if a key was just pressed. Used to make sure
	 * a menu item is rapidly or pressed. Prevents
	 * suddenly moving cursor to the top or bottom of the menu.
	 */
	private boolean keyDownWasPressed = false,
								keyUpWasPressed = false,
								keyEnterWasPressed = false;
	
	/**
	 * Selected item to set.
	 */
	private String set = null;
	
	/**
	 * Title of this menu.
	 */
	private String title;

	/**
	 * Used to create a selectable or typable menu.
	 *
	 * @param options
	 *            Array of Strings for the menu to display
	 * @param input
	 *            True = All fields become able to be typed on.
	 * @param title
	 *            Title of the menu
	 */
	public Menu(String[] options, boolean input, String title) {
		this.options = options;
		this.input = input;
		typedFields = new String[options.length];
		for (int i = 0; i < typedFields.length; i++) {
			typedFields[i] = "";
		}
		this.title = title;
	}

	/**
	 * Selects the next item.
	 * @param pressing The boolean variable of the key pressed at every moment.
	 */
	public void nextItem(boolean pressing) {
		if (pressing && keyDownWasPressed) {
			keyDownWasPressed = false;
			if (input == false) {
				if (selected < (options.length - 1)) {
					selected++;
					set = typedFields[selected];
				}
			} else {
				if (selected <= options.length) {
					selected++;
					if (selected < typedFields.length) {
						set = typedFields[selected];
					}
				}
			}
		}
		if (!pressing) {
			keyDownWasPressed = true;
		} else {
			keyDownWasPressed = false;
		}
	}

	/**
	 * Selects the previous item. 
	 * @param pressing The boolean variable of the key pressed at every moment.
	 */
	public void previousItem(boolean pressing) {
		if (pressing && keyUpWasPressed) {
			keyUpWasPressed = false;
			if (selected > 0) {
				selected--;
				if (selected < typedFields.length) {
					set = typedFields[selected];
				}
			}
		}
		if (!pressing) {
			keyUpWasPressed = true;
		} else {
			keyUpWasPressed = false;
		}
	}

	/**
	 * Render the menu to a screen.
	 * @param screen {@link Screen} to render the menu to.
	 * @param x X position of the menu.
	 * @param y Y position of the menu.
	 */
	public void render(Screen screen, int x, int y) {
		Font.render(title, screen, screen.xOffset + x, screen.yOffset + y,
				Colors.get(-1, -1, -1, 550), .5);
		if (input == false) {
			for (int i = 0; i < options.length; i++) {
				Font.render(((selected == i) ? "> " : "  ") + options[i],
						screen, screen.xOffset + x, screen.yOffset + y
								+ ((i + 1) * 16), Colors.get(-1, -1, -1, 555),
						.5);
			}
		} else {
			for (int i = 0; i < options.length; i++) {
				Font.render(((selected == i) ? "> " : "  ") + options[i]
						+ typedFields[i] + ((selected == i) ? "|" : " "),
						screen, screen.xOffset + x, screen.yOffset + y
								+ ((i + 1) * 16), Colors.get(-1, -1, -1, 555),
						.5);
			}
			Font.render(
					((selected == options.length) ? "> " : "  ") + "Accept",
					screen, screen.xOffset + x, screen.yOffset + y
							+ (options.length * 16) + 32,
					Colors.get(-1, -1, -1, 555), .5);
			Font.render(((selected == (options.length + 1)) ? "> " : "  ")
					+ "Back", screen, screen.xOffset + x, screen.yOffset + y
					+ (options.length * 16) + 48, Colors.get(-1, -1, -1, 555),
					.5);
		}
	}

	/**
	 * Return the currently selected item.
	 * @return the currently selected item.
	 */
	public int selected() {
		return selected;
	}

	/**
	 * Return the length of the options.
	 * @return Length of options list.
	 */
	public int length() {
		return options.length;
	}

	/**
	 * Sets some position of all of the typeable fields to some value.
	 * @param index Index to change in typedFields
	 * @param str String to change it to.
	 */
	public void setTypedFields(int index, String str) {
		if (input && (index < typedFields.length)) {
			typedFields[index] = str;
		}
	}

	/**
	 * Gets a list of all of the typed fields. Null at all times EXCEPT when all
	 * of the fields have been selected at least once and the enter key was pressed.
	 * @param pressing Constantly updated boolean variable of the enter key.
	 * @return List of fields, null if the user has not pressed enter yet.
	 */
	public String[] getTypedFields(boolean pressing) {
		if (!pressing && keyEnterWasPressed && (selected == options.length)) {
			keyEnterWasPressed = false;
			return typedFields;
		}
		if (pressing) {
			keyEnterWasPressed = true;
		} else {
			keyEnterWasPressed = false;
		}
		return null;
	}

	/**
	 * Requests a clear of a field.
	 * @return <code>True</code> if we should clear.
	 */
	public boolean requestClear() {
		return requestClear;
	}

	/**
	 * Makes requestClear false.
	 */
	public void setRequestClear() {
		requestClear = false;
	}

	/**
	 * Requests the variable of the currently selected item.
	 * @return The currently selected item.
	 */
	public String requestSet() {
		return set;
	}

	/**
	 * Nulls the currently selected item.
	 */
	public void setRequestSet() {
		set = null;
	}
}
