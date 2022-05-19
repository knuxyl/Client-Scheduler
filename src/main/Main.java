package main;
import database.Jdbc;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
/**
 * Java entry point class. Initializes and opens the main window.
 *
 * @author James Watson - 001520415
 */
public class Main extends Application {
	/** used to set language */
	public static String language = Locale.getDefault().getLanguage();
	/** title bar wgu icon */
	/**
	 * Entry method for application. Connects to server and calls to set up main window.
	 *
	 * @param args - command line arguments
	 */
    public static void main(String[] args) {
    	Jdbc.connect();
        launch(args);
    }
	/**
	 * Loads main windows and shows it.
	 *
	 * @throws IOException - throws if cannot find fxml scene file
	 */
	@Override
    public void start(Stage stageMain) throws IOException {
		Image icon = new Image("/res/icon.png");
    	Parent fxmlMain = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/fxmlscene/Main.fxml")));
        stageMain.setTitle("QAM2 - Java Application Development");
        stageMain.getIcons().add(icon);
        stageMain.setScene(new Scene(fxmlMain, 854, 480));
        stageMain.setResizable(false);
        stageMain.show();
    }
}