package tickettoride.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import tickettoride.mapdata.MapData;
import tickettoride.mapdata.MapData.Connection;
import tickettoride.mapdata.MapData.Destination;

public class TicketToRideController {

	private final double destinationCircleDiameter = 20;
	
	private Property<MapData> mapData = new SimpleObjectProperty<>();
	
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
		
		mapData.addListener((mapData) -> paintMap());
		mapCanvas.widthProperty().addListener((mapData) -> paintMap());
		mapCanvas.heightProperty().addListener((mapData) -> paintMap());
		
		Image transportCardBack;
		try {
			transportCardBack = ImageLoader.load("images/cardBack.jpg");
			deck.setFill(new ImagePattern(transportCardBack));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void createNewGame() throws FileNotFoundException {
		//TODO probably use a file chooser to load a file
		//GameController gameController = null; //TODO new GameController();
		//For now, we'll just manually build some stuff to test the map drawing
//		File backgroundImageFile = new File("C:/Users/nate/git/TicketToRide/TicketToRide/resources/maps/usaMap.jpg");
//
//		Image background = new Image(backgroundImageFile.toURI().toString());
		Image background = ImageLoader.load("maps/usaMap.jpg");
		
		Destination d1 = new Destination("Awesomeville", 0.5, 0.5);
		Destination d2 = new Destination("Paradise City", 0.6, 0.7);
		Destination d3 = new Destination("SuperdyDuperBurgh", 0.2, 0.8);
		Destination d4 = new Destination("Justokayville", 0.3, 0.4);
				
		mapData.setValue(new MapData() {

			@Override
			public Collection<Destination> getDestinations() {
				return Arrays.asList(d1, d2, d3, d4);
			}

			@Override
			public Image getBackgroundImage() {
				return background;
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
			
		});
		
		
		
		
//		Circle circle = new Circle();
//		circle.centerXProperty().bind(mapCanvas.widthProperty().divide(2.0));
//		circle.centerYProperty().bind(mapCanvas.heightProperty().divide(2.0));
//		circle.setRadius(destinationCircleDiameter);
//		
//		mapAnchorPane.getChildren().add(circle);
	}
	
	private void paintMap() {
		
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		
		gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		
		//Just for debug purposes
//		gc.setFill(Color.GREY);
//		for(int i = 0; i <= mapCanvas.getWidth(); i += 20) {
//			gc.strokeLine(i, 0, i, mapCanvas.getHeight());
//		}
//		for(int i = 0; i <= mapCanvas.getHeight(); i += 20) {
//			gc.strokeLine(0, i, mapCanvas.getWidth(), i);
//		}
		
		if(mapData.getValue() != null) {
			MapData map = mapData.getValue();

			gc.drawImage(map.getBackgroundImage(), 0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		
			

//			Map<Destination, Point> destinationPoints = new HashMap<>();
//			
//			//Draw cities
//			for(Destination dest : map.getDestinations()) {
//				gc.setFill(Color.BLACK);
//				
//				double x = dest.getXFraction() * mapCanvas.getWidth();
//				double y = dest.getYFraction() * mapCanvas.getHeight();
//				
//				destinationPoints.put(dest, new Point(x,y));
//
//				x -= destinationCircleDiameter/2;
//				y -= destinationCircleDiameter/2;
//				
//				gc.strokeOval(x, y, destinationCircleDiameter, destinationCircleDiameter);
//				
//				gc.strokeText(dest.getName(), x + destinationCircleDiameter, y);
//			}
//			
//			//Draw connections
//			for(Connection conn : map.getConnections()) {
//				Point start = destinationPoints.get(conn.getStart());
//				Point end = destinationPoints.get(conn.getEnd());
//				
//				gc.strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
//			}
		}
	}
	
	private class Point {
		private final double x;
		private final double y;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() { return x;}
		public double getY() { return y;}
	}
}
