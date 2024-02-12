package wv.kmg.mapprinter.proj;

public class WORLD_PROJ_DATA {
	public static final double RADIUS = 6378137;

	public static final double HALF_SIZE = Math.PI * RADIUS;

	public static final double[] EXTENT = {-HALF_SIZE, -HALF_SIZE, HALF_SIZE, HALF_SIZE};

	public static final double[] WORLD_EXTENT = {-180, -85, 180, 85};
}
