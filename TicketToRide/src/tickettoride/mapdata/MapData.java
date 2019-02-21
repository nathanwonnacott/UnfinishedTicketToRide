package tickettoride.mapdata;

import java.util.Collection;

public interface MapData {

	
	public Collection<Destination> getDestinations();
	
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
}
