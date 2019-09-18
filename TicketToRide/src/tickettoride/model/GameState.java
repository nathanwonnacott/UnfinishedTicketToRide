package tickettoride.model;

import java.io.FileNotFoundException;
import java.util.Collection;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableIntegerValue;
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
	
	/**
	 * @return the players in the game
	 */
	public Collection<Player> getPlayers() {
		//TODO
		return null;
	}
	
	public ObservableBooleanValue getTransportationCardsDeckRemainingProperty() {
		return new SimpleBooleanProperty(true);
	}
	
	public ObservableIntegerValue getDestinationCardsDeckRemainingProperty() {
		return new SimpleIntegerProperty(100);
	}
	
	/**
	 * Removes and returns 3 destination cards from the destination card
	 * deck (or if less than 3 are left, the remaining cards are returned)
	 * @return
	 */
	public Collection<DestinationCard> drawDestinationCards() {
		return null;
	}
	
	/**
	 * Removes and returns the transportation card at the specified index.
	 * If the index indicates a face-up transportation card, then the card at
	 * that index will be replaced by a card from the deck (unless the deck is
	 * empty).
	 * @param cardIndex the index of the card to draw (5 means from the deck)
	 * @return the color of the card that was drawn
	 */
	public CardColor drawTransportationCard(int cardIndex) {
		return CardColor.BLUE;
	}
	
	/**
	 * Places the specified destination cards at the bottom of the deck
	 * @param cards cards to be placed at the bottom of the deck
	 */
	public void placeDestinationCardsAtBottomOfDeck(Collection<DestinationCard> cards) {
		//TODO
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
	 * Adds the specified transportation to the specified player's hand
	 * @param player the play to whose hand the card should be added
	 * @param color the color of the destination card to be added to the player's hand
	 */
	public void addTransportationCardToPlayersHand(Player player, CardColor color) {
		
	}
	
	
	/**
	 * Removes the specified cards from the specified player's hand
	 * @param player
	 * @param cardsToRemove
	 */
	public void removeTransportationCardsFromPlayersHand(Player player, Collection<CardColor> cardsToRemove) {
		
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
	 * Adds the specified destination cards to the specified player's hand
	 * @param player the play to whose hand the cards should be added
	 * @param cards the destination cards to be added to the player's hand
	 */
	public void addDestinationCardsToPlayersHand(Player player, Collection<DestinationCard> cards) {
		//TODO
	}
	
	/**
	 * Returns an unmodifiable, observable map from players to the number of unused train pieces that the
	 * player has remaining.
	 * @return unmodifiable observable map
	 */
	public ObservableMap<Player, Integer> getNumTrainsRemaining() {
		return null;
	}
	
	/**
	 * Uses the specified number of train pieces from the player (decrements
	 * that players number of unused train pieces).
	 * @param player
	 * @param numTrains
	 */
	public void useTrains(Player player, int numTrains) {
		
	}

}
