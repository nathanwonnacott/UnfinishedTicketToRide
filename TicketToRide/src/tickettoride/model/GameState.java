package tickettoride.model;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import tickettoride.model.MapData.CardColor;
import tickettoride.utilities.ImageLoader;

/**
 * This class keeps track of the entire state of the game (including the map, various decks, players
 * hands, etc.)
 * </br></br>
 * Students will need to modify this file, but I wouldn't recomment modifying it yet because there are still
 * some more changes that I need to make to it.
 * </br></br>
 * TODO for Nate: add more documentation to this class once it's a little more solidified.
 * @author nate
 *
 */
public class GameState {

	private final GameDefinition gameDefinition;
	private final MapData mapData;
	private final Image background;
	
	
	public GameState(GameDefinition gameDefinition) {
		this.gameDefinition = gameDefinition;
		mapData = this.gameDefinition.getInitialMapData();
		
		Image nonFinalBackgroundImage = null;
		try {
			nonFinalBackgroundImage = ImageLoader.load(gameDefinition.getBackgroundImage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.background = nonFinalBackgroundImage;
	}
	
	
	public MapData getMap() {
		return mapData;
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
