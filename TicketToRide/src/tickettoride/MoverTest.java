package tickettoride;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.SimpleIntegerProperty;
import tickettoride.model.GameState;



class MoverTest {

	private Mover mover = null;
	private GameState mockedGameState = mock(GameState.class);
	
	@BeforeEach
	public void setup() {
		//This method will be run before each test.
		//TODO set mover to be an instance of your implementation of mover
		//This mover should be prepared to execute a non-first move of the game move.
		//The mover should use a reference to mockedGameState as it's game state.
		
		//Set up mocked methods for the game state.
		//A mocked object is a fake object used for testing purposes. We're using a
		//framework called mockito to instantiate a fake instance of GameState
		//and we just need to tell it what to return for the methods that we plan on
		//using in the tests.
		//The wiki page for the Mover implementation and testing phase explains in
		//more detail how mocked objects work.
		when(mockedGameState.getDestinationCardsDeckRemainingProperty())
			.thenReturn(new SimpleIntegerProperty(100));
	}
	
	private void setupForFirstMove() {
		//TODO set up whatever you need to indicate that this is the first move of the
		//game. You have a few choices of how you'll want to indicate that it's the first move.
		//You might choose to use a different implementation of mover for the first move, in which
		//case you'll want to set mover to that implementation. If you want to use a variable inside
		//your mover to indicate that it's the first move, then you'll want to set that variable
		//here. If you store the first move flag in your implementation of GameState, then you'll
		//want to mock out the GameState's getter method for that flag here.
		
		//Note that the "setup" method will be called before every test and it will be called before
		//this method is executed, so you don't have to repeat any setup that was already done
		//in that method.
		
	}
	
	@Test
	public void testGetDestinationCardSelectionMoveOnFirstTurn() {
		setupForFirstMove();
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetDestinationCardSelectionMoveOnNonFirstTurn() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumDestinationCardsThatCanBeDrawnOnFirstMove() {
		setupForFirstMove();
		assertEquals(3, mover.getNumDestinationCardsThatCanBeDrawn(),
				"On first move, should be able to draw 3 destination cards");
	}
	
	@Test
	public void testGetNumDestinationCardsThatCanBeDrawnOnNonFirstMove() {
		assertEquals(3, mover.getNumDestinationCardsThatCanBeDrawn(),
				"When more than 3 destination ards are remaining, should be able to draw 3 destination cards");
		
		for(int cardsRemaining = 3; cardsRemaining >=0 ; cardsRemaining--) {
			when(mockedGameState.getDestinationCardsDeckRemainingProperty())
				.thenReturn(new SimpleIntegerProperty(cardsRemaining));
			assertEquals(cardsRemaining, mover.getNumDestinationCardsThatCanBeDrawn(),
					"When only " + cardsRemaining + " destination cards are remaining, only " +
							cardsRemaining + " cards should be able to be drawn");
		}
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterTrainCardsAreDrawn() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterRouteIsClaimed() {
		fail("Not yet implemented");
	}

	@Test
	public void testDrawTransportationCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testCanDrawTransportationCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testBuildConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testCanBuildConnection() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTurnCompletedBinding() {
		fail("Not yet implemented");
	}

	//TODO make tests for DestinationCardSelectionMove class
//	@Test
//	public static interface DestinationCardSelectionMove {
//		/**
//		 * @return the destination cards that were drawn (this will normally be
//		 * 3 cards, but could be less if there are less than 3 cards remaining in the deck)
//		 */
//		public Set<DestinationCard> getDestinationCardOptions();
//		/**
//		 * Completes the move by choosing the destination cards to keep
//		 * @param cardsToKeep Set containing which of the destination cards out of those
//		 * returned by {@link #getDestinationCardOptions()} that will be kept
//		 * @throws IllegalMoveException if {@link #canSelectDestinationCards(Set)} would
//		 * return false
//		 */
//		public void selectDestinationCards(Set<DestinationCard> cardsToKeep);
//		/**
//		 * Indicates if the specified destination cards are a valid set of cards to complete.
//		 * The only case where this would return false would be if the set is the empty set or
//		 * if it includes any cards that were not returned by {@link #getDestinationCardOptions()}
//		 * @param cardsToKeep The cards that the player wishes to keep
//		 * @return true if the cards can be selected
//		 */
//		public boolean canSelectDestinationCards(Set<DestinationCard> cardsToKeep);
//	}
}
