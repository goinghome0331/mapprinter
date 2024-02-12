package wv.kmg.mapprinter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

public class GeojsonParser {
	static FeatureJSON fjson = new FeatureJSON();
	public static FeatureSource readFeatures(String json) throws IOException{
		Pattern p = Pattern.compile("(?<=\"type\":\")[^\"]+");
		Matcher m = p.matcher(json);
		String type = null;
		if(m.find()) {
			type = m.group();
		}
		
//		List<Feature> ret = new LinkedList<Feature>();
		FeatureSource fs = null;

		switch (type) {
		case "FeatureCollection": {
			FeatureCollection fc = fjson.readFeatureCollection(json);
//			FeatureIterator<Feature> fi = fc.features();
//			
//			while(fi.hasNext()) {
//				Feature f = fi.next();
//				ret.add(f);
//			}
			fs = DataUtilities.source(fc);
			break;
		}
		case "Feature": {
			SimpleFeature f = fjson.readFeature(json);
//			ret.add(f);
			fs = DataUtilities.source(f);
			break;
		}
		}
		
		return fs;
	}
}
