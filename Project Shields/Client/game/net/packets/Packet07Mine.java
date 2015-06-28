package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet07Mine extends Packet {

	private int id;
	private int x;
	private int y;
	private String team;

	public Packet07Mine(byte[] data) {
		super(07); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 5) {
			username = dataArray[0]; // Sets the username
			id = Integer.parseInt(dataArray[1]);
			x = Integer.parseInt(dataArray[2]); // Set the x
			y = Integer.parseInt(dataArray[3]); // Set the y
			team = dataArray[4];
		} else {
			isValid = false;
		}
	}

	public Packet07Mine(String username, int id, int x, int y, String team) {
		super(07); // Sending it from the client
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
		return ("07" + username + "," + id + "," + x + "," + y + "," + team)
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

	public void setID(int id) {
		this.id = id;
	}
}
