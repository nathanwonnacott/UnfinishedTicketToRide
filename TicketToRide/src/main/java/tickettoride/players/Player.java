package tickettoride.players;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import tickettoride.Mover;
import tickettoride.model.GameDefinition;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;

/**
 * Interface that all ticket to ride players must implement.
 * </br></br>
 * Students should implement a human player and one or more AI player concrete classes. If you
 * really want to dream big, you could also make remote players for playing across a network. Another
 * application might be to create a command line player for some manual testing.
 * </br></br>
 * It is important that students DO NOT modify this file, otherwise we can't have a tournament of the AIs. If
 * all players implement this interface, then we should be able to easily pit AI's created by different students 
 * against each other and see who wins.
 *
 */
public interface Player {
	
	/**
	 * Method to be called once at the beginning of the game to provide each player
	 * with a definition of the game. Many player implementations may not need to do
	 * anything in this method, but this can optionally be implemented if AI players
	 * need to prepare any strategies or anything based on the map, number of players, etc.
	 * @param gameDefinition the definition of the game that will be played
	 * @param numPlayers the number of players in this game
	 */
	public void initialize(GameDefinition gameDefinition, int numPlayers);

	/**
	 * Sets a reference to a read-only version of the player's transportation cards hand. The map should be created 
	 * via {@link FXCollections#unmodifiableObservableMap} so that the player can't cheat by modifying the map other than
	 * via a {@link Mover} object.
	 * </br></br>
	 * This method should be called during the initialization of the game with a reference to an empty map and then
	 * that map should reflect the player's hand throughout the game.
	 * @param unmodifiableTransportationCardsMap Unmodifiable view of this player's transportation card hand
	 * where the key is the color of transportation card and the value is the number of that card that the
	 * player has.
	 */
	public void setTransportationCardsMapView(ObservableMap<CardColor, Integer> unmodifiableTransportationCardsMap);

	/**
	 * Sets a reference to a read-only version of the player's destination cards. The set should be created 
	 * via {@link FXCollections#unmodifiableObservableSet} so that the player can't cheat by changing its cards other than
	 * via a {@link Mover} object.
	 * </br></br>
	 * This method should be called during the initialization of the game with a reference to an empty set and then
	 * that set should reflect the player's destination cards throughout the game.
	 * @param unmodifiableDestinationCards Unmodifiable view of this player's destination cards
	 */
	public void setDestinationCardsView(ObservableSet<DestinationCard> unmodifiableDestinationCards);
	
	/**
	 * Sets a reference to a read-only version of the map indicating how many train pieces each player has remaining.
	 * The map should be created via {@link FXCollections#unmodifiableObservableMap} so that the player can't cheat by 
	 * modifying the map other than via a {@link Mover} object.
	 * </br></br>
	 * This method should be called during the initialization of the game with a reference to a map that should
	 * have the initial number of trains for each player.
	 * @param unmodifiableTrainsRemaining Unmodifiable view of the number of trains remaining for each
	 * player where the key is the player and the value is the number of trains left for that player.
	 */
	public void setNumberOfTrainsRemainingView(ObservableMap<Player, Integer> unmodifiableTrainsRemaining);
	
	/**
	 * Sets a reference to a read-only list indicating the colors of the face-up transportation cards
	 * on the board. A null indicates a position which has no card (this will only occur once the deck
	 * of transportation cards has run out).
	 * The list should be created via {@link FXCollections#unmodifiableObservableList} so that the player can't cheat by 
	 * modifying the cards other than by drawing one via a {@link Mover} object.
	 * </br></br>
	 * This method should be called during the initialization of the game with a reference to a list that has 
	 * the initial face-up transportation cards.
	 * @param cards Unmodifiable view of the 5 face-up transportation cards, where index 0 represents the
	 * first face-up card and index 4 represents the last.
	 */
	public void setFaceUpTransportationCardsView(ObservableList<CardColor> cards);
	
	/**
	 * Performs a move on the specified map data using the mover object. Note that this method
	 * should not attempt to modify the state of the game other than through the mover object.
	 * This method should block until the move is complete. In other words, after calling this 
	 * method, a call to <pre>mover.getTurnCompletedBinding().get()</pre> should return false.
	 * @param mapData The current map
	 * @param mover
	 */
	public void executeMove(MapData mapData, Mover mover);
	
	/**
	 * @return a description of this player implementation type (for example, "Human Player",
	 * "Easy AI Player", "Advanced AI Player", etc.)
	 */
	public String getDescription();
	
}
