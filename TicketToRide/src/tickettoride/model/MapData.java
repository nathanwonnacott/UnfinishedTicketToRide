package tickettoride.model;

import java.util.Collection;

/**
 * This represents the board map including the destinations, connections between them, and
 * the state of which paths have been claimed by which players (that part isn't really included
 * in the interface yet, but it will be).
 * Students should not modify this interface, but should create their own implementation of it.
 * @author nate
 */
public interface MapData extends Cloneable {
	
	/**
	 * Enum that represents each of the transportation card colors and 
	 * the colors of path segments.
	 * @author nate
	 */
	public enum CardColor {
		RED,
		ORANGE,
		YELLOW,
		GREEN,
		BLUE,
		PURPLE,
		BLACK,
		WHITE,
		/**In the case of transportation cards, this represents the wild card, in the
		 * case of path segments, this represents the grey paths which can be built with
		 * any color. */
		ANY
	}

	
	/**
	 * @return a collection of all of the destinations on the map
	 */
	public Collection<Destination> getDestinations();
	
	/**
	 * @return a collection of connections between cities
	 */
	public Collection<Connection> getConnections();
	
	/**
	 * @param dest Destination object on the map
	 * @return a collection of all connection objects that have the specified
	 * destination as either their start or end points.
	 */
	public Collection<Connection> getConnectionsToOrFromDest(Destination dest);
	
	/**
	 * Creates a deep copy of this map data
	 * @return clone of the map data
	 */
	public MapData clone();
	
	/**
	 * Class represents a single destination on the map. Students should not modify this
	 * class.
	 * @author nate
	 */
	public static class Destination {
		/**
		 * The name of the destination which should be displayed on the map
		 */
		private final String name;
		/**
		 * Number between 0 and 1 representing where the destination should be displayed on the
		 * X axis (0 being the far left, and 1 being the far right)
		 */
		private final double xFraction;
		/**
		 * Number between 0 and 1 representing where the destination should be displayed on the
		 * Y axis (0 being the top, and 1 being the bottom)
		 */
		private final double yFraction;
		
		/**
		 * Instantiates a destination object
		 * @param name {@link #name}
		 * @param xFraction {@link #xFraction}
		 * @param yFraction {@link #yFraction}
		 */
		public Destination(String name, double xFraction, double yFraction) {
			this.name = name;
			this.xFraction = xFraction;
			this.yFraction = yFraction;
		}

		/**
		 * @return {@link #name}
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return {@link #xFraction}
		 */
		public double getXFraction() {
			return xFraction;
		}

		/**
		 * @return {@link #yFraction}
		 */
		public double getYFraction() {
			return yFraction;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof Destination) {
				Destination otherDest = (Destination)other;
				return otherDest.getName().equals(this.getName()) &&
						otherDest.getXFraction() == this.getXFraction() &&
						otherDest.getYFraction() == this.getYFraction();
			}
			else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			//Just using the hash code for the name is sufficient for our use cases. This
			//hash code method is valid (see lecture on hash codes/hash sets). Since within
			//a single game, you'll never have two destinations with the same name with different
			//x/y fractions, this hash code should work really well.
			return name.hashCode();
		}
	}
	
	/**
	 * Represents a connection between two destinations
	 * @author nate
	 */
	public static class Connection {
		/** The destination at which the connection starts</br>
		 * Note that it doesn't really matter which destination is the start and which is the end
		 */
		private final Destination start;
		/** The destination at which the connection starts</br>
		 * Note that it doesn't really matter which destination is the start and which is the end
		 */
		private final Destination end;
		
		/**
		 * The number of transportation cards required to complete the connection
		 */
		private final int numSegments;
		/**
		 * The color of transportation card that is required to complete the segment (note that {@link CardColor#ANY}
		 * transportation cards can be used for any connection color and that if this color is {@link CardColor#ANY},
		 * then any color of transportation cards can be used to completed this connection).
		 */
		private final CardColor color;
		
		/**
		 * Constructs a Connection object between the specified destinations with the specified properties
		 * @param start sets {@link #start}
		 * @param end sets {@link #end}
		 * @param color sets {@link #color}
		 * @param numSegments sets {@link #numSegments}
		 */
		public Connection(Destination start, Destination end, CardColor color, int numSegments) {
			this.start = start;
			this.end = end;
			this.numSegments = numSegments;
			this.color = color;
		}
		
		/** @return {@link #start} */
		public Destination getStart() {
			return start;
		}
		
		/** @return {@link #end} */
		public Destination getEnd() {
			return end;
		}
		
		/** @return {@link #color} */
		public CardColor getColor() {
			return color;
		}
		
		/** @return {@link #numSegments} */
		public int getNumSegments() {
			return numSegments;
		}
	}
}
