package tickettoride.ui;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tickettoride.mapdata.MapData;

public class MapPane extends Pane {

	private Property<MapData> mapDataProperty = new SimpleObjectProperty<>();
	
	public MapPane() {
		mapDataProperty.addListener((m) -> setupMap());
		this.setBackground(new Background(new BackgroundFill(Color.web("#ff0000"), CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
	public Property<MapData> getMapProperty() {
		return mapDataProperty;
	}
	
	private void setupMap() {
		System.out.println("Setting up map!");
//		this.getChildren().clear();
//		
//		MapData mapData = mapDataProperty.getValue();
//		
//		if(mapData == null) {
//			System.out.println("Null map data");
//			return;
//		}
//		
//		Canvas backgroundImageCanvas = new Canvas();
//		backgroundImageCanvas.getGraphicsContext2D().drawImage(mapData.getBackgroundImage(), getWidth(), getHeight());
//		getChildren().add(backgroundImageCanvas);
		
		
	}
	
}
