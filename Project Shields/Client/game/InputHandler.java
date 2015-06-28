package game;

import game.gfx.Font;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * {@link InputHandler} is a class that handles all of the types of key presses
 * that can happen within the game. This class extends {@link KeyListener}. An
 * {@link InputHandler} is also responsible for acquiring all messages that need
 * to be sent in the game.
 *
 * @author AJ Walter
 *
 */
public class InputHandler implements KeyListener {

	/**
	 * Message currently being typed by the user.
	 */
	private String message = "";

	/**
	 * States if the user is currently typing or not.
	 */
	private boolean isTyping = false;

	/**
	 * Change this to true once the user has pressed enter. The game class picks
	 * this up and sends the currently typed message if true.
	 */
	private boolean shouldSendMessage = false;

	/**
	 * Checks if the user has just opened the chat. This boolean removes the "t"
	 * that would be typed before every message.
	 */
	private boolean firstChar = true;

	/**
	 * wtf?
	 */
	private int mode = 0;

	/**
	 * Sets up user input.
	 *
	 * @param game
	 *            The {@link Game}.
	 * @param mode
	 *            Original {@link InputHandler#mode} that the input is set up
	 *            on. (0 or 1).
	 */
	public InputHandler(Game game, int mode) {
		game.addKeyListener(this);
		this.mode = mode;
	}

	/**
	 * Sets the {@link InputHandler#mode} that we need to use. If 0, escape will
	 * not clear. Options for i=0,1.
	 *
	 * @param i
	 *            0 or 1
	 */
	public void setMode(int i) {
		mode = i;
	}

	/**
	 * A class in {@link InputHandler} to handle the keys.
	 *
	 * @author AJ Walter
	 */
	public class Key {
		private boolean pressed = false;

		/**
		 * Checks to see if a {@link Key} is pressed.
		 *
		 * @return pressed
		 */
		public boolean isPressed() {
			return pressed;
		}

		/**
		 * Toggles a {@link Key}.
		 *
		 * @param isPressed
		 *            boolean is the key pressed.
		 */
		public void toggle(boolean isPressed) {
			pressed = isPressed;
		}
	}

	/**
	 * Controls the {@link Key} for the user's position. (WASD)
	 */
	public Key up = new Key(), down = new Key(), left = new Key(),
			right = new Key();

	/**
	 * Controls the {@link Key} for the camera. (ARROWS)
	 */
	public Key camup = new Key(), camdown = new Key(), camleft = new Key(),
			camright = new Key();

	/**
	 * Controls the {@link Key} for positioning the camera over the player.
	 * (SPACE)
	 */
	public Key camcenter = new Key();

	/**
	 * Controls the {@link Key} for sending a message. (ENTER)
	 */
	public Key textmessage = new Key();

	/**
	 * Controls the {@link Key} for sprinting. (SHIFT)
	 */
	public Key mod = new Key();

	/**
	 * Controls the {@link Key} for firing a bullet. (1, U, and NUMPAD_1)
	 */
	public Key fire = new Key();

	/**
	 * Controls the {@link Key} for placing a mine. (2, I, and NUMPAD_2)
	 */
	public Key mine = new Key();

	/**
	 * Controls the {@link Key} for closing out of menus. (Esc)
	 */
	public Key escape = new Key();

	// Key listener implemented method Key Pressed.
	@Override
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	// Key listener implemented method Key Released.
	@Override
	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	// Key listener implemented method Key Typed.
	@Override
	public void keyTyped(KeyEvent e) {
		final char c = e.getKeyChar();
		if (isTyping && (c != KeyEvent.VK_BACK_SPACE)
				&& (c != KeyEvent.VK_ENTER) && (c != KeyEvent.VK_ESCAPE)
				&& (mode == 1)) {
			if (Font.CHARS.indexOf((c + "").toUpperCase()) >= 0) {
				message += c;
			}
		}
		if (isTyping && (c != KeyEvent.VK_BACK_SPACE)
				&& (c != KeyEvent.VK_ENTER) && (c != KeyEvent.VK_ESCAPE)
				&& (mode == 0)) {
			if (!firstChar) {
				if (Font.CHARS.indexOf((c + "").toUpperCase()) >= 0) {
					message += c;
				}
			} else {
				firstChar = false;
			}
		}
	}

	/**
	 * Clear text from the listener.
	 */
	public void clearMessage() {
		message = "";
	}

	/**
	 * Gets the text typed in the listener.
	 *
	 * @return {@link String} typed message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets a message to send.
	 *
	 * @param msg
	 *            {@link String} message.
	 */
	public void setMessage(String msg) {
		message = msg;
	}

	/**
	 * Toggle keys
	 *
	 * @param keyCode
	 *            Key code
	 * @param isPressed
	 *            Is it pressed?
	 */
	public void toggleKey(int keyCode, boolean isPressed) {
		if (!isTyping) {
			if (keyCode == KeyEvent.VK_W) {
				up.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_S) {
				down.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_A) {
				left.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_D) {
				right.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_UP) {
				camup.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_DOWN) {
				camdown.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_LEFT) {
				camleft.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_RIGHT) {
				camright.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_SPACE) {
				camcenter.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_T) {
				textmessage.toggle(isPressed);
				setTyping(true);
				firstChar = true;
			}
			if (keyCode == KeyEvent.VK_SHIFT) {
				mod.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_SLASH) {
				textmessage.toggle(isPressed);
				setTyping(true);
				message = "/";
				firstChar = true;
			}
			if ((keyCode == KeyEvent.VK_1) || (keyCode == KeyEvent.VK_NUMPAD1)
					|| (keyCode == KeyEvent.VK_U)) {
				fire.toggle(isPressed);
			}
			if ((keyCode == KeyEvent.VK_2) || (keyCode == KeyEvent.VK_NUMPAD2)
					|| (keyCode == KeyEvent.VK_I)) {
				mine.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_ENTER) {
				textmessage.toggle(isPressed);
				shouldSendMessage = true;
			}
			if (keyCode == KeyEvent.VK_ESCAPE) {
				escape.toggle(isPressed);
				textmessage.toggle(false);
				setTyping(false);
			}
		} else { // we must be typing then.
			if (keyCode == KeyEvent.VK_UP) {
				camup.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_DOWN) {
				camdown.toggle(isPressed);
			}
			if (keyCode == KeyEvent.VK_ENTER) {
				textmessage.toggle(isPressed);
				shouldSendMessage = true;
				if (mode == 0) {
					isTyping = false;
				}
			}
			if ((keyCode == KeyEvent.VK_ESCAPE) && (mode == 0)) {
				setTyping(false);
			}
			if (keyCode == KeyEvent.VK_BACK_SPACE) {
				if ((message.length() > 0) && (isPressed == true)) {
					message = message.substring(0, message.length() - 1);
				}
			}
		}
	}

	/**
	 * Sets if the handler is in typing mode. Clears the current message when
	 * called.
	 *
	 * @param b
	 *            boolean typing.
	 */
	public void setTyping(boolean b) {
		isTyping = b;
		clearMessage();
	}

	/**
	 * Checks if the user is typing
	 *
	 * @return true if typing, false otherwise.
	 */
	public boolean isTyping() {
		return isTyping;
	}

	/**
	 * Gets a {@link String} with the message if it is available. Returns null
	 * if there is no message to be sent.
	 *
	 * @return {@link String} message if available, null otherwise.
	 */
	public String getSendableMessage() {
		if (shouldSendMessage) {
			shouldSendMessage = false;
			final String msg = message;
			message = "";
			return msg;
		} else {
			return null;
		}
	}
}
