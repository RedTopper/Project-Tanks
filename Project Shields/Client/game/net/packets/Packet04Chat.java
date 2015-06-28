package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet04Chat extends Packet {

	private String chat;
	private int color;

	public Packet04Chat(byte[] data) {
		super(04); // id of the packet.
		final String[] dataArray = readData(data).split("#");
		if (dataArray.length == 3) {
			username = dataArray[0]; // Sets the username
			color = Integer.parseInt(dataArray[1]); // Set the color
			chat = dataArray[2]; // Set the message
		} else {
			isValid = false;
		}
	}

	public Packet04Chat(String username, int color, String chat) {
		super(04); // Sending it from the client.
		this.chat = chat;
		this.username = username;
		this.color = color;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		return;
	}

	@Override
	public void writeData(GameServer server, InetAddress address, int port) {
		server.sendData(getData(), address, port);
	}

	@Override
	public byte[] getData() {
		return ("04" + username + "#" + color + "#" + chat).getBytes();
	}

	public String getMessage() {
		return chat;
	}

	public int getColor() {
		return color;
	}
}
