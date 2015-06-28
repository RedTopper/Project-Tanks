package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet02Move extends Packet {

	private int x, y;
	private boolean isMoving = false;
	private int movingDir = 1;
	private boolean hasFlag = false;

	public Packet02Move(byte[] data) {
		super(02); // id of the packet.
		final String[] dataArray = readData(data).split(",");
		if (dataArray.length == 6) {
			username = dataArray[0];
			x = Integer.parseInt(dataArray[1]);
			y = Integer.parseInt(dataArray[2]);
			isMoving = Integer.parseInt(dataArray[3]) == 1;
			movingDir = Integer.parseInt(dataArray[4]);
			hasFlag = Integer.parseInt(dataArray[5]) == 1;
		} else {
			isValid = false;
		}
	}

	public Packet02Move(String username, int x, int y, boolean isMoving,
			int movingDir, boolean flag) {
		super(02); // Sending it from the client.
		this.username = username;
		this.x = x;
		this.y = y;
		this.isMoving = isMoving;
		this.movingDir = movingDir;
		hasFlag = flag;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAllClientsExceptSelf(getData(), username);
	}

	@Override
	public void writeData(GameServer server, InetAddress address, int port) {
		return;
	}

	@Override
	public byte[] getData() {
		int moving = 0;
		if (isMoving) {
			moving = 1;
		} else {
			moving = 0;
		}

		int flag = 0;
		if (hasFlag) {
			flag = 1;
		} else {
			flag = 0;
		}
		return ("02" + username + "," + x + "," + y + "," + moving + ","
				+ movingDir + "," + flag).getBytes();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public int getMovingDir() {
		return movingDir;
	}

	public boolean getFlag() {
		return hasFlag;
	}
}
