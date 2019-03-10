package tickettoride;

import tickettoride.model.MapData;

public interface Player {

	public void executeMove(MapData mapData, Mover mover);
	
	public interface Mover {
		
	}
}
