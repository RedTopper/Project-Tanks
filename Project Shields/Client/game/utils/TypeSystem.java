package game.utils;

/**
 * Types used for the system menu.
 * @author AJ Walter
 */
public enum TypeSystem {
	/**
	 * The system menu is in its closed position.
	 */
	CLOSED, 
	
	/**
	 * The system menu is showing the default options.
	 */
	SYSTEM, 
	
	/**
	 * The system menu is currently selecting a mode for the game.
	 */
	MODE, 
	
	/**
	 * The system menu is trying to select a map for the game.
	 */
	MAP, 
	
	/**
	 * The system menu is showing the controls for the game.
	 */
	CONTROLS
}