package tickettoride.model;

import java.io.FileNotFoundException;
import java.util.Collection;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.scene.image.Image;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.MapData.CardColor;
import tickettoride.players.Player;
import tickettoride.utilities.ImageLoader;

/**
 * This class keeps track of the entire state of the game (including the map, various decks, players
 * hands, etc.)
 * </br></br>
 * Students will need to modify this file, but I wouldn't recommend modifying it yet because there are still
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
	
	
	public GameState(Collection<Player> players, GameDefinition gameDefinition) {
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
	
	public ObservableBooleanValue getTransportationCardsDeckRemainingProperty() {
		return new SimpleBooleanProperty(true);
	}
	
	public ObservableBooleanValue getDestinationCardsDeckRemainingProperty() {
		return new SimpleBooleanProperty(true);
	}
	
	public ObservableList<CardColor> getFaceUpTransportationCards() {
		//TODO replace this with non-hardcoded values
		ObservableList<CardColor> list = FXCollections.observableArrayList();
		list.add(CardColor.ANY);
		list.add(CardColor.BLACK);
		list.add(CardColor.BLUE);
		list.add(CardColor.GREEN);
		return list;
	}
	
	/**
	 * Returns an unmodifiable, observable map from card color to the number of that color that the specified
	 * player has in its hand. If no cards exist in the players hand of a certain color (which will
	 * often be the case for multiple colors), then it is ok if the map either does not have an entry
	 * for that color or that the color maps to 0.
	 * @param player The player in whose hand we want.
	 * @return Unmodifiable, Observable map
	 */
	public ObservableMap<CardColor, Integer> getPlayersTransportationCardsHand(Player player) {
		return null;
	}
	
	
	/**
	 * Returns an observable set of destination cards that the specified
	 * player has in its hand. 
	 * @param player The player in whose hand we want.
	 * @return Unmodifiable, Observable set
	 */
	public ObservableSet<DestinationCard> getPlayersDestinationCards(Player player) {
		return null;
	}
	
	/**
	 * Returns an unmodifiable, observable map from players to the number of unused train pieces that the
	 * player has remaining.
	 * @return unmodifiable observable map
	 */
	public ObservableMap<Player, Integer> getNumTrainsRemaining() {
		return null;
	}

}
