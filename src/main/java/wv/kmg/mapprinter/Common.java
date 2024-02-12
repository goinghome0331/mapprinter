package wv.kmg.mapprinter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Common {
	public static void writeImage(String name, String type,BufferedImage image) {
		File outputfile = new File(name);
		try {
			ImageIO.write(image, type, outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
