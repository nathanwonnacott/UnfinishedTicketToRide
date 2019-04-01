package tickettoride.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;

/**
 * Provides some static utility methods for loading image resources
 * @author nate
 *
 */
public class ImageLoader {

	/**
	 * Loads an image based on the path relative to the resources directory, or as
	 * an absolute location if an absolute path is specified
	 * @param path relative path to image file relative to resources directory, or
	 * and absolute path
	 * @return Image
	 * @throws FileNotFoundException 
	 */
	public static Image load(String path) throws FileNotFoundException {
		return load(new File(path));
	}
	
	/**
	 * Loads an image based on the path relative to the resources directory, or and
	 * absolute path if specified
	 * @param file path relative path to image file relative to resources directory, or an
	 * absolute file path
	 * @return Image
	 * @throws FileNotFoundException 
	 */
	public static Image load(File path) throws FileNotFoundException {
		if(!path.isAbsolute()) {
			path = new File("resources/" + path.getPath());
		}
		return new Image(path.toURI().toString());
	}
}
