package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet00Login extends Packet {
	private int x, y;
	private String team;
	private int id;

	public Packet00Login(byte[] data) {
		super(00); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 5) {
			username = dataArray[0]; // Strip the packet and read the
			// username
			id = Integer.parseInt(dataArray[1]);
			x = Integer.parseInt(dataArray[2]);
			y = Integer.parseInt(dataArray[3]);
			team = dataArray[4];
		} else {
			isValid = false;
		}
	}

	public Packet00Login(String username, int id, int x, int y, String team) {
		super(00); // Sending it from the client.
		this.username = username;
		this.id = id;
		this.x = x;
		this.y = y;
		this.team = team;
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
		return ("00" + username + "," + id + "," + getX() + "," + getY() + "," + team)
				.getBytes();
	}

	public int getID() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getTeam() {
		return team;
	}
}
