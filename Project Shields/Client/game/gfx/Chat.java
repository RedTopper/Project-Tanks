package game.gfx;

import game.utils.Debug;
import game.utils.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * This class displays the chat that comes from the server, other people, or
 * other messages that are urgent. The Chat object is only a display, and does
 * not accept input.
 *
 * @author AJ Walter
 */
public class Chat {

	/**
	 * Chat message history.
	 */
	private final List<String> chatHistory = new ArrayList<>();

	/**
	 * Username of the message.
	 */
	private final List<String> chatter = new ArrayList<>();

	/**
	 * Color of the sent message.
	 */
	private final List<Integer> chatColor = new ArrayList<>();

	/**
	 * Height of the letters in the chatter box.
	 */
	public static final double TEXT_HEIGHT = 0.5;

	/**
	 * Amount of messages displayed when the chat is closed.
	 */
	public static final int CLOSED_HEIGHT = 3;

	/**
	 * Amount of messages displayed when the chat is open.
	 */
	public static final int OPEN_HEIGHT = 11;

	/**
	 * Name of this class.
	 */
	public static final String CLASS = "Chatter";

	/**
	 * Amount of ticks the chatter is open for before hiding.
	 */
	public static final int TICKS_OPEN = 300;

	private int showing = TICKS_OPEN;

	/**
	 * Updates the chat.
	 */
	public void tick() {
		if (showing > 0) {
			showing--;
		}
	}

	/**
	 * Displays the current set of messages to the screen.
	 *
	 * @param screen
	 *            {@link Screen} object to display on.
	 * @param pressed
	 *            True to show the chat, false otherwise.
	 * @param msg
	 *            {@link String} The message to be rendered in the type field.
	 */
	public void renderChat(Screen screen, boolean pressed, String msg) {
		if (pressed) {
			Font.render(msg, screen, screen.xOffset,
					(screen.yOffset + screen.height) - 32,
					Colors.get(-1, -1, -1, 500), TEXT_HEIGHT);
			for (int i = 0; (i < OPEN_HEIGHT) && (i < chatHistory.size()); i++) {
				final String cChatter = chatter.get(i);
				final String cHistory = chatHistory.get(i);
				final int cColor = chatColor.get(i);
				Font.render(cChatter + cHistory, screen, screen.xOffset,
						(screen.yOffset + screen.height)
						- (32 + (int) (32 * TEXT_HEIGHT))
								- (i * (int) (32 * TEXT_HEIGHT)),
						Colors.get(-1, -1, -1, cColor), TEXT_HEIGHT);
			}
		} else {
			if (showing > 0) {
				for (int i = 0; (i < CLOSED_HEIGHT) && (i < chatHistory.size()); i++) {
					final String cChatter = chatter.get(i);
					final String cHistory = chatHistory.get(i);
					final int cColor = chatColor.get(i);
					Font.render(cChatter + cHistory, screen, screen.xOffset,
							(screen.yOffset + screen.height) - 32
									- (i * (int) (32 * TEXT_HEIGHT)),
							Colors.get(-1, -1, -1, cColor), TEXT_HEIGHT);
				}
			}
		}
	}

	/**
	 * Adds a new message to the chatter.
	 *
	 * @param username
	 *            {@link String} Username that said the message.
	 * @param message
	 *            {@link String} Message
	 * @param color
	 *            Hex based int from 000 to 555.
	 */
	public void addMessageToHistory(String username, String message, int color) {
		chatter.add(0, username);
		chatHistory.add(0, message);
		chatColor.add(0, color);
		Debug.out(Type.INFO, CLASS, username + message);
		showing = TICKS_OPEN; // reset
	}
}
