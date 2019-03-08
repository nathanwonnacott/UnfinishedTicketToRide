package tickettoride.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tickettoride.mapdata.MapData;
import tickettoride.mapdata.MapData.CardColor;
import tickettoride.mapdata.MapData.Connection;
import tickettoride.mapdata.MapData.Destination;
import tickettoride.utilities.MappedBinding;

public class MapPane extends AnchorPane {

	private Property<MapData> mapDataProperty = new SimpleObjectProperty<>();
	
	private Canvas backgroundCanvas;

	private final double DEST_RADIUS = 10.0;

	private final double HIGHLIGHTED_DEST_RADIUS = 12.0;
	
	private final double DEST_CENTER_TO_CONNECTION_START = 15.0;
	private final double PATH_SEGMENT_BUFFER = 15.0;
	
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
		
		Map<Destination, Circle> circles = new HashMap<>();
		
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
			
			destCircle.fillProperty().set(Color.WHITE);
			destCircle.radiusProperty().set(DEST_RADIUS);
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
			
			
			circles.put(dest, destCircle);

			//Note that it's important that the name is added before the circle. Otherwise, when the
			//mouse is on the right hand side of the circle, the text enlarging basically steals the mouse
			//and makes a mouse exit event for the circle, which causes the highlighted state to fluctuate
			//back and forth rapidly. By putting the dest circle on top, the circle has precedence.
			this.getChildren().add(cityName);
			this.getChildren().add(destCircle);
		}
		
		//Setup connections
		for(Connection conn : mapData.getConnections()) {
			Circle startCircle = circles.get(conn.getStart());
			Circle endCircle = circles.get(conn.getEnd());
			
			DoubleBinding pathLength = Bindings.createDoubleBinding(
					() -> Math.sqrt(
							Math.pow(startCircle.getCenterX() - endCircle.getCenterX(), 2.0) +
							Math.pow(startCircle.getCenterY() - endCircle.getCenterY(), 2.0)),
					startCircle.centerXProperty(),
					startCircle.centerYProperty(),
					endCircle.centerXProperty(),
					endCircle.centerYProperty())
					.subtract(DEST_CENTER_TO_CONNECTION_START * 2); 
			
			DoubleBinding pathCenterX = startCircle.centerXProperty().add(endCircle.centerXProperty()).divide(2.0);
			DoubleBinding pathCenterY = startCircle.centerYProperty().add(endCircle.centerYProperty()).divide(2.0);
			
			Rectangle pathRect = new Rectangle();
			pathRect.setOpacity(0.2);
			pathRect.setArcHeight(5);
			pathRect.setArcWidth(5);
			pathRect.widthProperty().bind(pathLength);
			pathRect.setHeight(DEST_RADIUS * 2);	//TODO make bigger if multiple paths
			
			pathRect.xProperty().bind(pathCenterX.subtract(pathLength.divide(2.0)));
			pathRect.yProperty().bind(pathCenterY.subtract(pathRect.heightProperty().divide(2.0)));
			
			this.getChildren().add(pathRect);
			
			DoubleBinding rotationRadians = 
					Bindings.createDoubleBinding(
						() -> Math.atan2(endCircle.getCenterY() - startCircle.getCenterY(), 
								endCircle.getCenterX() - startCircle.getCenterX()), 
						startCircle.centerXProperty(),
						startCircle.centerYProperty(),
						endCircle.centerXProperty(),
						endCircle.centerYProperty());	
			
			pathRect.rotateProperty().bind(
					MappedBinding.createDoubleBinding(
							rotationRadians, 
							rad -> Math.toDegrees(rad.doubleValue())
							)
					);

			
			//Add path segments		
			for(int i = 1; i <= conn.getNumSegments(); i++) {
				//TODO handle multiple paths between two destinations
				
				Rectangle segmentRect = new Rectangle();
				segmentRect.setArcHeight(5);
				segmentRect.setArcWidth(5);
				segmentRect.setOpacity(0.8);
				
				Paint fillColor = null;
				
				switch(conn.getColor()) {
				case ANY:
					fillColor = Color.GREY;
					break;
				case BLACK:
					fillColor = Color.BLACK;
					break;
				case BLUE:
					fillColor = Color.BLUE;
					break;
				case GREEN:
					fillColor = Color.GREEN;
					break;
				case ORANGE:
					fillColor = Color.ORANGE;
					break;
				case PURPLE:
					fillColor = Color.PURPLE;
					break;
				case RED:
					fillColor = Color.RED;
					break;
				case WHITE:
					fillColor = Color.WHITE;
					break;
				case YELLOW:
					fillColor = Color.YELLOW;
					break;
				}
				
				segmentRect.setFill(fillColor);
				
				segmentRect.heightProperty().bind(pathRect.heightProperty().multiply(0.8));
						
				DoubleBinding pathDividedByN = pathRect.widthProperty().divide(conn.getNumSegments());
				
				segmentRect.widthProperty().bind(pathDividedByN.subtract(PATH_SEGMENT_BUFFER));
				
				double segmentsAwayFromCenter = i - (conn.getNumSegments() + 1)/2.0;
				
				DoubleBinding segmentCenterX = 
						Bindings.createDoubleBinding(
								() -> pathCenterX.get() + 
									Math.cos(rotationRadians.get()) * pathDividedByN.get() * segmentsAwayFromCenter,
								pathCenterX,
								rotationRadians,
								pathDividedByN
								);
				
				DoubleBinding segmentCenterY = 
						Bindings.createDoubleBinding(
								() -> pathCenterY.get() + 
									Math.sin(rotationRadians.get()) * pathDividedByN.get() * segmentsAwayFromCenter,
								pathCenterY,
								rotationRadians,
								pathDividedByN
								);
				
				
				segmentRect.xProperty().bind(segmentCenterX.subtract(segmentRect.widthProperty().divide(2)));
				
				segmentRect.yProperty().bind(segmentCenterY.subtract(segmentRect.heightProperty().divide(2)));
				
				segmentRect.rotateProperty().bind(pathRect.rotateProperty());
				
				this.getChildren().add(segmentRect);
				
			}
							
			
			
			
			
		}
		
	}
	
}
