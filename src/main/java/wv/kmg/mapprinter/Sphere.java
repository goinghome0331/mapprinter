package wv.kmg.mapprinter;

public class Sphere {
	static final double DEFAULT_RADIUS = 6371008.8;

	public static double getDistance(double[] c1, double[] c2, Double radius) {
		radius = radius != null ? radius : DEFAULT_RADIUS;
		double lat1 = Cal.toRadians(c1[1]);
		double lat2 = Cal.toRadians(c2[1]);
		double deltaLatBy2 = (lat2 - lat1) / 2;
		double deltaLonBy2 = Cal.toRadians(c2[0] - c1[0]) / 2;
		double a =

				Math.sin(deltaLatBy2) * Math.sin(deltaLatBy2)
						+ Math.sin(deltaLonBy2) * Math.sin(deltaLonBy2) * Math.cos(lat1) * Math.cos(lat2);
		return 2 * radius * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
}
