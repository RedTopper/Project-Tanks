package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;
import game.utils.Debug;
import game.utils.Type;

import java.net.InetAddress;

public abstract class Packet {

	protected boolean isValid = true;
	protected String username;
	public static final String CLASS = "Packet";

	public static enum PacketTypes {
		INVALID(-1), LOGIN(00), DISCONNECT(01), MOVE(02), TESTLOGIN(03), CHAT(
				04), DAMAGE(05), BULLET(06), MINE(07), DESTROY(10), STARTGAME(
						11), LEVEL(77), SCORE(13);

		private int packetId;

		private PacketTypes(int packetId) {
			this.packetId = packetId;
		}

		public int getId() {
			return packetId;
		}
	}

	public byte packetId;

	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}

	public String getUsername() {
		if ((username == null) || username.equals("")) {
			Debug.out(Type.WARNING, CLASS, "USERNAME IS NULL OR EMPTY! Type: "
					+ packetId);
		}
		return username;
	}

	public abstract void writeData(GameClient client); // How do we write data?

	public abstract void writeData(GameServer server); // How do we write data?

	public abstract void writeData(GameServer server, InetAddress address,
			int port);

	public String readData(byte[] data) { // How do we read that data? (Omits
		// the packet type)
		final String message = new String(data).trim();
		return message.substring(2); // Drop the packet type.
	}

	public abstract byte[] getData();

	public static PacketTypes lookupPacket(String packetId) {
		try {
			return lookupPacket(Integer.parseInt(packetId));
		} catch (final Exception e) {
			return PacketTypes.INVALID;
		}
	}

	public static PacketTypes lookupPacket(int id) {
		for (final PacketTypes p : PacketTypes.values()) { // Loop through all
			// of them
			if (p.getId() == id) { // If we find it
				return p; // return what it is
			}
		}
		return PacketTypes.INVALID; // We didn't find it!
	}

	public boolean isValid() {
		return isValid;
	}

	protected void debug(String str) {
		Debug.out(Type.DEBUG, CLASS, str);
	}
}
