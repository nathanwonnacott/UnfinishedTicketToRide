package tickettoride.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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

public class TicketToRideController {

	private Property<GameState> game = new SimpleObjectProperty<>();
	private ObjectBinding<MapData> mapData = 
			Bindings.createObjectBinding(
					() -> game.getValue() == null ? null : game.getValue().getMap(),
					game);
	
	@FXML
	protected ScrollPane mapPane;
	
	@FXML
	protected MapPane mapAnchorPane;
	@FXML
	protected Canvas mapCanvas;
	
	@FXML
	protected ImageView backgroundImage;
	
	@FXML
	protected Rectangle cardToDraw1;
	@FXML
	protected Rectangle cardToDraw2;
	@FXML
	protected Rectangle cardToDraw3;
	@FXML
	protected Rectangle cardToDraw4;
	@FXML
	protected Rectangle cardToDraw5;
	@FXML
	protected Rectangle deck;
	
	private final double MIN_MAP_WIDTH = 400;
	private final double MIN_MAP_HEIGHT = 400;
	public void initialize() {
		
		mapPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		mapPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);

		mapCanvas.widthProperty().bind(Bindings.max(MIN_MAP_WIDTH, mapPane.widthProperty().subtract(15.0)));
		mapCanvas.heightProperty().bind(Bindings.max(MIN_MAP_HEIGHT, mapPane.heightProperty().subtract(15.0)));
		
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
	
	private void setUpSidePanelCardBindings() {
		
		try {
			Image transportCardBack = ImageLoader.load("images/cardBack.jpg");
			deck.setFill(new ImagePattern(transportCardBack));
			
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
				drawCardRects.forEach(r -> r.fillProperty().unbind());
				if(game.getValue() != null) {
					List<ObjectProperty<CardColor>> cardProperties = game.getValue().getFaceUpTransportationCardProperties();
					for(int i =0; i < drawCardRects.size(); i++) {
						final ObjectProperty<CardColor> colorProperty = cardProperties.get(i);
						drawCardRects.get(i)
							.fillProperty()
							.bind(Bindings.createObjectBinding(
									() -> {
										CardColor color = colorProperty.getValue();
										if(color == null) {
											return null;
										}
										else {
											return new ImagePattern(transportationCardImages.get(color));
										}
									},
									colorProperty
									)
									);
					}
				}
			});
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method to be called by the File->New Game option. Should prompt the user for a json
	 * game definition file, load it, and then set the {@link #game} property.
	 * @throws FileNotFoundException
	 */
	@FXML
	public void createNewGame() throws FileNotFoundException {
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
			
		};
		GameDefinition hardCodedGameDefinition = new GameDefinition(new File("maps/usaMap.jpg"), 
																	hardCodedMapData, 24);
		
		//Once your game definition is created, set the game property to a new GameState value initialized
		//with the newly loaded game definition. This will cause the map to be drawn
		game.setValue(new GameState(hardCodedGameDefinition));
		
	}
	
	private void paintMap() {

		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		
		gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		
		if(game.getValue() != null && game.getValue().getBackgroundImage() != null) {
			gc.drawImage(game.getValue().getBackgroundImage(), 
							0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		}
	}
}
