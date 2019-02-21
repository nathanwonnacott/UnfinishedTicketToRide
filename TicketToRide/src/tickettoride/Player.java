package tickettoride;

import tickettoride.mapdata.MapData;

public interface Player {

	public void executeMove(MapData mapData, Mover mover);
	
	public interface Mover {
		
	}
}
