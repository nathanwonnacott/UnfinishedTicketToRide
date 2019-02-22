package tickettoride.ui;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import tickettoride.mapdata.MapData;
import tickettoride.mapdata.MapData.Destination;

public class MapPane extends AnchorPane {

	private Property<MapData> mapDataProperty = new SimpleObjectProperty<>();
	
	private Canvas backgroundCanvas;
	
	private final double DEST_RADIUS = 10.0;
	
	public MapPane() {
		mapDataProperty.addListener((m) -> setupMap());
		this.setBackground(new Background(new BackgroundFill(Color.web("#ff0000"), CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
	//TODO, see if this can somehow just be set automatically by FXML
	public void setBackgroundCanvas(Canvas canvas) {
		this.backgroundCanvas = canvas;
	}
	
	public Property<MapData> getMapProperty() {
		return mapDataProperty;
	}
	
	private void setupMap() {
		System.out.println("Setting up map!");
		
		this.getChildren().clear();
		if(backgroundCanvas != null)
			this.getChildren().add(backgroundCanvas);
		
		MapData mapData = mapDataProperty.getValue();
		
		if(mapData == null) {
			System.out.println("Null map data");
			return;
		}
		
		
		//Setup destinations
		for(Destination dest : mapData.getDestinations()) {
			
			Circle destCircle = new Circle();
			destCircle.setRadius(DEST_RADIUS);
			destCircle.centerXProperty().bind(backgroundCanvas.widthProperty().multiply(dest.getXFraction()));
			destCircle.centerYProperty().bind(backgroundCanvas.heightProperty().multiply(dest.getYFraction()));
			
			//TODO, come up with a better effect than this.
			//For now, I'm just proving the concept that effects can by applied when mousing over the
			//cities
			destCircle.setOnMouseEntered((evt) -> destCircle.setEffect(new Glow(0.8)));
			destCircle.setOnMouseExited((evt) -> destCircle.setEffect(null));
			this.getChildren().add(destCircle);
		}
		
	}
	
}
