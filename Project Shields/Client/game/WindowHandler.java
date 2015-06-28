package game;

import game.net.packets.Packet01Disconnect;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * The {@link WindowHandler} class is added to the applications
 * <code>frame</code>. This class implements {@link WindowListener}. The only
 * method used is {@link WindowHandler#windowClosing(WindowEvent)}
 *
 * @author AJ Walter
 */
public class WindowHandler implements WindowListener {

	private final Game game;

	public WindowHandler(Game game) {
		this.game = game;
		this.game.frame.addWindowListener(this); // adds the window listener.
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * Sends a disconnect packet when the user clicks the X button of their
	 * game.
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		final Packet01Disconnect packet = new Packet01Disconnect(
				game.player.getUsername());
		packet.writeData(game.socketClient);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

}
