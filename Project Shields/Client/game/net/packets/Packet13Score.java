package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet13Score extends Packet {

	String team;
	private int score;

	public Packet13Score(byte[] data) {
		super(13); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 3) {
			username = dataArray[0];
			team = dataArray[1];
			score = Integer.parseInt(dataArray[2]);
		} else {
			isValid = false;
		}
	}

	public Packet13Score(String username, String team, int score) {
		super(13); // Sending it from the client
		this.team = team;
		this.username = username;
		this.score = score;
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
		return ("13" + username + "," + team + "," + score).getBytes();
	}

	public int getScore() {
		return score;
	}

	public String getTeam() {
		return team;
	}
}
