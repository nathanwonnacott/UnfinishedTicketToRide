package tickettoride.mapdata;

import java.util.Collection;

import javafx.scene.image.Image;

public interface MapData {

	
	public Collection<Destination> getDestinations();
	
	public Collection<Connection> getConnections();
	
	public Image getBackgroundImage();
	
	public class Destination {
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
		
	}
	
	public class Connection {
		private final Destination start;
		private final Destination end;
		//TODO add in path segment information
		
		public Connection(Destination start, Destination end) {
			this.start = start;
			this.end = end;
		}
		
		public Destination getStart() {
			return start;
		}
		
		public Destination getEnd() {
			return end;
		}
	}
	
	public class PathSegment {
		
	}
}
