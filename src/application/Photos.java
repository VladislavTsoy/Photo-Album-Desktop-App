package application;

import controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is used to launch the program.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Photos extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	    Controller ctrl = new Controller();
	    ctrl.start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
