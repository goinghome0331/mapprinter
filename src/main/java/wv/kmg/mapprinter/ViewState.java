package wv.kmg.mapprinter;

import wv.kmg.mapprinter.proj.Projection;

public interface ViewState {
	public Projection getProjection();
	public int getCurrentZoom();
	public double getResolution();
	public double[] getViewExtent();
	public void setViewBounds(double[] extent);
	public double[] getCenter();

}
