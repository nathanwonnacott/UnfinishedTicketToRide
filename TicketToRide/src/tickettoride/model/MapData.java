package tickettoride.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.scene.image.Image;

public interface MapData {
	
	public enum CardColor {
		RED,
		ORANGE,
		YELLOW,
		GREEN,
		BLUE,
		PURPLE,
		BLACK,
		WHITE,
		ANY
	}

	
	public Collection<Destination> getDestinations();
	
	public Image getBackgroundImage();
	
	public static class Destination {
		private final String name;
		private final double xFraction;
		private final double yFraction;
		
		public Destination(String name, double xFraction, double yFraction) {
			this.name = name;
			this.xFraction = xFraction;
			this.yFraction = yFraction;
		}

		public String getName() {
			return name;
		}

		public double getXFraction() {
			return xFraction;
		}

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
			return name.hashCode();
		}
	}
	
	public Collection<Connection> getConnections();
	
	public Collection<Connection> getConnectionsToOrFromDest(Destination dest);
	
	public static class Connection {
		private final Destination start;
		private final Destination end;
		
		private final int numSegments;
		private final CardColor color;
		
		public Connection(Destination start, Destination end, CardColor color, int numSegments) {
			this.start = start;
			this.end = end;
			this.numSegments = numSegments;
			this.color = color;
		}
		
		public Destination getStart() {
			return start;
		}
		
		public Destination getEnd() {
			return end;
		}
		
		public CardColor getColor() {
			return color;
		}
		
		public int getNumSegments() {
			return numSegments;
		}
	}
}
