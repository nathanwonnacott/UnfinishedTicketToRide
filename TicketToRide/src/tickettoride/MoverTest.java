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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import tickettoride.Mover.DestinationCardSelectionMove;
import tickettoride.Mover.IllegalMoveException;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.GameState;
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Connection;
import tickettoride.model.MapData.Destination;



class MoverTest {

	/**
	 * Mover object to be used in all the tests. Should get reset before each test by
	 * the {@link #setup()} and {@link #setupForFirstMove()} methods.
	 */
	private Mover mover = null;
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
		when(mockedGameState.getMap()).thenReturn(mockedMapData);
		when(mockedGameState.getDestinationCardsDeckRemainingProperty())
			.thenReturn(destinationCardsRemainingProperty);
		
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
		
	}
	
	/** Utility method to execute a simple connection claiming method. This isn't
	 * intended to test the collection claiming method itself, but rather be used in 
	 * other test to ensure things like not being able to execute other move types
	 * after claiming a connection.
	 */
	private void claimConnection() {
		mover.buildConnection(mockedConnection1to2, Collections.singleton(CardColor.ANY));
	}
	
	@Test
	public void testSelectDestinationCardsOnFirstTurn() {
		setupForFirstMove();

		DestinationCard destCard1 = mock(DestinationCard.class);
		DestinationCard destCard2 = mock(DestinationCard.class);
		DestinationCard destCard3 = mock(DestinationCard.class);
		
		Collection<DestinationCard> expectedDestinationOptions =
				Arrays.asList(destCard1, destCard2, destCard3);
		
		when(mockedGameState.drawDestinationCards()).thenReturn(expectedDestinationOptions);
		
		DestinationCardSelectionMove selectionMove = mover.getDestinationCardsSelectionMove();
		
		assertThat(selectionMove.getDestinationCardOptions(),
				containsInAnyOrder(expectedDestinationOptions));
		
		//TODO add checks that you must select 2 or three of these
		//can probably save all other checks for how the move operates for the 
		//more general destination card selection tests. Or find a nice reusable
		//way to check for either case
		fail("Not done implementing this test yet");
		
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
			destinationCardsRemainingProperty.set(cardsRemaining);
			assertEquals(cardsRemaining, mover.getNumDestinationCardsThatCanBeDrawn(),
					"When only " + cardsRemaining + " destination cards are remaining, only " +
							cardsRemaining + " cards should be able to be drawn");
		}
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterTrainCardsAreDrawn() {
		mover.drawTransportationCard(1);
		assertEquals(0, mover.getNumDestinationCardsThatCanBeDrawn(),
				"Cannot draw destination cards after a train card has been drawn");
		
		try {
			mover.getDestinationCardsSelectionMove();
			fail("Illegal move exception should have been thrown when attempting"
					+ " to draw destination card after drawing a transportation card");
		}
		catch(IllegalMoveException e) {
			//Do nothing
		}
	}
	
	@Test
	public void testCantDrawDestinationCardsAfterRouteIsClaimed() {
		
		claimConnection();
		assertEquals(0, mover.getNumDestinationCardsThatCanBeDrawn(),
				"Cannot draw destination cards after claiming a connection");
		
		try {
			mover.getDestinationCardsSelectionMove();
			fail("Illegal move exception should have been thrown when attempting"
					+ " to draw destination card after claiming a connection");
		}
		catch(IllegalMoveException e) {
			//Do nothing
		}
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

}
