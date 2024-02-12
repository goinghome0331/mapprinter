package wv.kmg.mapprinter.config;

import java.util.Arrays;

public class NaverJson {
	String attribution;
	double[] bounds;
	double[] center;
	String format;
	int limitzoom;
	int maxzoom;
	int minzoom;
	String name;
	String scheme;
	String tilejson;
	String[] tiles;
	String version;
	public NaverJson() {
		
	}
	public NaverJson(String attribution, double[] bounds, double[] center, String format, int limitzoom, int maxzoom,
			int minzoom, String name, String scheme, String tilejson, String[] tiles, String version) {
		super();
		this.attribution = attribution;
		this.bounds = bounds;
		this.center = center;
		this.format = format;
		this.limitzoom = limitzoom;
		this.maxzoom = maxzoom;
		this.minzoom = minzoom;
		this.name = name;
		this.scheme = scheme;
		this.tilejson = tilejson;
		this.tiles = tiles;
		this.version = version;
	}
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public double[] getBounds() {
		return bounds;
	}
	public void setBounds(double[] bounds) {
		this.bounds = bounds;
	}
	public double[] getCenter() {
		return center;
	}
	public void setCenter(double[] center) {
		this.center = center;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getLimitzoom() {
		return limitzoom;
	}
	public void setLimitzoom(int limitzoom) {
		this.limitzoom = limitzoom;
	}
	public int getMaxzoom() {
		return maxzoom;
	}
	public void setMaxzoom(int maxzoom) {
		this.maxzoom = maxzoom;
	}
	public int getMinzoom() {
		return minzoom;
	}
	public void setMinzoom(int minzoom) {
		this.minzoom = minzoom;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getTilejson() {
		return tilejson;
	}
	public void setTilejson(String tilejson) {
		this.tilejson = tilejson;
	}
	public String[] getTiles() {
		return tiles;
	}
	public void setTiles(String[] tiles) {
		this.tiles = tiles;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "NaverJson [attribution=" + attribution + ", bounds=" + Arrays.toString(bounds) + ", center="
				+ Arrays.toString(center) + ", format=" + format + ", limitzoom=" + limitzoom + ", maxzoom=" + maxzoom
				+ ", minzoom=" + minzoom + ", name=" + name + ", scheme=" + scheme + ", tilejson=" + tilejson
				+ ", tiles=" + Arrays.toString(tiles) + ", version=" + version + "]";
	}
	
	
}
