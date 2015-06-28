package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet01Disconnect extends Packet {

	public Packet01Disconnect(byte[] data) {
		super(01); // id of the packet.
		username = readData(data); // Strip the packet and read the
		// username
	}

	public Packet01Disconnect(String username) {
		super(01); // Sending it from the client.
		this.username = username;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public void writeData(GameServer server, InetAddress address, int port) {
		return;
	}

	@Override
	public byte[] getData() {
		return ("01" + username).getBytes();
	}
}
