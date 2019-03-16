package tickettoride.model;

import java.io.File;

/**
 * Defines a Ticket to ride game (there are a variety of releases of ticket to 
 * ride with different maps, initial train pieces, etc.) This class stores all
 * of the information to initialize a ticket to ride game. This data should be
 * read in from a game definition JSON file.
 * </br>
 * If at some point we wanted to define some slight variations on the rules, that
 * should be done in this class as well.
 * 
 * @author nate
 */
public class GameDefinition {

	/**
	 * Image file containing the background image to be displayed in the map pane 
	 */
	private final File backgroundImage;
	/**
	 * {@link MapData} object containing the destination and connection information.
	 */
	private final MapData mapData;
	/**
	 * Number of train pieces that each player will start the game with
	 */
	private final int initialNumberOfTrainsPerPlayer;
	
	/**
	 * Initializes a game definition with the specified parameters
	 * @param backgroundImage {@link #backgroundImage}
	 * @param mapData {@link #mapData}
	 * @param numTrains {@link #initialNumberOfTrainsPerPlayer}
	 */
	public GameDefinition(File backgroundImage, MapData mapData, int numTrains) {
		this.backgroundImage = backgroundImage;
		this.mapData = mapData;
		this.initialNumberOfTrainsPerPlayer = numTrains;
	}

	/**
	 * @return {@link #backgroundImage}
	 */
	public File getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * @return {@link #mapData}
	 */
	public MapData getMapData() {
		return mapData;
	}

	/**
	 * @return {@link #initialNumberOfTrainsPerPlayer}
	 */
	public int getInitialNumberOfTrainsPerPlayer() {
		return initialNumberOfTrainsPerPlayer;
	}
	
}
