package cgsmhbm;

import javafx.application.Application;
import javafx.stage.Stage;

public class Program extends Application {
	
	@Override
	public void start(Stage primaryStage) {
	    try {
	        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/MainView.fxml"));
	        javafx.scene.Parent root = loader.load();
	        javafx.scene.Scene scene = new javafx.scene.Scene(root, 1400, 600); 
	        primaryStage.setTitle("Controle de Garantias da SMH");
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}

	public static void main(String[] args) {
		launch(args);
		
	}

}
