package tickettoride;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import tickettoride.Mover.DestinationCardSelectionMove;
import tickettoride.Mover.IllegalMoveException;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.GameState;
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Connection;
import tickettoride.model.MapData.Destination;
import tickettoride.players.Player;



class MoverTest {

	/**
	 * Mover object to be used in all the tests. Should get reset before each test by
	 * the {@link #setup()} and {@link #setupForFirstMove()} methods.
	 */
	private Mover mover = null;
	
	/**
	 * The player to be executing the moves (most likely won't need to mock out any of
	 * it's methods)
	 */
	private Player mockedPlayer = mock(Player.class);
	
	/**
	 * Mocked game state that should be referenced by {@link #mover} and will be used
	 * to verify interactions with game state objects.
	 */
	private GameState mockedGameState = mock(GameState.class);
	
	/**
	 * Mocked map data. The map will just have a few destinations/connections needed 
	 * for tests.
	 */
	private MapData mockedMapData = mock(MapData.class);

	/**
	 * First mocked destination to be part of the mocked map
	 */
	private Destination mockedDestination1 = mock(Destination.class);
	

	/**
	 * Second mocked destination to be part of the mocked map
	 */
	private Destination mockedDestination2 = mock(Destination.class);
	
	/**
	 * Connection between {@link #mockedDestination1} and {@link #mockedDestination2}
	 * In the initial setup, this connection will have a length of 1 and the color green.
	 */
	private Connection mockedConnection1to2 = mock(Connection.class);
	
	/**
	 * Property indicating the number of destination cards remaining in the game state
	 */
	private IntegerProperty destinationCardsRemainingProperty = new SimpleIntegerProperty(100);
	
	/** mocked destination card 1*/
	private DestinationCard destCard1 = mock(DestinationCard.class);
	/** mocked destination card 2*/
	private DestinationCard destCard2 = mock(DestinationCard.class);
	/** mocked destination card 3*/
	private DestinationCard destCard3 = mock(DestinationCard.class);
	
	/** Standard collection of 3 destination cards to be returned by default from game state deck */
	Collection<DestinationCard> expectedDestinationOptions =
			Arrays.asList(destCard1, destCard2, destCard3);
	
	/** Mover's turn complete property. It will be populated in the setup 
	 * method. Note that by populating it once in setup method rather than just
	 * fetching the value each time we want to check, we can ensure that the
	 * mover is updating the same boolean property each time.
	 */
	private ObservableBooleanValue turnCompleteBinding;
	
	/**
	 * Capturing argument for collections of destination cards. Note, Captors for
	 * generic types have to be declared as member variables like this in order
	 * to be typesafe.
	 */
	@Captor
	private ArgumentCaptor<Collection<DestinationCard>> destinationCardsCaptor;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		//This method will be run before each test.
		//TODO set mover to be an instance of your implementation of mover
		//This mover should be prepared to execute a non-first move of the game move.
		//The mover should use a reference to mockedGameState as it's game state.
		
		
		turnCompleteBinding = mover.getTurnCompletedBinding();
		//Set up mocked methods for the game state.
		//A mocked object is a fake object used for testing purposes. We're using a
		//framework called mockito to instantiate a fake instance of GameState
		//and we just need to tell it what to return for the methods that we plan on
		//using in the tests.
		//The wiki page for the Mover implementation and testing phase explains in
		//more detail how mocked objects work.
		when(mockedGameState.getMap()).thenReturn(mockedMapData);
		when(mockedGameState.getDestinationCardsDeckRemainingProperty())
			.thenReturn(destinationCardsRemainingProperty);
		when(mockedGameState.drawDestinationCards()).thenReturn(expectedDestinationOptions);
		
		//Set up mocked map
		when(mockedMapData.getDestinations())
			.thenReturn(Arrays.asList(mockedDestination1, mockedDestination2));
		
		when(mockedMapData.getConnections())
			.thenReturn(Collections.singleton(mockedConnection1to2));
		
		mockedMapData.getConnectionsToOrFromDest(same(mockedDestination1));
		when(mockedMapData.getConnectionsToOrFromDest(
				or(same(mockedDestination1), 
						same(mockedDestination2))))
			.thenReturn(Collections.singleton(mockedConnection1to2));
		
		when(mockedConnection1to2.getNumSegments()).thenReturn(1);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.GREEN);
		
		
		
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
		
		//Also note that if you create a new instance of a Mover here, then you'll also
		//need to update turnCompleteBinding
		
	}
	

	///////////////////////////////////////////////////////////////////////////////
	//	DESTINATION CARD DRAWING MOVES
	///////////////////////////////////////////////////////////////////////////////
	
	/** Utility method to execute a simple connection claiming method. This isn't
	 * intended to test the collection claiming method itself, but rather be used in 
	 * other test to ensure things like not being able to execute other move types
	 * after claiming a connection.
	 */
	private void claimConnection() {
		mover.buildConnection(mockedConnection1to2, Collections.singleton(CardColor.ANY));
	}
	
	@Test
	public void testCantSelect1DestinationCardsOnFirstTurn() {
		setupForFirstMove();
		
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		
		assertFalse(selectionMove.canSelectDestinationCards(Collections.singleton(destCard1)),
				"Should not be able to select 1 destination card on the first turn");
		
		try {
			selectionMove.selectDestinationCards(Collections.singleton(destCard1));
			fail("Selecting 1 destination card on the first turn should result in an exception");
		}
		catch(IllegalMoveException e) {}
		
		assertFalse(turnCompleteBinding.get(),
				"After failing to select 1 destination card, the turn should not be complete");
		
	}
	
	@ParameterizedTest(name = "Selecting {0} destination cards on the first turn")
	@ValueSource(ints = {2, 3})
	public void testSelectingDestinationCardsOnFirstTurn(int numCardsToSelect) {
		setupForFirstMove();
		
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		
		assertThat(
				"Destination card selection options should be the 3 returned from the deck",
				selectionMove.getDestinationCardOptions(),
				containsInAnyOrder(expectedDestinationOptions));
		
		
		testCardSelection(selectionMove, expectedDestinationOptions, numCardsToSelect);
	}
	
	@ParameterizedTest(name = "Selecting {0} destination card(s) on turn other than first turn")
	@ValueSource(ints = {1, 2, 3})
	public void testGetDestinationCardSelectionMoveOnNonFirstTurn(int numCardsToSelect) {
		
		when(mockedGameState.drawDestinationCards()).thenReturn(expectedDestinationOptions);
		
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		
		assertThat(
				"Destination card selection options should be the 3 returned from the deck",
				selectionMove.getDestinationCardOptions(),
				containsInAnyOrder(expectedDestinationOptions));
		
		
		testCardSelection(selectionMove, expectedDestinationOptions, numCardsToSelect);
	}
	
	/**
	 * Method asserts that cards can be selected, selects them, and verifies that
	 * the game state was modified appropriately
	 * @param move the DestinationCardSelectionMove object to use
	 * @param options avialable destination cards (selected cards will be chosen from this collection
	 * @param numToSelect number of cards to choose from that collection
	 */
	private void testCardSelection(DestinationCardSelectionMove move, Collection<DestinationCard> options, int numToSelect) {
		Set<DestinationCard> cardsToSelect = options
				.stream()
				.limit(numToSelect)
				.collect(Collectors.toSet());
		
		Set<DestinationCard> cardsNotSelected = options
												.stream()
												.filter(x -> ! cardsToSelect.contains(x))
												.collect(Collectors.toSet());
		
		assertFalse(turnCompleteBinding.get(),
				"Turn should not be complete prior to selecting the destination cards");
		
		move.selectDestinationCards(cardsToSelect);
		
		
		
		verify(mockedGameState).addDestinationCardsToPlayersHand(
									mockedPlayer, 
									destinationCardsCaptor.capture());
		
		//Note that it's possilbe that the mover implementation will call
		//addDestinationCardsToPlayersHand once will all of the values, or
		//call it once per value. The line below will get all of the calls
		//to that method and combine all of the arguments into one collection
		Collection<DestinationCard> allDestinationCardsDrawn =
				destinationCardsCaptor.getAllValues()
										.stream()
										.flatMap(x -> x.stream())
										.collect(Collectors.toList());
		assertThat(allDestinationCardsDrawn,
				containsInAnyOrder(cardsToSelect));
		
		//Now check that the non-selected cards were added to the bottom
		//of the deck
		verify(mockedGameState)
			.placeDestinationCardsAtBottomOfDeck(destinationCardsCaptor.capture());
		
		Collection<DestinationCard> allDestinationCardsAddedBackToDeck =
				destinationCardsCaptor.getAllValues()
										.stream()
										.flatMap(x -> x.stream())
										.collect(Collectors.toList());
		
		assertThat(allDestinationCardsAddedBackToDeck,
				containsInAnyOrder(cardsNotSelected));
		
		assertTrue(turnCompleteBinding.get(),
				"Turn should be complete after destination cards are selected");
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
			destinationCardsRemainingProperty.set(cardsRemaining);
			assertEquals(cardsRemaining, mover.getNumDestinationCardsThatCanBeDrawn(),
					"When only " + cardsRemaining + " destination cards are remaining, only " +
							cardsRemaining + " cards should be able to be drawn");
		}
	}
	
	@Test
	public void testCantSelect0DestinationCards() {
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		assertFalse(selectionMove.canSelectDestinationCards(Collections.emptySet()),
				"Should not be able to select 0 destination cards");
		
		try {
			selectionMove.selectDestinationCards(Collections.emptySet());
			fail("Selecting 0 destination cards should throw an exception");
		}
		catch(IllegalMoveException e) {} //do nothing
	}
	
	@Test
	public void testCantSelectCardThatWasNotOneOfOptions() {
		
		//Try to select 1 card that is present, plus a new card that was not one of the choices
		Set<DestinationCard> badSelections = Set.of(destCard1, mock(DestinationCard.class));
		
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		assertFalse(selectionMove.canSelectDestinationCards(badSelections),
				"Should not be able to select a destination card that was not drawn");
		
		try {
			selectionMove.selectDestinationCards(badSelections);
			fail("Selecting a destination card that is not one of the options should throw an exception");
		}
		catch(IllegalMoveException e) {} //do nothing
	}
	
	@ParameterizedTest(name = "Test can't draw destination cards after {0} train cards are drawn")
	@ValueSource(ints = {1, 2})
	public void testCantDrawDestinationCardsAfterTrainCardsAreDrawn(int numTrainCardsDrawn) {
		mover.drawTransportationCard(1);
		ensureCantDrawDestinationCards("draw destination cards after a train card has been drawn");
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterRouteIsClaimed() {
		claimConnection();
		ensureCantDrawDestinationCards("draw destination cards after claiming a connection");
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterBeginningADestinationCardDraw() {
		mover.getDestinationCardsSelectionMove();
		ensureCantDrawDestinationCards("draw destination cards after already starting a destination draw process");
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterCompletingADestinationCardDraw() {
		mover.getDestinationCardsSelectionMove().selectDestinationCards(Collections.singleton(destCard1));
		ensureCantDrawDestinationCards("draw destination cards after already completing a destination card draw");
	}
	
	private void ensureCantDrawDestinationCards(String attemptedMoveDescription) {
		assertEquals(0, mover.getNumDestinationCardsThatCanBeDrawn(),
				"Cannot " + attemptedMoveDescription);
		
		try {
			mover.getDestinationCardsSelectionMove();
			fail("Illegal move exception should have been thrown when attempting"
					+ " to " + attemptedMoveDescription);
		}
		catch(IllegalMoveException e) {
			//Do nothing
		}
	}
	
	@Test
	public void testCantModifyDestinationCardDrawingOptions() {
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		Collection<DestinationCard> options = selectionMove.getDestinationCardOptions();
		
		//Attempt to add another card to the options. It doesn't matter if the
		//implementing class throws an exception, or just does nothing, but we
		//must ensure that it doesn't change what we're actually allowed to do
		DestinationCard cheaterCard = mock(DestinationCard.class);
		try {
			options.add(cheaterCard);
		}
		catch(Throwable t) {}
		assertFalse(selectionMove.canSelectDestinationCards(Collections.singleton(cheaterCard)),
				"Should not be able to select destination card that was illegally inserted into options set");
		
		try {
			selectionMove.selectDestinationCards(Collections.singleton(cheaterCard));
			fail("Should throw exception when trying to select destination card that was illegally inserted into options set");
		}
		catch(IllegalMoveException e) {};
	}
	
	///////////////////////////////////////////////////////////////////////////////
	//	TRANSPORTATION CARD DRAWING MOVES
	///////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDrawTransportationCard() {
		fail("Not yet implemented");
	}

	@Test
	public void testCanDrawTransportationCard() {
		fail("Not yet implemented");
	}
	

	///////////////////////////////////////////////////////////////////////////////
	//	ROUTE CLAIMING MOVES
	///////////////////////////////////////////////////////////////////////////////

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

}
