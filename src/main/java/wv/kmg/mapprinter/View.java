package wv.kmg.mapprinter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;

import wv.kmg.mapprinter.proj.METERS_PER_UNIT;
import wv.kmg.mapprinter.proj.Projection;






public class View extends MapViewport implements ViewState{
	private final Logger LOG = Logger.getLogger("View");
	Projection projection;
	int currentZoom;
	double[] extent;
	double[] canvasExtent;
	double rotation;
	double[] center;
	int[] tileSize;
	double maxResolution_;
	double minResolution_;
	int minZoom_;
	double zoomFactor_;
	public View(String epsg, int currentZoom, double[] center,double[] size) throws Exception {
		super();
		
		this.projection = Projection.getInstance(epsg);
		this.rotation = 0;
		this.center = center;
		// cneter와 resolution과 가로, 세로 길이로 extent 구하기)
		
		setCurrentZoom(currentZoom);
		
		this.extent = Extent.getForViewAndSize(center, getResolutionForZoom(currentZoom), this.rotation, size, null);
		this.setViewBounds(this.extent);
		
	}

	public void setExtent(double[] extent) {
		this.extent = extent;
	}
	public void setCurrentZoom(int currentZoom) throws Exception {
		this.currentZoom = currentZoom;
		setConstraint(center,projection,currentZoom);
	}
	public void setConstraint(double[] center, Projection projection, int zoom) throws Exception {
		Map<String,Object> constraint = createResolutionConstraint(center,projection.getCode(),currentZoom);
		maxResolution_ = (double)constraint.get("maxResolution");
		minResolution_ = (double)constraint.get("minResolution");
		minZoom_ = (int)constraint.get("minZoom");
		zoomFactor_ = (double)constraint.get("zoomFactor");
	}
	public int getCurrentZoom() {
		return currentZoom;
	}


	public double[] getCenter() {
		return this.center;
		//return Extent.getCenter(this.extent);
	}
	public double[] getViewExtent() {
		return this.extent;
	}
	public Projection getProjection() {
		// TODO Auto-generated method stub
		return this.projection;
	}
	public double getResolution() {
		return getResolutionForZoom(currentZoom);
	}
	@Override
	// view bounds(extent) 설정하기
	public void setViewBounds(double[] extent) {
		this.extent = extent;
		this.center = Extent.getCenter(extent);
		
		try {
			setCurrentZoom(this.currentZoom);
			this.setBounds(new ReferencedEnvelope(extent[0],extent[2],extent[1],extent[3],projection.getCoordinateReferenceSystem()));
			LOG.info("view epsg:"+projection.getCode()+", zoom:"+currentZoom+", extent:"+Arrays.toString(this.extent)+" center:"+Arrays.toString(this.center));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public double getResolutionForZoom(int zoom) {
		return this.maxResolution_ / Math.pow(this.zoomFactor_, zoom - this.minZoom_);
	}

	public Map<String, Object> createResolutionConstraint(double[] center, String epsg, int zoom) throws Exception {
//		  let resolutionConstraint;
		double maxResolution;
		double minResolution;

		// TODO: move these to be ol constants
		// see https://github.com/openlayers/openlayers/issues/2076
		int defaultMaxZoom = 28;
		int defaultZoomFactor = 2;

		int minZoom = MapUtil.DEFAULT_MIN_ZOOM;

		int maxZoom = defaultMaxZoom;

		double zoomFactor = defaultZoomFactor;

		boolean multiWorld = false;

		boolean smooth = true;

		boolean showFullExtent = false;

//		  const projection = createProjection(options.projection, 'EPSG:3857');
		Projection projection = Projection.getInstance(epsg);
		double[] projExtent = projection.getExtent();
		boolean constrainOnlyCenter = false;
		double[] extent = null;
		if (!multiWorld && extent != null && projection.isGlobal()) {
			constrainOnlyCenter = false;
			extent = projExtent;
		}

//		  if (options.resolutions !== undefined) {
//		    const resolutions = options.resolutions;
//		    maxResolution = resolutions[minZoom];
//		    minResolution =
//		      resolutions[maxZoom] !== undefined
//		        ? resolutions[maxZoom]
//		        : resolutions[resolutions.length - 1];
//
//		    if (options.constrainResolution) {
//		      resolutionConstraint = createSnapToResolutions(
//		        resolutions,
//		        smooth,
//		        !constrainOnlyCenter && extent,
//		        showFullExtent
//		      );
//		    } else {
//		      resolutionConstraint = createMinMaxResolution(
//		        maxResolution,
//		        minResolution,
//		        smooth,
//		        !constrainOnlyCenter && extent,
//		        showFullExtent
//		      );
//		    }
//		  } else {
//		    
//		  }
		// calculate the default min and max resolution
		double size = projExtent == null ? // use an extent that can fit the whole world if need be
				(360 * METERS_PER_UNIT.degrees) / projection.getMetersPerUnit()
				: Math.max(Extent.getWidth(projExtent), Extent.getHeight(projExtent));

		double defaultMaxResolution = size / MapUtil.DEFAULT_TILE_SIZE
				/ Math.pow(defaultZoomFactor, MapUtil.DEFAULT_MIN_ZOOM);

		double defaultMinResolution = defaultMaxResolution
				/ Math.pow(defaultZoomFactor, defaultMaxZoom - MapUtil.DEFAULT_MIN_ZOOM);

		// user provided maxResolution takes precedence
		maxResolution = -1;
		if (maxResolution != -1) {
			minZoom = 0;
		} else {
			maxResolution = defaultMaxResolution / Math.pow(zoomFactor, minZoom);
		}

		// user provided minResolution takes precedence
		minResolution = -1;
		if (minResolution == -1) {
//		      if (options.maxZoom !== undefined) {
//		        if (options.maxResolution !== undefined) {
//		          minResolution = maxResolution / Math.pow(zoomFactor, maxZoom);
//		        } else {
//		          minResolution = defaultMaxResolution / Math.pow(zoomFactor, maxZoom);
//		        }
//		      } else {
//		        minResolution = defaultMinResolution;
//		      }
			minResolution = defaultMinResolution;
		}

		// given discrete zoom levels, minResolution may be different than provided
		maxZoom = minZoom
				+ (int) Math.round(Math.floor(Math.log(maxResolution / minResolution) / Math.log(zoomFactor)));
		minResolution = maxResolution / Math.pow(zoomFactor, maxZoom - minZoom);

//		    if (options.constrainResolution) {
//		      resolutionConstraint = createSnapToPower(
//		        zoomFactor,
//		        maxResolution,
//		        minResolution,
//		        smooth,
//		        !constrainOnlyCenter && extent,
//		        showFullExtent
//		      );
//		    } else {
//		      resolutionConstraint = createMinMaxResolution(
//		        maxResolution,
//		        minResolution,
//		        smooth,
//		        !constrainOnlyCenter && extent,
//		        showFullExtent
//		      );
//		    }
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("maxResolution", maxResolution);
		m.put("minResolution", minResolution);
		m.put("minZoom", minZoom);
		m.put("zoomFactor", zoomFactor);
		return m;
	}


}
