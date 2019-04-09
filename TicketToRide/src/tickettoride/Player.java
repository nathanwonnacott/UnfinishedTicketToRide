package tickettoride;

import tickettoride.model.MapData;

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
	 * Performs a move on the specified map data using the mover object. This method should
	 * block until the move is complete. In other words, after calling this method, a call to 
	 * <pre>mover.getTurnCompletedBinding().get()</pre> should return false.
	 * @param mapData
	 * @param mover
	 */
	public void executeMove(MapData mapData, Mover mover);
	
}
