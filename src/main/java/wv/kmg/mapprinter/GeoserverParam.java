package wv.kmg.mapprinter;

public class GeoserverParam extends RequestParam{
	public GeoserverParam() {
		m.put("SERVICE","WMS");
		m.put("VERSION","1.1.0");
		m.put("REQUEST","GetMap");
		m.put("FORMAT","image/png");
		m.put("TRANSPARENT","true");
		m.put("WIDTH","256");
		m.put("HEIGHT","256");
		m.put("TILED","true");
		m.put("VERSION","1.1.1");
		m.put("STYLES","");
	}

}
