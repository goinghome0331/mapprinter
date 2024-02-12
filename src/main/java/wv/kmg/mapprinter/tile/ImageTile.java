package wv.kmg.mapprinter.tile;

import java.awt.image.BufferedImage;

public class ImageTile {
	BufferedImage image;
	double[] extent;
	public ImageTile(BufferedImage image, double[] extent) {
		super();
		this.image = image;
		this.extent = extent;
	}
	public BufferedImage getImage() {
		return image;
	}
	public double[] getExtent() {
		return extent;
	}
	
}
