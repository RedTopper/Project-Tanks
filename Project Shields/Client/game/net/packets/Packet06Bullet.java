package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet06Bullet extends Packet {

	private int id;
	private int x;
	private int y;
	private int direction;
	private String team;

	public Packet06Bullet(byte[] data) {
		super(06); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 6) {
			username = dataArray[0]; // Sets the username
			id = Integer.parseInt(dataArray[1]);
			x = Integer.parseInt(dataArray[2]); // Set the x
			y = Integer.parseInt(dataArray[3]); // Set the y
			direction = Integer.parseInt(dataArray[4]); // Set the
			// direction
			team = dataArray[5];
		} else {
			isValid = false;
		}
	}

	public Packet06Bullet(String username, int id, int x, int y, int direction,
			String team) {
		super(06); // Sending it from the client
		this.username = username;
		this.id = id;
		this.x = x;
		this.y = y;
		this.direction = direction;
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
		return ("06" + username + "," + id + "," + x + "," + y + ","
				+ direction + "," + team).getBytes();
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

	public int getDirection() {
		return direction;
	}

	public String getTeam() {
		return team;
	}

	public void setID(int id) {
		this.id = id;

	}
}
