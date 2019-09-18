package tickettoride.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is the main entry point for the application. It does very little (as is typical for a JavaFX application
 * that uses FXML). For more details on what is going on here, look at various online tutorials on FXML and JavaFX
 * applications.
 * @author nate
 *
 */
public class TicketToRide extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("TicketToRide.fxml"));
       
        Scene scene = new Scene(root, 1000, 600);
    
        stage.setTitle("Ticket to Ride");
        stage.setScene(scene);
        stage.show();
	}

	
	public static void main(String args[]) {
		TicketToRide.launch(args);
	}
}
