package tickettoride.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import tickettoride.model.GameDefinition;
import tickettoride.model.GameState;
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Destination;
import tickettoride.utilities.ImageLoader;

/**
 * The FXML controller class that handles all actions from the GUI and sets up various bindings.
 * This is basically the middle man between the user interface and the game logic.
 * </br></br>
 * Students will have to modify some parts of this file, but they should use caution to only modify what they
 * need to because Nate will likely be making some modifications too and merges could get nasty if too much changes
 * @author nate
 *
 */
public class TicketToRideController {

	/** Property containing the current {@link GameState} */
	private Property<GameState> game = new SimpleObjectProperty<>();
	
	/** Binding that results in the current {@link MapData} */
	private ObjectBinding<MapData> mapData = 
			Bindings.createObjectBinding(
					() -> game.getValue() == null ? null : game.getValue().getMap(),
					game);
	
	/**
	 * Scrolling pane that contains the map
	 */
	@FXML
	protected ScrollPane mapScrollPane;
	
	/**
	 * Pane containing the map (this is the content within the {@link #mapScrollPane}
	 */
	@FXML
	protected MapPane mapAnchorPane;
	
	/**
	 * Canvas object used to display background image
	 */
	@FXML
	protected Canvas mapCanvas;
	
	/**
	 * Background image to be displayed (path specified by the game definition file)
	 */
	@FXML
	protected ImageView backgroundImage;
	
	/**  Graphical element representing the 1st face up transportation card */
	@FXML
	protected Rectangle cardToDraw1;
	/**  Graphical element representing the 2nd face up transportation card */
	@FXML
	protected Rectangle cardToDraw2;
	/**  Graphical element representing the 3rd face up transportation card */
	@FXML
	protected Rectangle cardToDraw3;
	/**  Graphical element representing the 4th face up transportation card */
	@FXML
	protected Rectangle cardToDraw4;
	/**  Graphical element representing the 5th face up transportation card */
	@FXML
	protected Rectangle cardToDraw5;
	/** Graphical element representing the deck of transportation cards */
	@FXML
	protected Rectangle transportationDeck;
	
	/** Graphical element representing the deck of destination cards */
	@FXML
	protected Rectangle destinationDeck;
	
	/** Minimum width that the map should be sized to before using the scroll bars (this will probably change) */
	private final double MIN_MAP_WIDTH = 400;
	/** Minimum width that the map should be sized to before using the scroll bars (this will probably change) */	
	private final double MIN_MAP_HEIGHT = 400;
	
	
	/** This is a special method called by the FXML loader. It is used to set up various properties 
	 * of the graphical components after all of the members with the "FXML" notation have been
	 * populated.
	 */
	public void initialize() {
		
		//A lot of this is likely to change because I'm not 100% satisfied with my 
		//resizing behavior. Basically all of this is to try to properly handle resizing
		mapScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		mapScrollPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);

		mapCanvas.widthProperty().bind(Bindings.max(MIN_MAP_WIDTH, mapScrollPane.widthProperty().subtract(15.0)));
		mapCanvas.heightProperty().bind(Bindings.max(MIN_MAP_HEIGHT, mapScrollPane.heightProperty().subtract(15.0)));
		
		backgroundImage.fitWidthProperty().bind(mapCanvas.widthProperty());
		backgroundImage.fitHeightProperty().bind(mapCanvas.heightProperty());
		
		mapCanvas.setOpacity(0.5);
		
		mapAnchorPane.getMapProperty().bind(mapData);
		mapAnchorPane.setBackgroundCanvas(mapCanvas);
		
		game.addListener((mapData) -> paintMap());
		mapCanvas.widthProperty().addListener((mapData) -> paintMap());
		mapCanvas.heightProperty().addListener((mapData) -> paintMap());
		
		setUpSidePanelCardBindings();
	}
	
	/**
	 * Method sets up bindings to ensure that the face up transportation cards on the side panel
	 * always show the appropriate image based on the {@link #game game state's} current face up transportation
	 * cards
	 */
	private void setUpSidePanelCardBindings() {
		
		try {
			Image transportCardBack = ImageLoader.load("images/transportationBack.jpg");
			transportationDeck.setFill(new ImagePattern(transportCardBack));
			
			Image destinationCardBack = ImageLoader.load("images/destinationBack.jpg");
			destinationDeck.setFill(new ImagePattern(destinationCardBack));
			
			Map<CardColor, Image> transportationCardImages = new HashMap<>();
			transportationCardImages.put(CardColor.ANY, ImageLoader.load("images/wild.jpg"));
			transportationCardImages.put(CardColor.BLACK, ImageLoader.load("images/black.jpg"));
			transportationCardImages.put(CardColor.BLUE, ImageLoader.load("images/blue.jpg"));
			transportationCardImages.put(CardColor.GREEN, ImageLoader.load("images/green.jpg"));
			transportationCardImages.put(CardColor.ORANGE, ImageLoader.load("images/orange.jpg"));
			transportationCardImages.put(CardColor.PURPLE, ImageLoader.load("images/purple.jpg"));
			transportationCardImages.put(CardColor.RED, ImageLoader.load("images/red.jpg"));
			transportationCardImages.put(CardColor.WHITE, ImageLoader.load("images/white.jpg"));
			transportationCardImages.put(CardColor.YELLOW, ImageLoader.load("images/yellow.jpg"));
			
			List<Rectangle> drawCardRects = Arrays.asList(
					cardToDraw1,
					cardToDraw2,
					cardToDraw3,
					cardToDraw4,
					cardToDraw5);
			
			game.addListener((x) -> {
				
				if(game.getValue() != null) {
					ObservableList<CardColor> cardProperties = game.getValue().getFaceUpTransportationCards();
					
					//Little function to update a GUI rect based on a card value. Consumes the index of the card
					IntConsumer updateDrawRect = i -> {
						CardColor color = cardProperties.get(i);
						ImagePattern pattern = color == null ? null : new ImagePattern(transportationCardImages.get(color));
						drawCardRects.get(i).setFill(pattern);
					};
					
					//Set to initial card values, then add listener for changes
					IntStream.range(0, cardProperties.size()).forEach(updateDrawRect);
					
					cardProperties.addListener(new ListChangeListener<CardColor>() {

						@Override
						public void onChanged(Change<? extends CardColor> change) {
							//Most likely, this while loop will only ever iterate once per call
							while(change.next()) {
								//Most likely, this int stream will just have one element, but better to be safe.
								IntStream.range(change.getFrom(), change.getTo()).forEach(updateDrawRect);
							}
						}
						
					});
				}
				else {
					//If the game is null, clear them all (I don't think this will ever actually happen)
					drawCardRects.forEach(r -> r.setFill(null));
				}
			});
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to be called by the File->New Game option. Should prompt the user for a json
	 * game definition file, load it, and then set the {@link #game} property.
	 * </br></br>
	 * If there are any issues loading the file (such as the file not existing, or being
	 * improperly formatted) it should show a meaningful error dialog indicating the cause
	 * of the error.
	 */
	@FXML
	public void createNewGame() {
		//TODO Allow the user to select a game definition file to load, then load the
		//file.
		
		//For now, we'll just manually build a simple game definition so that we
		//can test out the drawing code. Replace this hard coded definition with
		//one loaded from the specified file.
		Destination d1 = new Destination("Awesomeville", 0.5, 0.5);
		Destination d2 = new Destination("Paradise City", 0.6, 0.7);
		Destination d3 = new Destination("SuperdyDuperBurgh", 0.2, 0.8);
		Destination d4 = new Destination("Justokayville", 0.3, 0.4);
				
		MapData hardCodedMapData = new MapData() {

			@Override
			public Collection<Destination> getDestinations() {
				return Arrays.asList(d1, d2, d3, d4);
			}

			@Override
			public Collection<Connection> getConnectionsToOrFromDest(Destination dest) {
				return getConnections().stream().filter(c -> c.getStart().equals(dest) || c.getEnd().equals(dest))
									.collect(Collectors.toList());
			}

			@Override
			public Collection<Connection> getConnections() {
				return Arrays.asList(new Connection(d1, d3, CardColor.BLACK, 3),
						new Connection(d1, d2, CardColor.YELLOW, 1),
						new Connection(d2, d3, CardColor.ORANGE, 2),
						new Connection(d3, d4, CardColor.ANY, 3),
						new Connection(d4, d3, CardColor.BLUE, 3),
						new Connection(d4, d1, CardColor.GREEN, 3));
			}
			
			@Override
			public MapData clone() {
				return this;	//For simplicity here, we're not actually going to clone it. It won't matter
								//for this case, but needs to be implemented in the real version.
			}
			
		};
		GameDefinition hardCodedGameDefinition = new GameDefinition(new File("maps/usaMap.jpg"), 
																	hardCodedMapData, 24);
		
		//Once your game definition is created, set the game property to a new GameState value initialized
		//with the newly loaded game definition. This will cause the map to be drawn
		game.setValue(new GameState(hardCodedGameDefinition));
		
	}
	
	/**
	 * Paints the background image. This method probably isn't actually necessary. I may take it out
	 * later.
	 */
	private void paintMap() {
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		
		gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		
		if(game.getValue() != null && game.getValue().getBackgroundImage() != null) {
			gc.drawImage(game.getValue().getBackgroundImage(), 
							0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		}
	}
}
