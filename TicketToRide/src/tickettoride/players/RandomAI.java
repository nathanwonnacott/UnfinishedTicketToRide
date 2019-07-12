package tickettoride.players;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tickettoride.Mover;
import tickettoride.Mover.DestinationCardSelectionMove;
import tickettoride.model.GameDefinition;
import tickettoride.model.GameDefinition.DestinationCard;
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;

/**
 * This will be a pretty random player. For now it's even worse than random and will end up with hugely
 * negative scores every game. I'll modify this later. Students should not modify this class
 * @author nate
 */
public class RandomAI implements Player {
	
	@Override
	public void setTransportationCardsMapView(Map<CardColor, Integer> unmodifiableTransportationCardsMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDestinationCardsView(Set<DestinationCard> unmodifiableDestinationCards) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNumberOfTrainsRemainingView(Map<Player, Integer> unmodifiableTrainsRemaining) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setFaceUpTransportationCardsView(List<CardColor> cards) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeMove(MapData mapData, Mover mover) {
		//This will be modified by the instructor later. For now, it will just draw destination cards until
		//it's empty and then just draw the first transportation card.
		if(mover.getNumDestinationCardsThatCanBeDrawn() > 0) {
			DestinationCardSelectionMove selector =  mover.getDestinationCardsSelectionMove();
			DestinationCard card = selector.getDestinationCardOptions().iterator().next();
			selector.selectDestinationCards(Collections.singleton(card));
		}
		else {
			mover.drawTransportationCard(0);
		}
	}

	@Override
	public String getDescription() {
		return "Random AI Player";
	}

	@Override
	public void initialize(GameDefinition gameDefinition, int numPlayers) {
		// This player implementation doesn't need to do anything on initialization
	}

}
