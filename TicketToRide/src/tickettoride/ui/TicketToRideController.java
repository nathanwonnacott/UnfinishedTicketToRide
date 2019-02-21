package tickettoride.ui;

import java.util.Collection;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import tickettoride.mapdata.MapData;
import tickettoride.mapdata.MapData.Destination;

public class TicketToRideController {

	
	private Property<MapData> mapData = new SimpleObjectProperty<>();
	
	@FXML
	protected ScrollPane mapPane;
	
	@FXML
	protected AnchorPane mapAnchorPane;
	@FXML
	protected Canvas mapCanvas;
	
	private final double MIN_MAP_WIDTH = 400;
	private final double MIN_MAP_HEIGHT = 400;
	public void initialize() {
		
		mapPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		mapPane.setHbarPolicy(ScrollBarPolicy.ALWAYS);

		mapCanvas.widthProperty().bind(Bindings.max(MIN_MAP_WIDTH, mapPane.widthProperty().subtract(15.0)));
		mapCanvas.heightProperty().bind(Bindings.max(MIN_MAP_HEIGHT, mapPane.heightProperty().subtract(15.0)));
		
		mapData.addListener((mapData) -> paintMap());
		mapCanvas.widthProperty().addListener((mapData) -> paintMap());
		mapCanvas.heightProperty().addListener((mapData) -> paintMap());
		
	}
	
	@FXML
	public void createNewGame() {
		//TODO probably use a file chooser to load a file
		//GameController gameController = null; //TODO new GameController();
		
		mapData.setValue(new MapData() {

			@Override
			public Collection<Destination> getDestinations() {
				return Collections.singleton(new Destination("Awesomeville", 0.5, 0.5));
			}
			
		});
	}
	
	private void paintMap() {
		
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		
		gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		
		//Just for debug purposes
		gc.setFill(Color.GREY);
		for(int i = 0; i <= mapCanvas.getWidth(); i += 20) {
			gc.strokeLine(i, 0, i, mapCanvas.getHeight());
		}
		for(int i = 0; i <= mapCanvas.getHeight(); i += 20) {
			gc.strokeLine(0, i, mapCanvas.getWidth(), i);
		}
		
		if(mapData.getValue() != null) {
		
			double destRadius = 10;
			
			//Draw cities
			for(Destination dest : mapData.getValue().getDestinations()) {
				gc.setFill(Color.BLACK);
				
				double x = dest.getXFraction() * mapCanvas.getWidth() - destRadius;
				double y = dest.getYFraction() * mapCanvas.getHeight() - destRadius;
				gc.fillOval(x, y, destRadius * 2, destRadius * 2);
			}
		}
	}
}
