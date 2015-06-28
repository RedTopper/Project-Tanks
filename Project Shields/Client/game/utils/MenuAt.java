package game.utils;

/**
 * This enum is used to define where the title screen menu is.
 * @author AJ Walter
 */
public enum MenuAt {
	/**
	 * The menu is being reset to the {@link MAIN}, and needs to clear some
	 * variables.
	 */
	RESET, 
	
	/**
	 * The menu is at the main menu.
	 */
	MAIN, 
	
	/**
	 * The menu is asking login information from the user.
	 */
	USERNAME, 
	
	/**
	 * The menu is showing credits for the game.
	 */
	CREDITS,
	
	/**
	 * The menu is asking for the team to belong to.
	 */
	TEAM, 
	
	/**
	 * The game is setting up a connection to the server.
	 */
	PRE,
	
	/**
	 * The game is receiving data about the server.
	 */
	CONNECT, 
	
	/**
	 * The game is processing the information it received and connecting the client.
	 */
	POST
}
