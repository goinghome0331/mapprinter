package wv.kmg.mapprinter.proj;

import java.util.HashMap;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import wv.kmg.mapprinter.tile.TileGrid;

public class Projection {
	String code;
	CoordinateReferenceSystem crs;
	double[] extent_;
	double[] wolrd_extent_;
	boolean canWrapX_;
	boolean global_;
	TileGrid defaultTileGrid;
	static Map<String,Projection> m = new HashMap<String,Projection>(); 
	
	static {
		try {
			m.put("EPSG:3857", new Projection("EPSG:3857",WORLD_PROJ_DATA.EXTENT,WORLD_PROJ_DATA.WORLD_EXTENT, true,null));
			m.put("EPSG:4326", new Projection("EPSG:4326",WORLD_PROJ_DATA.EXTENT,WORLD_PROJ_DATA.WORLD_EXTENT, true,null));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private Projection(String code, double[] extent, double[] wolrd_extent, boolean global,TileGrid defaultTileGrid) throws Exception {
		super();
		this.code = code;
//		crs = CRS.decode(code);
		
		this.extent_ = extent;
		this.wolrd_extent_ = wolrd_extent;
		this.global_ = global;
		this.defaultTileGrid = defaultTileGrid;
		this.canWrapX_ = !!(this.global_ && this.extent_ != null);
	}
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return this.crs;
	}
//	public double[] transform(double x, double y,Projection projection) throws Exception {
//		MathTransform mathTransform = CRS.findMathTransform(crs, projection.getCoordinateReferenceSystem(),true);
//		double[] source = {x,y};
//		double[] target = new double[2];
//		mathTransform.transform(source, 0,target,0,1);
//		return target;
//	}
//	public double[] transform(double[] source,Projection projection) throws Exception {
//		if (source.length == 2)
//			return this.transform(source[0],source[1],projection);
//		else if(source.length == 4) {
//			double[] p1 = this.transform(source[0],source[1],projection);
//			double[] p2 = this.transform(source[2],source[3],projection);
//			return new double[] {p1[0],p1[1],p2[0],p2[1]}; 
//		}
//		return null;
//	}
	public static Projection getInstance(String code) throws Exception {
		Projection p = m.get(code);
		if (p == null) {
			p = new Projection(code,null,null,false,null);
			m.put(code, p);
		}
		return p;
	}
	public static boolean equivalent(Projection proj1,Projection proj2) {
		if (proj1 == proj2) return true;
		if (proj1.getCode().equals(proj2.getCode())) return true;
		
		return false;
	}
	
	public String getCode() {
		return code;
	}
	public double[] getExtent() {
		if(extent_ == null && defaultTileGrid != null) {
			return defaultTileGrid.getExtent();
		}
		return extent_;
	}
	public TileGrid getDefaultTileGrid() {
		return defaultTileGrid;
	}
	public void setDefaultTileGrid(TileGrid defaultTileGrid) {
		this.defaultTileGrid = defaultTileGrid;
	}
	public String getUnits() {
		return "m";
	}
	public int getMetersPerUnit() {
		return 1;
	}
	public boolean canWrapX() {
	    return this.canWrapX_;
	  }
	public boolean isGlobal() {
		return global_;
	}

	
	
}
