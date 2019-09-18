package tickettoride;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Pair;
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
	
	/** Observable list of face up transportation card colors (to be returned by the mocked game state).
	 * Note that there is no significance about the initial actual values of the cards except that there
	 * are no wilds. */
	private ObservableList<CardColor> faceUpTransportationCards = FXCollections.observableArrayList(
			CardColor.BLUE,
			CardColor.GREEN,
			CardColor.YELLOW,
			CardColor.YELLOW,
			CardColor.PURPLE
			);
	
	/** Color of the transportation card on top of the deck */
	private CardColor transportationCardDeckTopColor = CardColor.RED;
	
	/** Observable boolean indicating if cards are remaining in the transportation card deck.
	 * To be returned by the mocked game state */
	private SimpleBooleanProperty transportationCardDeckRemaining = new SimpleBooleanProperty(true);
	
	/**
	 * Capturing argument for selected destination cards. Note, Captors for
	 * generic types have to be declared as member variables like this in order
	 * to be typesafe.
	 */
	@Captor
	private ArgumentCaptor<Collection<DestinationCard>> selectedDestinationCardsCaptor;
	

	/**
	 * Capturing argument for destination cards that were returned to the bottom of
	 * the deck. Note, Captors for generic types have to be declared as member variables 
	 * like this in order to be typesafe.
	 */
	@Captor
	private ArgumentCaptor<Collection<DestinationCard>> destinationCardsReturnedToDeckCaptor;
	
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
		when(mockedGameState.getPlayers())
			.thenReturn(List.of(mockedPlayer, mock(Player.class), mock(Player.class)));
		when(mockedGameState.getMap()).thenReturn(mockedMapData);
		when(mockedGameState.getDestinationCardsDeckRemainingProperty())
			.thenReturn(destinationCardsRemainingProperty);
		when(mockedGameState.drawDestinationCards()).thenReturn(expectedDestinationOptions);
		when(mockedGameState.getFaceUpTransportationCards()).thenReturn(faceUpTransportationCards);
		when(mockedGameState.getTransportationCardsDeckRemainingProperty()).thenReturn(transportationCardDeckRemaining);
		
		when(mockedGameState.drawTransportationCard(anyInt())).thenAnswer(invocation -> {
			Integer index = invocation.getArgumentAt(0, Integer.class);
			if(index == 5) {
				return transportationCardDeckTopColor;
			}
			else {
				return faceUpTransportationCards.get(index);
			}
			
		});
		
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
	
	
	/**
	 * Sets the map connections to only contain a double connection from {@link #mockedDestination1} to
	 * {@link #mockedDestination2}. Any other connections will be removed from the map
	 * @param color1 The color of the first connection
	 * @param color2 the color of the second connection
	 * @param num the number of path segments in the connection
	 * @return pair of the newly created connection mocked objects
	 */
	private Pair<Connection, Connection> setupDoubleConnectionFrom1to2(CardColor color1, CardColor color2, int num) {
		
		Connection conn1 = mock(Connection.class);
		Connection conn2 = mock(Connection.class);
		
		when(mockedMapData.getConnections())
			.thenReturn(Set.of(conn1, conn2));
		
		when(mockedMapData.getConnectionsToOrFromDest(
				or(same(mockedDestination1), 
						same(mockedDestination2))))
			.thenReturn(Set.of(conn1, conn2));
		
		
		Stream.of(conn1, conn2).forEach(c -> when(c.getNumSegments()).thenReturn(num));
		when(conn1.getColor()).thenReturn(color1);
		when(conn2.getColor()).thenReturn(color2);
		return new Pair<>(conn1, conn2);

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
				containsInAnyOrder(expectedDestinationOptions.toArray()));
		
		
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
				containsInAnyOrder(expectedDestinationOptions.toArray()));
		
		
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
									same(mockedPlayer), 
									selectedDestinationCardsCaptor.capture());
		
		//Note that it's possible that the mover implementation will call
		//addDestinationCardsToPlayersHand once will all of the values, or
		//call it once per value. The line below will get all of the calls
		//to that method and combine all of the arguments into one collection
		Collection<DestinationCard> allDestinationCardsDrawn =
				selectedDestinationCardsCaptor.getAllValues()
										.stream()
										.flatMap(x -> x.stream())
										.collect(Collectors.toList());
		assertThat(allDestinationCardsDrawn,
				containsInAnyOrder(cardsToSelect.toArray()));
		
		//Now check that the non-selected cards were added to the bottom
		//of the deck
		//Note that we allow as few as 0 invocations here to handle the case where all of the
		//cards were selected. We'll be checking the arguments via the captor though, so if
		//an invocation was needed here but it was never invoked, then we'll still catch the
		//error below after we inspect the arguments
		verify(mockedGameState, atLeast(0))
			.placeDestinationCardsAtBottomOfDeck(destinationCardsReturnedToDeckCaptor.capture());
		
		Collection<DestinationCard> allDestinationCardsAddedBackToDeck =
				destinationCardsReturnedToDeckCaptor.getAllValues()
										.stream()
										.flatMap(x -> x.stream())
										.collect(Collectors.toList());
		
		assertThat(allDestinationCardsAddedBackToDeck,
				containsInAnyOrder(cardsNotSelected.toArray()));
		
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

	@ParameterizedTest(name = "Test can't draw transportation card # {0} on first move")
	@ValueSource(ints = {0, 5})
	public void testCantDrawTransportationCardOnFirstTurn(int cardIndex) {
		setupForFirstMove();
		assertFalse(mover.canDrawTransportationCard(cardIndex),
				"Can't draw transportation card on first turn");
		
		try {
			mover.drawTransportationCard(cardIndex);
			fail("Should throw exception when trying to draw a transportation card on firtst turn");
		}
		catch(IllegalMoveException e) {};
		
	}

	@ParameterizedTest(name = "Test can't draw transportation card # {0} after beginning a destination card move")
	@ValueSource(ints = {0, 5})
	public void testCantDrawTransportationCardAfterDrawingDestinationCard(int cardIndex) {
		mover.getDestinationCardsSelectionMove();
		
		assertCantDrawTransportationCard("after beginning a destination card draw move", cardIndex);
	}
	
	@ParameterizedTest(name = "Test can't draw transportation card # {0} after claiming a route")
	@ValueSource(ints = {0, 5})
	public void testCantDrawTransportationCardAfterClaimingRoute(int cardIndex) {
		this.claimConnection();
		
		assertCantDrawTransportationCard("after claiming a route", cardIndex);
	}
	
	@ParameterizedTest(name = "Test can't draw transportation card # {0} after already drawing 2 transportation cards")
	@ValueSource(ints = {0, 5})
	public void testCantDrawTransportationCardAfterDrawingTwoTransportationCards(int cardIndex) {
		mover.drawTransportationCard(2);
		mover.drawTransportationCard(5);
		
		assertCantDrawTransportationCard("after already drawing 2 transportation cards", cardIndex);
	}
	
	@ParameterizedTest(name = "Test can't draw transportation card # {0} when there is not card there")
	@ValueSource(ints = {0, 1, 2, 3, 4, 5})
	public void testCantDrawCardsWhenCardsAreDepleted(int cardIndex) {
		if(cardIndex < 5) {
			faceUpTransportationCards.set(cardIndex, null);
			assertCantDrawTransportationCard("when the card is not present", cardIndex);
		}
		else {
			transportationCardDeckRemaining.set(false);
			assertCantDrawTransportationCard("when the deck is depleted", cardIndex);
		}
	}
	
	@Test
	public void testDrawingFaceUpWildAsFirstCard() {
		final int cardIndex = 3;
		faceUpTransportationCards.set(cardIndex, CardColor.ANY);
		
		mover.drawTransportationCard(cardIndex);
		
		assertCantDrawTransportationCard("after drawing a face up wild card", 2);
	}
	
	/**
	 * Asserts that {@link Mover#canDrawTransportationCard} returns false and
	 * {@link Mover#drawTransportationCard} throws an exception when trying
	 * to draw the specified card.
	 * @param description description of the state of the game which should
	 * cause the card to not be drawalbe (for showing up in assert statements)
	 * @param cardIndex the index (0-5) of the card to draw.
	 */
	private void assertCantDrawTransportationCard(String description, int cardIndex) {
		assertFalse(mover.canDrawTransportationCard(cardIndex),
				"Can't draw transportation card " + cardIndex + " " + description);
		
		try {
			mover.drawTransportationCard(cardIndex);
			fail("Should throw exception when trying to draw a transportation card " + cardIndex + " "
					+ description);
		}
		catch(IllegalMoveException e) {};
	}
	
	
	@ParameterizedTest(name = "Test draw wild from deck and then draw index {0}")
	@ValueSource(ints = {0, 5})
	public void testDrawingWildFromDeckAndSecondCard(int cardIndex) {
		
		//Draw wild from deck
		when(mockedGameState.drawTransportationCard(5)).thenReturn(CardColor.ANY);
		mover.drawTransportationCard(5);
		
		//Now make sure we can draw another card
		assertTrue(mover.canDrawTransportationCard(cardIndex), 
				"Should be able to draw second card after drawing a wild from the deck");

		mover.drawTransportationCard(cardIndex);
	}
	
	@ParameterizedTest(name = "Draw 2 non-wild cards. Indices: {0}, {1}")
	@MethodSource("draw2CardsArgumentProvider")
	public void testDraw2NonWildTransportationCards(int firstCardIndex, int secondCardIndex) {
		
		assertFalse(turnCompleteBinding.getValue(),
				"Turn should not be complete prior to doing anything");
		
		
		CardColor card1 = testSingleTransportationCardDraw(firstCardIndex);
		assertFalse(turnCompleteBinding.getValue(),
				"Turn should not be complete after only drawing 1 transportation card");
		
		//Replace whatever card with a different card just to make sure that the second
		//draw on duplicate indices is resulting in a new card color (which may or may not
		//actually be the same color)
		if(firstCardIndex < 5) {
			faceUpTransportationCards.set(firstCardIndex, CardColor.GREEN);
		}
		else {
			transportationCardDeckTopColor = CardColor.GREEN;
		}
		
		CardColor card2 = testSingleTransportationCardDraw(secondCardIndex);
		assertTrue(turnCompleteBinding.getValue(),
				"Turn should be complete after drawing 2 destination cards");
		
		//Verify all interactions with the game state
		
		if(firstCardIndex == secondCardIndex) {
			verify(mockedGameState, times(2)).drawTransportationCard(firstCardIndex);
		}
		else {
			verify(mockedGameState).drawTransportationCard(firstCardIndex);
			verify(mockedGameState).drawTransportationCard(secondCardIndex);
		}
		
		if(card1 == card2) {
			verify(mockedGameState, times(2)).addTransportationCardToPlayersHand(mockedPlayer, card1);
		}
		else {
			verify(mockedGameState).addTransportationCardToPlayersHand(mockedPlayer, card1);
			verify(mockedGameState).addTransportationCardToPlayersHand(mockedPlayer, card2);
		}
		
	}
	
	/** utility method to test a single successful transportation card draw.
	 * @return the color of the card that was drawn*/
	private CardColor testSingleTransportationCardDraw(int index) {
		
		assertTrue(mover.canDrawTransportationCard(index),
				"Drawing card at index " + index + " should be allowed");
		
		CardColor expectedColor = index < 5 ?
						faceUpTransportationCards.get(index) :
							transportationCardDeckTopColor;
						
		assertEquals(expectedColor, mover.drawTransportationCard(index));
		
		return expectedColor;
	}
	
	/** Provides arguments used in {@link #testDraw2NonWildTransportationCards} */
	private static Stream<Arguments> draw2CardsArgumentProvider() {
		return Stream.of(
				//Try each face up card
				Arguments.of(0,1),
				Arguments.of(2,3),
				Arguments.of(3,4),
				//Check face up and drawing from the deck combos
				Arguments.of(1,5),
				Arguments.of(5,1),
				//Check drawing from the same spot twice. Once with
				//green (which is significant because the test replaces
				//the card with green, so we'll get the same color twice
				//in a row) and one with a different color.
				Arguments.of(1,1),
				Arguments.of(3,3),
				//Check drawing both from the deck
				Arguments.of(5,5)
				);
	}
	
	

	///////////////////////////////////////////////////////////////////////////////
	//	ROUTE CLAIMING MOVES
	///////////////////////////////////////////////////////////////////////////////

	@ParameterizedTest(name = "Build connection of length {0}, color {1}")
	@MethodSource("testBuildNonGreyConnectionArgumentProvider")
	public void testBuildNonGreyConnection(int num, CardColor color) {
		
		Collection<CardColor> cardsToUse = Collections.nCopies(num, color);
		
		when(mockedConnection1to2.getNumSegments()).thenReturn(num);
		when(mockedConnection1to2.getColor()).thenReturn(color);
		
		checkBuildConnection(mockedConnection1to2, cardsToUse);
	}
	
	/** Provides arguments used in {@link #testBuildNonGreyConnection} */
	private static Stream<Arguments> testBuildNonGreyConnectionArgumentProvider() {
		//We'll do every combination of length 1-5 connections with every color
		//except for ANY
		return numAndColorArgumentProvider()
					.filter(a -> a.get()[1] != CardColor.ANY);
	}
	
	@ParameterizedTest(name = "Build connection of length {0}, with cards colored {1}")
	@MethodSource("numAndColorArgumentProvider")
	public void testBuildGreyConnection(int num, CardColor color) {
		Collection<CardColor> cardsToUse = Collections.nCopies(num, color);

		when(mockedConnection1to2.getNumSegments()).thenReturn(num);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.ANY);
		
		checkBuildConnection(mockedConnection1to2, cardsToUse);
	}
	
	/** Provides arguments consisting of combinations of connection lengths and colors */
	private static Stream<Arguments> numAndColorArgumentProvider() {
		//We'll do every combination of length 1-3 connections with every color
		return IntStream.range(1, 4)
				.mapToObj(i->i)
				.flatMap(i ->
						Arrays.stream(CardColor.values())
								.map(c -> Arguments.of(i, c)));
	}
	
	/**
	 * Tests that you can build on the "first" connection in a double connection. Note that
	 * double connections don't really have a true concept of first and second, but we just
	 * want to make sure that it works on each.
	 */
	@Test
	public void testBuildOnFirstDoubleConnection() {
		Pair<Connection, Connection> connections = 
				this.setupDoubleConnectionFrom1to2(CardColor.BLUE, CardColor.ORANGE, 3);
		
		checkBuildConnection(connections.getKey(), Collections.nCopies(3, CardColor.BLUE));
	}
	
	/**
	 * Tests that you can build on the "second" connection in a double connection. Note that
	 * double connections don't really have a true concept of first and second, but we just
	 * want to make sure that it works on each.
	 */
	@Test
	public void testBuildOnSecondDoubleConnection() {
		Pair<Connection, Connection> connections = 
				this.setupDoubleConnectionFrom1to2(CardColor.BLUE, CardColor.ORANGE, 3);
		
		checkBuildConnection(connections.getValue(), Collections.nCopies(3, CardColor.ORANGE));
	}
	
	@Test
	public void testBuildConnectionWithWilds() {

		when(mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.BLUE);
		
		checkBuildConnection(mockedConnection1to2,
				List.of(CardColor.BLUE, CardColor.ANY, CardColor.ANY));
	}
	
	
	/**
	 * Utility method used by all tests that check for a successful building of a connection
	 * @param connectionToBuild
	 * @param cardsToUse
	 */
	private void checkBuildConnection(Connection connectionToBuild, Collection<CardColor> cardsToUse) {
		
		//Make mocked game state indicate that the player has enough cards in his hand
		ObservableMap<CardColor, Integer> playersHand = 
				convertCardsCollectionToCardCountsMap(cardsToUse);
		
		
		
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(playersHand);
		
		mover.buildConnection(connectionToBuild, cardsToUse);
		
		verify(connectionToBuild).claim(mockedPlayer);
		verify(mockedGameState).useTrains(mockedPlayer, connectionToBuild.getNumSegments());
		verify(mockedGameState).removeTransportationCardsFromPlayersHand(mockedPlayer, cardsToUse);
		
		
	}

	@Test
	public void testCanBuildStandardConnection() {
		when(mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.GREEN);
		
		assertTrue(mover.canBuildConnection(mockedConnection1to2, 
											Collections.nCopies(3, CardColor.GREEN)));
	}
	
	@Test
	public void testCanBuildGreyConnection() {
		when(mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.ANY);
		
		assertTrue(mover.canBuildConnection(mockedConnection1to2, 
											Collections.nCopies(3, CardColor.PURPLE)));
	}
	
	@Test
	public void testCanBuildGreyConnectionWithPartialWildCards() {
		when(mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.ANY);
		
		assertTrue(mover.canBuildConnection(mockedConnection1to2, 
											List.of(CardColor.ANY, CardColor.BLUE, CardColor.ANY)));
	}
	
	@Test
	public void testCanBuildConnectionWithWild() {
		when(mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.BLACK);
		
		assertTrue(mover.canBuildConnection(mockedConnection1to2, 
											List.of(CardColor.ANY, CardColor.BLUE, CardColor.ANY)));
	}
	


	@Test
	public void testCantBuildConnectionWithWrongColor() {
		testCantBuildConnectionAfterSettingUpState(4, CardColor.WHITE, Collections.nCopies(4,  CardColor.ORANGE));
	}


	@Test
	public void testCantBuildGreyConnectionWithMismatchedCards() {
		testCantBuildConnectionAfterSettingUpState(2, CardColor.ANY, List.of(CardColor.GREEN, CardColor.BLUE));
	}

	@Test
	public void testCantBuildConnectionWithTooFewCards() {
		testCantBuildConnectionAfterSettingUpState(4, CardColor.PURPLE, Collections.nCopies(3,  CardColor.PURPLE));
	}
	

	@Test
	public void testCantBuildConnectionThatIsAlreadyClaimed() {
		when(mockedConnection1to2.getNumSegments()).thenReturn(2);
		when(mockedConnection1to2.getColor()).thenReturn(CardColor.RED);
		when(mockedConnection1to2.getOwnerProperty())
			.thenReturn(new SimpleObjectProperty<>(mock(Player.class)));
		testCantBuildConnection(mockedConnection1to2, Collections.nCopies(2,  CardColor.RED));
	}

	@Test
	public void testCantBuildConnectionOnSecondPathOfDoubleIn2PlayerGame() {
		//Set to only 2 players
		Player otherPlayer = mock(Player.class);
		when(mockedGameState.getPlayers())
			.thenReturn(List.of(mockedPlayer, otherPlayer));
		
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(FXCollections.observableMap(Map.of(CardColor.ORANGE, 2)));
		
		Pair<Connection, Connection> connections = 
				this.setupDoubleConnectionFrom1to2(CardColor.BLUE, CardColor.ORANGE, 2);
		
		//Make the blue one already claimed
		when(connections.getKey().getOwnerProperty()).thenReturn(new SimpleObjectProperty<>(otherPlayer));
		
		testCantBuildConnection(connections.getValue(), Collections.nCopies(2,  CardColor.ORANGE));
	}
	
	@Test
	public void testCantClaimBothConnectionsOnDoublePath() {
		
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(FXCollections.observableMap(Map.of(CardColor.ORANGE, 2)));
		
		Pair<Connection, Connection> connections = 
				this.setupDoubleConnectionFrom1to2(CardColor.BLUE, CardColor.ORANGE, 2);
		
		//Make the blue one already claimed by this same player
		when(connections.getKey().getOwnerProperty()).thenReturn(new SimpleObjectProperty<>(mockedPlayer));
		
		testCantBuildConnection(connections.getValue(), Collections.nCopies(2,  CardColor.ORANGE));
	}

	@Test
	public void testCantBuildConnectionWithCardsThePlayerDoesntHave() {
		//Set up some hand that doesn't have 3 red cards
		//Note that this hand has 4 wilds, so it could actually build this connection, but
		//not with the 3 red cards that we're going to specify
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(FXCollections.observableMap(Map.of(CardColor.ORANGE, 2, CardColor.ANY, 4)));
	
		when(this.mockedConnection1to2.getNumSegments()).thenReturn(3);
		when(this.mockedConnection1to2.getColor()).thenReturn(CardColor.RED);

		testCantBuildConnection(mockedConnection1to2, Collections.nCopies(3,  CardColor.RED));
	}	
	
	@Test
	public void testCantBuildConnectionIfPlayerDoesntHaveEnoughTrains() {
		when(mockedGameState.getNumTrainsRemaining())
			.thenReturn(FXCollections.observableMap(
					Map.of(mockedPlayer, 4)));
		
		testCantBuildConnectionAfterSettingUpState(5, CardColor.PURPLE, Collections.nCopies(5, CardColor.PURPLE));
	}
	
	@Test
	public void testCantBuildConnectionIfConnectionDoesNotExist() {
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(FXCollections.observableMap(Map.of(CardColor.ORANGE, 2)));
		
		//Create a connection that is not in the map
		Connection mockedConnection = mock(Connection.class);
		when(mockedConnection.getNumSegments()).thenReturn(2);
		when(mockedConnection.getColor()).thenReturn(CardColor.ORANGE);

		testCantBuildConnection(mockedConnection, Collections.nCopies(2,  CardColor.ORANGE));
	}
	
	@Test
	public void testCantBuidConnectionOnFirstTurn() {
		setupForFirstMove();
		testCantBuildConnectionAfterSettingUpState(1, CardColor.BLUE, Collections.singleton(CardColor.BLUE));
	}
	
	@Test
	public void testCantBuildConnectionIfPlayerHasAlreadyStartedDestCardDrawMove() {
		mover.getDestinationCardsSelectionMove();
		testCantBuildConnectionAfterSettingUpState(1, CardColor.ORANGE, Collections.singleton(CardColor.ORANGE));
	}
	
	@Test
	public void testCantBuildConnectionIfPlayerHasAlreadyDrawnATransportationCard() {
		mover.drawTransportationCard(0);
		testCantBuildConnectionAfterSettingUpState(1, CardColor.PURPLE, Collections.singleton(CardColor.PURPLE));
	}
	
	/**
	 * Converts a collection of cards to the observable map from cards to card counts that
	 * is used in the game state in {@link GameState#getPlayersTransportationCardsHand(Player)}
	 * @param cards
	 * @return
	 */
	private ObservableMap<CardColor, Integer> convertCardsCollectionToCardCountsMap(
			Collection<CardColor> cards) {
		return cards.stream()
					.collect(
							Collectors.toMap(
										x -> x,
										x -> 1,
										(x, y) -> x + y,
										() -> FXCollections.observableMap(new HashMap<>())));
	}
	
	/**
	 * Sets up the game state such that the game map has a connection with the specified 
	 * color and length and such that the player has the specified cards and ensures that
	 * the connection still cannot be built (via {@link #testCantBuildConnection(Connection, Collection)}
	 * @param numSegments
	 * @param connectionColor
	 * @param cardsToUse
	 */
	private void testCantBuildConnectionAfterSettingUpState(int numSegments, CardColor connectionColor,
											Collection<CardColor> cardsToUse) {
		Connection mockedConnection = mock(Connection.class);
		when(mockedConnection.getNumSegments()).thenReturn(numSegments);
		when(mockedConnection.getColor()).thenReturn(connectionColor);
		
		when(mockedConnection.getStart()).thenReturn(this.mockedDestination1);
		when(mockedConnection.getEnd()).thenReturn(this.mockedDestination2);
		
		when(mockedMapData.getConnections()).thenReturn(Collections.singleton(mockedConnection));
		when(mockedMapData.getConnectionsToOrFromDest(any()))
			.thenReturn(Collections.singleton(mockedConnection));
		
		when(mockedGameState.getPlayersTransportationCardsHand(mockedPlayer))
			.thenReturn(convertCardsCollectionToCardCountsMap(cardsToUse));
		
		testCantBuildConnection(mockedConnection, cardsToUse);
	}
	
	/**
	 * Asserts that the specified connection cannot be built with the specified 
	 * cards. First checks via {@link Mover#canBuildConnection(Connection, Collection)}
	 * and then attempts to actually build the connection and asserts that an error is
	 * thrown.
	 * @param connection
	 * @param cardsToUse
	 */
	private void testCantBuildConnection(Connection connection, Collection<CardColor> cardsToUse) {
		
		assertFalse(mover.canBuildConnection(connection,  cardsToUse));
		
		try {
			mover.buildConnection(connection, cardsToUse);
			fail("Expected mover.buildConnection to throw exception, but no exception thrown");
		}
		catch(IllegalMoveException e) {}
	}
}
