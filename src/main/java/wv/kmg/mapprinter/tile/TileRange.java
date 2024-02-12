package wv.kmg.mapprinter.tile;

public class TileRange {
	int minX,maxX,minY,maxY;

	public TileRange(int minX, int maxX, int minY, int maxY) {
		super();
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	public int getMinX() {
		return minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	@Override
	public String toString() {
		return "TileRange [minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY + "]";
	}
	
}
