package game.net.packets;

import game.net.GameClient;
import game.net.GameServer;

import java.net.InetAddress;

public class Packet03TestLogin extends Packet {

	private String loginData;
	private String version;
	private int teamRed = -2;
	private int teamGreen = -2;
	private int id = 0;

	/**
	 * Note to developer:
	 *
	 * -3 is invalid -2 is request -1 and 0 is sendGreen 0 and -1 is sendRed
	 *
	 * @param data
	 *            Array of data that came from the internet
	 */
	public Packet03TestLogin(byte[] data) {
		super(03); // id of the packet.
		String[] dataArray = readData(data).split(",");
		if (dataArray.length == 5) {
			loginData = dataArray[0]; // data of username
			id = Integer.parseInt(dataArray[1]);
			version = dataArray[2]; // version
			teamGreen = Integer.parseInt(dataArray[3]);
			teamRed = Integer.parseInt(dataArray[4]);
		} else {
			dataArray = new String[] { dataArray[0], "NO VERSION!", "-3", "-3" };
		}
	}

	/**
	 * Used to create a login packet.
	 *
	 * @param loginData
	 *            Generally the username, or a failed connection message
	 * @param id
	 *            The id used to login. TO SEND LOGIN PACKET TO SERVER USE -1!
	 *            (Different than getting teams!)
	 * @param version
	 *            Game version
	 */
	public Packet03TestLogin(String loginData, int id, String version) {
		super(03); // Sending it from the client.
		this.loginData = loginData;
		this.id = id;
		this.version = version;
		teamGreen = -2;
		teamRed = -2;
	}

	/**
	 * Poweruser way to create a login packet.
	 *
	 * @param loginData
	 *            Generally the username, or a failed connection message
	 * @param id
	 *            The id used to login. TO SEND LOGIN PACKET TO SERVER USE -1!
	 *            (Different than getting teams!)
	 * @param version
	 *            Game version
	 * @param green
	 *            Get or set team green
	 * @param red
	 *            Get or set team red
	 */
	public Packet03TestLogin(String loginData, int id, String version,
			int green, int red) {
		super(03); // Sending it from the client.
		this.loginData = loginData;
		this.id = id;
		this.version = version;
		teamGreen = green;
		teamRed = red;
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
		return ("03" + loginData + "," + id + "," + version + "," + teamGreen
				+ "," + teamRed).getBytes();
	}

	@Override
	public String getUsername() {
		return loginData;
	}

	public int getID() {
		return id;
	}

	public String getReply() {
		return loginData;
	}

	public String getVersion() {
		return version;
	}

	public int getGreen() {
		return teamGreen;
	}

	public int getRed() {
		return teamRed;
	}
}
