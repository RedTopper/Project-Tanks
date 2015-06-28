package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet10Destroy extends Packet {

	private int id;

	public Packet10Destroy(byte[] data) {
		super(10); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 2) {
			username = dataArray[0];
			id = Integer.parseInt(dataArray[1]);
		} else {
			isValid = false;
		}
	}

	public Packet10Destroy(String username, int id) {
		super(10); // Sending it from the client
		this.username = username;
		this.id = id;
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
		return ("10" + username + "," + id).getBytes();
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
}
