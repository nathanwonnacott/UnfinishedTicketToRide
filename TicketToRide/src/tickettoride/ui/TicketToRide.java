package tickettoride.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
