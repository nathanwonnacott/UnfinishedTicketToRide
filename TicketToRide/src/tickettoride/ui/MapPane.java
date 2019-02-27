package tickettoride.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
	}
	
	//TODO, see if this can somehow just be set automatically by FXML
	public void setBackgroundCanvas(Canvas canvas) {
		this.backgroundCanvas = canvas;
	}
	
	public Property<MapData> getMapProperty() {
		return mapDataProperty;
	}
	
	private void setupMap() {
		

		MapData mapData = mapDataProperty.getValue();
		if(mapData == null) {
			return;
		}
		
		this.getChildren().clear();
		this.getChildren().add(backgroundCanvas);
		
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
			
			
			Text cityName = new Text(dest.getName());
			cityName.yProperty().bind(destCircle.centerYProperty());
			cityName.xProperty().bind(destCircle.centerXProperty().add(destCircle.radiusProperty().add(5)));
			
			cityName.effectProperty().bind(
					MappedBinding.createBinding(highlighted, (h) -> h ? glow : normalEffects)
					);
			cityName.fontProperty().bind(
					MappedBinding.createBinding(highlighted, (h) -> h ? new Font(24) : new Font(10))
					);
			
			
			
			this.getChildren().add(destCircle);
			this.getChildren().add(cityName);
		}
		
	}
	
}
