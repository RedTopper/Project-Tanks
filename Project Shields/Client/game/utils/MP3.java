package game.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.player.Player;

/**
 * This class is used to play MP3 files using the javazoom library.
 * @author AJ Walter
 */
public class MP3 {
	/**
	 * File name of the song.
	 */
	private String filename;
	
	/**
	 * Javazoom player.;
	 */
	private Player player;
	
	/**
	 * Thread that plays MP3 files.
	 */
	private Thread mp3Thread;
	
	/**
	 * True if the directory cannot be changed due to access of this class.
	 */
	private boolean locked;
	
	/**
	 * True if the player is idle.
	 */
	private boolean idle;

	/**
	 * Raw input stream of the MP3.
	 */
	private InputStream fis = null;
	
	/**
	 * Buffered input stream of the MP3.
	 */
	private BufferedInputStream bis = null;

	/**
	 * Creates a new MP3 player with no audio.
	 */
	public MP3() {
		filename = "";
		mp3Thread = null;
		locked = true;
		idle = true;
	}

	/**
	 * Plays the song contained within this player.
	 */
	public void play() {
		if (!locked) {
			if ((filename != null) && (filename != "")) {
				try {
					setBis(setFis(MP3.class.getResourceAsStream(filename)));
					player = new Player(bis);

					mp3Thread = new Thread() {
						@Override
						public void run() {
							try {
								idle = false;
								Debug.out(Type.INFO, "Music", "Played " + filename + "!");
								player.play();
								Debug.out(Type.INFO, "Music", "Song finished!");
								idle = true;
								return;
							} catch (final Exception e) {
								e.printStackTrace();
							}
						}
					};

					mp3Thread.start();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Stops the music in it's tracks.
	 */
	public void stopMusic() {
		if (player != null) {
			player.close();
		}
	}

	/**
	 * Changes the music to a separate directory.
	 * @param newSong String directory to change the player to.
	 */
	public void changeMusic(String newSong) {
		locked = true;
		filename = "";
		try {
			if (player != null) {
				player.close();
			}
			mp3Thread = null;

			filename = newSong;

			locked = false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attemps to close this player.
	 */
	public void close() {
		locked = true;

		try {
			if (player != null) {
				player.close();
			}

			if (mp3Thread != null) {
				mp3Thread = null;
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private InputStream setFis(InputStream file) {
		if (fis != null) {
			try {
				fis.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		fis = file;
		return fis;
	}

	private BufferedInputStream setBis(InputStream buff) {
		if (bis != null) {
			try {
				bis.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		bis = new BufferedInputStream(buff);
		return bis;
	}

	/**
	 * True if this player is not currently playing a song.
	 * @return <code>True</code> if there is a song playing.
	 */
	public boolean isIdle() {
		return idle;
	}
}