package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet05Damage extends Packet {

	private int damage;

	public Packet05Damage(byte[] data) {
		super(05); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 2) {
			username = dataArray[0]; // Sets the username
			damage = Integer.parseInt(dataArray[1]); // Set the damage
		} else {
			isValid = false;
		}
	}

	public Packet05Damage(String username, int damage) {
		super(05); // Sending it from the client
		this.username = username;
		this.damage = damage;
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
		return ("05" + username + "," + damage).getBytes();
	}

	public int getDamage() {
		return damage;
	}
}
