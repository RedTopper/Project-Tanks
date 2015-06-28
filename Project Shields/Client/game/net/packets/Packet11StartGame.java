package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet11StartGame extends Packet {

	private String gameMode = null;
	private String gameMap = null;
	private String systemMap = null;

	public Packet11StartGame(byte[] data) {
		super(11); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 4) {
			username = dataArray[0];
			gameMode = dataArray[1];
			gameMap = dataArray[2];
			systemMap = dataArray[3];
		} else {
			isValid = false;
		}
	}

	public Packet11StartGame(String username, String gameMode, String gameMap,
			String systemMap) {
		super(11); // Sending it from the client
		this.username = username;
		this.gameMode = gameMode;
		this.gameMap = gameMap;
		this.systemMap = systemMap;
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
		return ("11" + username + "," + gameMode + "," + gameMap + "," + systemMap)
				.getBytes();
	}

	public String getGameMode() {
		return gameMode;
	}

	public String getGameMap() {
		return gameMap;
	}

	public String getSystemMap() {
		return systemMap;
	}
}
