package tickettoride.model;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Destination;

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
	
	/**
	 * Returns the number of transportation cards for the specified color
	 * @param color color to check
	 * @return the number of cards of that color for this game
	 */
	public int getNumberOfTransportationCards(CardColor color) {
		//TODO should return a number from the game definition file.
		//to be completed by student.
		//Note that the student will also have to either modify the constructor or add a
		//mutator to get this to work.
		return 10;
	}
	
	public Set<DestinationCard> getDestinationCards() {
		//TODO should return all of the destination cards for the game. To
		//be completed by the student.
		//Note that the student will also have to either modify the constructor or add a
		//mutator to get this to work.
		return Collections.emptySet();
	}
	
	public static class DestinationCard {
		/**
		 * The starting point that needs to be connected to {@link #end} in order
		 * to earn the points. (Note that it doesn't matter which destination is
		 * start and which one is end)
		 */
		private final Destination start;

		/**
		 * The ending point that needs to be connected to {@link #start} in order
		 * to earn the points. (Note that it doesn't matter which destination is
		 * start and which one is end)
		 */
		private final Destination end;
		/**
		 * The number of points that will be awarded for connecting {@link #start} and {@link #end}
		 */
		private final int numPoints;
		
		/**
		 * @param start {@link #start}
		 * @param end {@link #end}
		 * @param numPoints {@link #numPoints}
		 */
		public DestinationCard(Destination start, Destination end, int numPoints) {
			this.start = start;
			this.end = end;
			this.numPoints = numPoints;
		}
		
		/** @return {@link #start} */
		public Destination getStart() {
			return start;
		}

		/** @return {@link #end} */
		public Destination getEnd() {
			return end;
		}
		
		/** @return {@link #numPoints} */
		public int getNumPoints() {
			return numPoints;
		}
	}
}
