package tickettoride.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import tickettoride.mapdata.MapData;
import tickettoride.mapdata.MapData.Destination;
import tickettoride.utilities.MappedBinding;

public class MapPane extends AnchorPane {

	private Property<MapData> mapDataProperty = new SimpleObjectProperty<>();
	
	private Canvas backgroundCanvas;

	private final double DEST_RADIUS = 10.0;

	private final double HIGHLIGHTED_DEST_RADIUS = 12.0;
	
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
			destCircle.setStrokeWidth(2);
			destCircle.setStroke(Color.color(0.15, 0.05, 0));
			
			destCircle.centerXProperty().bind(backgroundCanvas.widthProperty().multiply(dest.getXFraction()));
			destCircle.centerYProperty().bind(backgroundCanvas.heightProperty().multiply(dest.getYFraction()));
			
			//Set up highlighting effects for when mouse is over circle
			BooleanProperty highlighted = new SimpleBooleanProperty(false);
			destCircle.setOnMouseEntered((evt) -> highlighted.set(true));
			destCircle.setOnMouseExited((evt) -> highlighted.set(false));
			
			Color fillColor = Color.color(1, 1, 1, 0.1);
			Color highlightedFillColor = Color.color(1,  1,  1, 0.5);
			
			destCircle.fillProperty().bind(
					MappedBinding.createBinding(highlighted,  (h) -> h ? highlightedFillColor : fillColor)
					);
			
			
			destCircle.radiusProperty().bind(
					MappedBinding.createBinding(highlighted, (h) -> h ? HIGHLIGHTED_DEST_RADIUS : DEST_RADIUS)
					);
			

			Effect normalEffects = new GaussianBlur(2);
			DropShadow glow = new DropShadow(HIGHLIGHTED_DEST_RADIUS * 1.2, Color.LIGHTYELLOW);
			glow.setInput(normalEffects);
			destCircle.effectProperty().bind(
					MappedBinding.createBinding(highlighted, (h) -> h ? glow : normalEffects)
					);
			
			
			
			this.getChildren().add(destCircle);
		}
		
	}
	
}
