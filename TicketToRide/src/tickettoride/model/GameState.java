package tickettoride.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class GameState {

	private final MapData map;
	
	
	public GameState(MapData map) {
		this.map = map;
	}
	
	
	public MapData getMap() {
		return map;
	}
}
