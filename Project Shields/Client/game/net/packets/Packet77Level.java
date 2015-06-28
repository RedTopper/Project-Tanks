package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet77Level extends Packet {

	private String path;
	private int width;
	private int height;
	
	public Packet77Level(byte[] data) {
		super(77); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 4) {
			username = dataArray[0];
			path = dataArray[1];
			width = Integer.parseInt(dataArray[2]);
			height = Integer.parseInt(dataArray[3]);
		} else {
			isValid = false;
		}
	}

	public Packet77Level(String username, String path, int width, int height) {
		super(77); // Sending it from the client
		this.path = path;
		this.username = username;
		this.width = width;
		this.height = height;
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
		server.sendData(getData(), address, port);
	}

	@Override
	public byte[] getData() {
		return ("77" + username + "," + path + "," + width + "," + height).getBytes();
	}
	
	public String getPath() {
		return path;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
