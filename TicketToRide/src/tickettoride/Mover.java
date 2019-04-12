package tickettoride;

import java.util.Collection;
import java.util.Set;

import javafx.beans.binding.BooleanBinding;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Connection;

/**
 * Object that validates and performs all possible moves in the game. An implementation of this
 * will be passed to players (both human and AI) to modify the game state based on the players desired
 * move. Students should implement this interface with an object that can view and modify the entire state
 * of the game.
 * </br></br>
 * For each possible type of move, there is a method to check if the method is allowed (useful for presenting 
 * available options in the GUI, and for checking possible moves in an AI). There is also a method separate method
 * for each move type which actually execute the move. Each of these will throw an {@link IllegalMoveException} if
 * the move is not allowed.
 * </br></br>
 * This interface should not be modified by the student since it will be used by all players and we need to 
 * keep compatibility between multiple players implemented by different people.
 * @author nate
 *
 */
public interface Mover {
	
	/**
	 * Returns a {@link DestinationCardSelectionMove} object which can be used to select
	 * destination cards. The turn will not be completed (i.e. the turn completed binding will
	 * result in false) until one or more destination cards are selected from the returned
	 * DestinationCardSelectionMove object
	 * @return {@link DestinationCardSelectionMove}
	 * @throws {@link IllegalMoveException} if {@link #canSelectDestinationCards()} would return
	 * false
	 */
	public DestinationCardSelectionMove getDestinationCardsSelectionMove();
	/**
	 * Indicates if the player may select destination cards. This will return true unless there
	 * are no remaining destination cards in the deck, if the player has already begun or completed 
	 * another move this turn, or if the player has already draw destination cards this turn.
	 * @return true if selecting a destination card is a valid current move
	 */
	public boolean canSelectDestinationCards();
	
	/**
	 * Draws the transportation card from the specified draw location. If this drawing a {@link CardColor#ANY}
	 * card that is face up, then the first call to this will end the turn. Otherwise, the second call will.
	 * @param cardNumber The card to draw. If it is a number 0-4, then it will draw the face-up transportation
	 * card at that index. If it is 5, then it will draw from the deck
	 * @return The color of the drawn card
	 * @throws {@link IllegalMoveException} if {@link #canDrawTransportationCard(int)} would return false
	 */
	public CardColor drawTransportationCard(int cardNumber);
	
	/**
	 * Indicates if the play can draw the specified card. Conditions where this will return false are:
	 * <ul>
	 * 	<li>This is the first turn of the game</li>
	 * 	<li>The number specified is not between 0 and 5 inclusive</li>
	 *  <li>There is not card at the specified position (we've run out of cards in the deck)</li>
	 *  <li>The player has already drawn a face-up {@link CardColor#ANY} card, or drawn 2 cards</li>
	 *  <li>The player has begun or completed another move this turn</li>
	 * </ul>
	 * @param cardNumber The card to draw. If it is a number 0-4, then it will draw the face-up transportation
	 * card at that index. If it is 5, then it will draw from the deck
	 * @return true if the card can be drawn.
	 */
	public boolean canDrawTransportationCard(int cardNumber);
	
	/**
	 * Builds/claims the specified connection, using the specified cards.
	 * @param connectionToBuild the connection between two adjacent destinations to build/claim
	 * @param cardsToUse The cards that should be discarded in order to complete the connection
	 * @throws IllegalMoveException if {@link #canBuildConnection(Connection, Collection)} would have
	 * returned false.
	 */
	public void buildConnection(Connection connectionToBuild, Collection<CardColor> cardsToUse);
	
	/**
	 * Determines if the player can build/claim the specified connection using the specified cards. Conditions
	 * that could result in this returning false are:
	 * <ul>
	 * 	<li>The specified cards are not sufficient to build the connection</li>
	 *  <li>The player does not actually have the specified cards</li>
	 *  <li>The connection is already claimed</li>
	 *  <li>The connection is one of multiple connections between two cities and either:
	 *  	<ul>
	 *  		<li>The current player already owns one of the connections</li>
	 *  		<li>This is only a two player game</li>
	 *  	</ul>
	 *  </li>
	 *  <li>The player does not have enough train pieces remaining to build the connection</li>
	 *  <li>The specified connection does not actually exist in the map</li>
	 *  <li>The player has already begun/completed another move this turn</li>
	 * </ul>
	 * @param connectionToBuild
	 * @param cardsToUse
	 * @return true if the connection can be built
	 */
	public boolean canBuildConnection(Connection connectionToBuild, Collection<CardColor> cardsToUse);
	
	/**
	 * @return Boolean binding indicating whether or not this turn has been completed.
	 */
	public BooleanBinding getTurnCompletedBinding();
	
	/**
	 * This is basically another mover object that finishes out the destination card selection move.
	 * This allows the player to view the 3 destination cards that were drawn and choose which one(s)
	 * to keep.
	 * @author nate
	 *
	 */
	public static interface DestinationCardSelectionMove {
		/**
		 * @return the destination cards that were drawn (this will normally be
		 * 3 cards, but could be less if there are less than 3 cards remaining in the deck)
		 */
		public Set<DestinationCard> getDestinationCardOptions();
		/**
		 * Completes the move by choosing the destination cards to keep
		 * @param cardsToKeep Set containing which of the destination cards out of those
		 * returned by {@link #getDestinationCardOptions()} that will be kept
		 * @throws IllegalMoveException if {@link #canSelectDestinationCards(Set)} would
		 * return false
		 */
		public void selectDestinationCards(Set<DestinationCard> cardsToKeep);
		/**
		 * Indicates if the specified destination cards are a valid set of cards to complete.
		 * The only case where this would return false would be if the set is the empty set or
		 * if it includes any cards that were not returned by {@link #getDestinationCardOptions()}
		 * @param cardsToKeep The cards that the player wishes to keep
		 * @return true if the cards can be selected
		 */
		public boolean canSelectDestinationCards(Set<DestinationCard> cardsToKeep);
	}
	
	/**
	 * Exception to to be thrown when a player attempts to execute a move that is not allowed
	 * @author nate
	 */
	@SuppressWarnings("serial")
	public static class IllegalMoveException extends RuntimeException {
		public IllegalMoveException(String message) { 
			super(message);
		}
	}
}
