package wv.kmg.mapprinter;

public class Transform {
	public static double[] compose(double[] transform, double dx1, double dy1, double sx, double sy, double angle,
			double dx2, double dy2) {
		double sin = Math.sin(angle);
		double cos = Math.cos(angle);
		transform[0] = sx * cos;
		transform[1] = sy * sin;
		transform[2] = -sx * sin;
		transform[3] = sy * cos;
		transform[4] = dx2 * sx * cos - dy2 * sx * sin + dx1;
		transform[5] = dx2 * sy * sin + dy2 * sy * cos + dy1;
		return transform;
	}

	public static double[] apply(double[] transform, double[] coordinate) {
		double x = coordinate[0];
		double y = coordinate[1];
		coordinate[0] = transform[0] * x + transform[2] * y + transform[4];
		coordinate[1] = transform[1] * x + transform[3] * y + transform[5];
		return coordinate;
	}

}
