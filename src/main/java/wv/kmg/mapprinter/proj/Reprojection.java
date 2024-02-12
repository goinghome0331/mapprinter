package wv.kmg.mapprinter.proj;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.proj4j.BasicCoordinateTransform;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.ProjCoordinate;


public class Reprojection {
	public static Map<String,CoordinateReferenceSystem> d = new HashMap<String,CoordinateReferenceSystem>();
	static {
		CRSFactory crsFactory = new CRSFactory();
		d.put("EPSG:5174",crsFactory.createFromParameters("EPSG:5174","+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43"));
		d.put("EPSG:5181",crsFactory.createFromParameters("EPSG:5181","+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs"));
		d.put("EPSG:3857",crsFactory.createFromParameters("EPSG:3857","+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs"));
		d.put("EPSG:4326",crsFactory.createFromParameters("EPSG:4326","+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"));
	}
//	CoordinateTransform coordinateTransform;
	BasicCoordinateTransform transformer;
	public static Reprojection createInstance(String sourceProj,String targetProj) { 
		if (d.get(sourceProj) == null ||  d.get(targetProj) == null) {
			return null;
		}
		return new Reprojection(sourceProj,targetProj);
	}
	private Reprojection(String sourceProj,String targetProj) {
		if (d.get(sourceProj) == null ||  d.get(targetProj) == null) {
			throw new RuntimeException("create Reprojection object error");
		}
		transformer = new BasicCoordinateTransform(d.get(sourceProj), d.get(targetProj));
		
	}
	public double[] transform(double x, double y) {
		ProjCoordinate p = new ProjCoordinate();
		p.x = x;
		p.y = y;
		// 변환된 좌표를 담을 객체 생성
		ProjCoordinate p2 = new ProjCoordinate();
		ProjCoordinate projCoordinate = transformer.transform(p, p2);
		return new double[] {projCoordinate.x,projCoordinate.y};
		
	}
	public double[] transform(double[] p) {
		return this.transform(p[0],p[1]);
		
	}
	
}
