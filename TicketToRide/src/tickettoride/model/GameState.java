package tickettoride.model;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import tickettoride.model.MapData.CardColor;

public class GameState {

	private final MapData map;
	
	
	public GameState(MapData map) {
		this.map = map;
	}
	
	
	public MapData getMap() {
		return map;
	}
	
	public List<ObjectProperty<CardColor>> drawCards() {
		//TODO
		return Arrays.asList(new SimpleObjectProperty<>(),
				new SimpleObjectProperty<>(CardColor.ANY),
				new SimpleObjectProperty<>(CardColor.BLACK),
				new SimpleObjectProperty<>(CardColor.BLUE),
				new SimpleObjectProperty<>(CardColor.GREEN));
	}
}
