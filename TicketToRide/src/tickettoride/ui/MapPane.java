package tickettoride.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import tickettoride.model.MapData;
import tickettoride.model.MapData.CardColor;
import tickettoride.model.MapData.Connection;
import tickettoride.model.MapData.Destination;
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
		
		//Keep track of connections that have already been drawn. This is needed because
		//when there are two connections between 2 cities, they are drawn at the same time
		//even if they aren't listed one after the other in the collection of connections
		//from the map, so we need to skip the second connection once we iterate to it
		//in the collection of connections.
		Set<Connection> connectionsAlreadyDrawn = new HashSet<>();
		
		for(Connection conn : mapData.getConnections()) {
			if(connectionsAlreadyDrawn.contains(conn)) {
				continue;
			}
			
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
			
			//Find if there are multiple paths
			List<Connection> currentConnections = 
					mapData.getConnections()
							.stream()
							.filter(c -> 
									(c.getStart().equals(conn.getStart()) && c.getEnd().equals(conn.getEnd())) ||
									(c.getStart().equals(conn.getEnd()) && c.getEnd().equals(conn.getStart()))
									)
							.collect(Collectors.toList());
			
			if(currentConnections.size() == 0) {
				throw new RuntimeException("Couldn't find connections from " + conn.getStart() + " to " +
											conn.getEnd() + ", but one exists. This is likely Nate's fault.");
			}
			
			int numSegments = currentConnections.get(0).getNumSegments();
			if(currentConnections.stream().anyMatch(c -> c.getNumSegments() != numSegments)) {
				throw new IllegalArgumentException("All connections between the same two cities must have the same"
						+ " number of segments");
			}
			
			connectionsAlreadyDrawn.addAll(currentConnections);
			
			
			Rectangle pathRect = new Rectangle();
			pathRect.setOpacity(0.2);
			pathRect.setArcHeight(5);
			pathRect.setArcWidth(5);
			pathRect.widthProperty().bind(pathLength);
			pathRect.setHeight(DEST_RADIUS * 2 * currentConnections.size());
			
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
			for(int segmentNumber = 1; segmentNumber <= conn.getNumSegments(); segmentNumber++) {
				for(int connectionNumber = 1; connectionNumber <= currentConnections.size(); connectionNumber++) {
					
					Rectangle segmentRect = new Rectangle();
					segmentRect.setArcHeight(5);
					segmentRect.setArcWidth(5);
					segmentRect.setOpacity(0.8);
					
					Paint fillColor = null;
					
					switch(currentConnections.get(connectionNumber - 1).getColor()) {
					case ANY:
						fillColor = Color.LIGHTGREY;
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
					
					segmentRect.heightProperty().bind(pathRect.heightProperty()
																.divide(currentConnections.size())
																.multiply(0.8));
							
					DoubleBinding pathDividedByN = pathRect.widthProperty().divide(conn.getNumSegments());
					DoubleBinding pathHeightDividedByNumConnections = pathRect.heightProperty().divide(currentConnections.size());
					
					segmentRect.widthProperty().bind(pathDividedByN.subtract(PATH_SEGMENT_BUFFER));
					
					double segmentsAwayFromCenter = segmentNumber - (conn.getNumSegments() + 1)/2.0;
					double pathsAwayFromCenter = connectionNumber - (currentConnections.size() + 1)/2.0;
					
					DoubleBinding segmentCenterX = 
							Bindings.createDoubleBinding(
									() -> pathCenterX.get() + 
										Math.cos(rotationRadians.get()) * pathDividedByN.get() * segmentsAwayFromCenter +
										Math.sin(rotationRadians.get()) * pathHeightDividedByNumConnections.get() * pathsAwayFromCenter,
									pathCenterX,
									rotationRadians,
									pathDividedByN
									);
					
					DoubleBinding segmentCenterY = 
							Bindings.createDoubleBinding(
									() -> pathCenterY.get() + 
										Math.sin(rotationRadians.get()) * pathDividedByN.get() * segmentsAwayFromCenter -
										Math.cos(rotationRadians.get()) * pathHeightDividedByNumConnections.get() * pathsAwayFromCenter,
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
	
}
