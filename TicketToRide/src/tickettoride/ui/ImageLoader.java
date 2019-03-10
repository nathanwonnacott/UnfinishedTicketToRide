package tickettoride.ui;

import java.io.File;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;

public class ImageLoader {

	/**
	 * Loads an image based on the path relative to the resources directory
	 * @param path relative path to image file relative to resources directory
	 * @return Image
	 * @throws FileNotFoundException 
	 */
	public static Image load(String path) throws FileNotFoundException {
		File file = new File("resources/" + path);
		return new Image(file.toURI().toString());
	}
}
