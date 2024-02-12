package wv.kmg.mapprinter;

public class MapUtil {
	public static final int DEFAULT_MAX_ZOOM = 42;
	public static final int DEFAULT_MIN_ZOOM = 0;
	
	public static final int DEFAULT_TILE_SIZE = 256;
	public static final double ERROR_THRESHOLD = 0.5;
	public static final double pixelRatio = 1;
	
	
	public static double pixelRound(double value, double pixelRatio) {
		return Math.round(value * pixelRatio) / pixelRatio;
	}
}
