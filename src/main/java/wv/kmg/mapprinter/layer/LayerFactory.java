package wv.kmg.mapprinter.layer;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Logger;

import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import wv.kmg.mapprinter.GeojsonParser;
import wv.kmg.mapprinter.GeoserverParam;
import wv.kmg.mapprinter.RequestParam;
import wv.kmg.mapprinter.ViewState;
import wv.kmg.mapprinter.proj.Reprojection;

public class LayerFactory {
	private static final Logger LOG = Logger.getLogger("LayerFactory");
	// 타입에 따라 layer 생성하기
	public static Layer createLayer(String name,ViewState viewState) throws Exception{
		String type = System.getProperty(name+".type");
		if(type == null) {
			throw new RuntimeException(name+".type is null");
		}
		LOG.info("create Layer "+name);
		LOG.info("Layer info ["+name+"]> type : " + type);
		switch(type) {
		case "json":{
			String path = System.getProperty(name+".path");
			if(path == null) {
				throw new RuntimeException(name+".path is null");
			}
			LOG.info("Layer info ["+name+"]> path : " + path);
			FileInputStream fis = new FileInputStream(new File(path));
			int ch;
			StringBuffer sb = new StringBuffer();
			while((ch = fis.read()) != -1) {
				sb.append((char)ch);
			}
			FeatureSource featuresource = GeojsonParser.readFeatures(sb.toString());
			CoordinateReferenceSystem crs = featuresource.getBounds().getCoordinateReferenceSystem();
			if(crs == null) {
				LOG.info("feature doesn't have projection. rendering feature failed!");
				return null;
			}
			String depsg = "EPSG:"+crs.getIdentifiers().iterator().next().getCode();
			FeatureIterator it = featuresource.getFeatures().features();
			String vepsg = viewState.getProjection().getCode();
			if(!depsg.equals(vepsg)) {
				LOG.info("view and feature projection is different.");
				while(it.hasNext()) {
					SimpleFeature f = (SimpleFeature)it.next();
					GeometryCollection gc = (GeometryCollection)f.getDefaultGeometry();
					
					for(int i = 0; i < gc.getNumGeometries(); i++) {
						Geometry g = gc.getGeometryN(i);
						Coordinate[] coordinates = g.getCoordinates();
						for(int j = 0; j < coordinates.length;j++) {
							Reprojection reproj = Reprojection.createInstance(depsg, vepsg);
							double[] ret = reproj.transform(coordinates[j].x, coordinates[j].y);
							coordinates[j].x = ret[0];
							coordinates[j].y = ret[1]; 
						}
					}
				}
				LOG.info("complete transform!");
			}
			
			StyleBuilder styleBuilder = new StyleBuilder();
	        FilterFactory ff = CommonFactoryFinder.getFilterFactory();
	        PolygonSymbolizer polygonSymbolizer = styleBuilder.createPolygonSymbolizer(Color.BLUE);
	        polygonSymbolizer.getFill().setOpacity(ff.literal(0.1)); 

	        polygonSymbolizer.setStroke(styleBuilder.createStroke(Color.BLUE, 3.0));
	        
	        LineSymbolizer lineSymbolizer = styleBuilder.createLineSymbolizer(Color.BLUE);
	        lineSymbolizer.setStroke(styleBuilder.createStroke(Color.BLUE, 3.0));
	        
	        
	        PointSymbolizer pointSymbolizer = styleBuilder.createPointSymbolizer();
	        
	        
	        Style style = styleBuilder.createStyle(polygonSymbolizer);
	        FeatureLayer layer = new FeatureLayer(featuresource, style);
	        return layer;
		}
		case "wms":{
			String wmsType = System.getProperty(name+".wms.type");
			String path = System.getProperty(name+".path");
			String layerName = System.getProperty(name+".name");
			String proj = System.getProperty(name+".proj");
			if(path == null) {
				throw new RuntimeException(name+".path is null");
			}else if(layerName == null) {
				throw new RuntimeException(name+".layerName is null");
			}else if(proj == null) {
				throw new RuntimeException(name+".proj is null");
			}
			LOG.info("Layer info ["+name+"]> path : " + path);
			LOG.info("Layer info ["+name+"]> layerName : " + layerName);
			LOG.info("Layer info ["+name+"]> proj : " + proj);
			
			RequestParam param=null;
			switch(wmsType) {
			case "Geoserver":
				param = new GeoserverParam();
				break;
			}
			return new WmsLayer(path,layerName,proj,param);
		}
		}
		return null;
	}

}
