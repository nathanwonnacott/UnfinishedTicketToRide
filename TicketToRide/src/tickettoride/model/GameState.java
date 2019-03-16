package tickettoride.model;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import tickettoride.model.MapData.CardColor;
import tickettoride.utilities.ImageLoader;

public class GameState {

	private final GameDefinition gameDefinition;
	private final Image background;
	
	
	public GameState(GameDefinition gameDefinition) {
		this.gameDefinition = gameDefinition;
		
		Image nonFinalBackgroundImage = null;
		try {
			nonFinalBackgroundImage = ImageLoader.load(gameDefinition.getBackgroundImage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.background = nonFinalBackgroundImage;
	}
	
	
	public MapData getMap() {
		return gameDefinition.getMapData();
	}
	
	public Image getBackgroundImage() {
		return background;
	}
	
	public GameDefinition gameDefinition() {
		return gameDefinition;
	}
	
	public List<ObjectProperty<CardColor>> getFaceUpTransportationCardProperties() {
		//TODO replace with properties representing each face up transportation card
		return Arrays.asList(new SimpleObjectProperty<>(),
				new SimpleObjectProperty<>(CardColor.ANY),
				new SimpleObjectProperty<>(CardColor.BLACK),
				new SimpleObjectProperty<>(CardColor.BLUE),
				new SimpleObjectProperty<>(CardColor.GREEN));
	}
}
