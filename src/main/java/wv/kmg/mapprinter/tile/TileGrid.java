package wv.kmg.mapprinter.tile;

import java.util.Arrays;

import wv.kmg.mapprinter.Cal;
import wv.kmg.mapprinter.Extent;
import wv.kmg.mapprinter.MapUtil;
import wv.kmg.mapprinter.Sphere;
import wv.kmg.mapprinter.proj.METERS_PER_UNIT;
import wv.kmg.mapprinter.proj.Projection;
import wv.kmg.mapprinter.proj.Reprojection;

public class TileGrid {
	private double[] origin;
	private double[] extent;
	private double[] resolutions;
	private int[] tileSize;
	
	private static double[] tmpTileCoord = {0,0,0};
	public TileGrid(double[] origin, double[] extent, double[] resolutions, int[] tileSize) {
		this.origin = origin;
		this.extent = extent;
		this.resolutions = resolutions;
		this.tileSize = tileSize;
	}
	
	public double[] getResolutions() {
		return resolutions;
	}
	public double getResolution(int z) {
	    return this.resolutions[z];
	}
	
	public double[] getOrigin() {
		return origin;
	}

	public double[] createOrUpdate(int z, int x, int y, double[] tileCoord) {
		if (tileCoord != null) {
			tileCoord[0] = z;
			tileCoord[1] = x;
			tileCoord[2] = y;
			return tileCoord;
		} else {
			return new double[] { z, x, y };
		}
	}

	public TileRange createOrUpdateRange(int minX, int maxX, int minY, int maxY, TileRange tileRange) {
		if (tileRange != null) {
			tileRange.setMinX(minX);
			;
			tileRange.setMaxX(maxX);
			tileRange.setMinY(minY);
			tileRange.setMaxY(maxY);
			return tileRange;
		} else {
			return new TileRange(minX, maxX, minY, maxY);
		}
	}

	public double[] getTileCoordForCoordAndZ(double[] coordinate, int z, double[] opt_tileCoord) {
		return this.getTileCoordForXYAndZ_(coordinate[0], coordinate[1], z, false, opt_tileCoord);
	}
	public double[] getTileCoordForXYAndZ_(double x, double y, int z, boolean reverseIntersectionPolicy,
			double[] opt_tileCoord) {

		double tileCoordX = (x - this.origin[0]) / this.resolutions[z] / this.tileSize[0];
		double tileCoordY = (this.origin[1] - y) / this.resolutions[z] / this.tileSize[1];
		int tileX = (int) Math.floor(tileCoordX);
		int tileY = (int) Math.floor(tileCoordY);
		if (reverseIntersectionPolicy) {
			tileCoordX = Cal.ceil(tileCoordX, Cal.DECIMALS) - 1;
			tileCoordY = Cal.ceil(tileCoordY, Cal.DECIMALS) - 1;
		} else {
			tileCoordX = Cal.floor(tileCoordX, Cal.DECIMALS);
			tileCoordY = Cal.floor(tileCoordY, Cal.DECIMALS);
		}

		return createOrUpdate(z, (int) tileCoordX, (int) tileCoordY, opt_tileCoord);
	}

	public TileRange getTileRangeForExtentAndZ(double[] extent, int z, TileRange tempTileRange) {
		double[] tileCoord = tmpTileCoord;
		this.getTileCoordForXYAndZ_(extent[0], extent[3], z, false, tileCoord);
		double minX = tileCoord[1];
		double minY = tileCoord[2];
		this.getTileCoordForXYAndZ_(extent[2], extent[1], z, true, tileCoord);
		return createOrUpdateRange((int) minX, (int) tileCoord[1], (int) minY, (int) tileCoord[2], tempTileRange);
	}
	public int[] calcTile(double x, double y, double resolution) {
		int z = getZForResolution(resolution,0);
		
		double tileCoordX = (x - this.origin[0]) / this.resolutions[z] / this.tileSize[0];
		double tileCoordY = (this.origin[1] - y) / this.resolutions[z] / this.tileSize[1];
		int tileX = (int)Math.floor(tileCoordX);
		int tileY = (int)Math.floor(tileCoordY);
		return new int[] {z,tileX,tileY};
	}
	public int getZForResolution(double target,int direction) {
//		if(target >= this.resolutions[0]) {
//			return 0;
//		}else if(target < this.resolutions[this.resolutions.length - 1]) {
//			return this.resolutions.length - 1;
//		}else {
//			int m_dif = Integer.MAX_VALUE;
//			int idx = this.resolutions.length - 1;
//			
//			for(int i = 0 ; i < this.resolutions.length;i++) {
//				int dif = (int)Math.abs(target - this.resolutions[i]);
//				if(m_dif > dif) {
//					m_dif = dif;
//					idx = i;
//				}
//			}
//			
//			return idx;
//		}
		
		int n = this.resolutions.length;
		  if (this.resolutions[0] <= target) {
		    return 0;
		  } else if (target <= this.resolutions[n - 1]) {
		    return n - 1;
		  } else {
		    int i;
		    if (direction > 0) {
		      for (i = 1; i < n; ++i) {
		        if (this.resolutions[i] < target) {
		          return i - 1;
		        }
		      }
		    } else if (direction < 0) {
		      for (i = 1; i < n; ++i) {
		        if (this.resolutions[i] <= target) {
		          return i;
		        }
		      }
		    } else {
		      for (i = 1; i < n; ++i) {
		        if (this.resolutions[i] == target) {
		          return i;
		        } else if (this.resolutions[i] < target) {
		          if (this.resolutions[i - 1] - target < target - this.resolutions[i]) {
		            return i - 1;
		          } else {
		            return i;
		          }
		        }
		      }
		    }
		    return n - 1;
		  }
	}
//	public double[] getTileCoordExtent(TileCoord tileCoord) {
//		double resolution = this.resolutions[tileCoord.getZ()];
//		double minX = this.origin[0] + tileCoord.getX() * tileSize[0] * resolution;
//		double minY = origin[1] - (tileCoord.getY() + 1) * tileSize[1] * resolution;
//		double maxX = minX + tileSize[0] * resolution;
//		double maxY = minY + tileSize[1] * resolution;
//		
//		return new double[] {minX,minY,maxX,maxY};
//	}
	public double[] getTileCoordExtent(int[] tileCoord) {
		double resolution = this.resolutions[tileCoord[0]];
		double minX = this.origin[0] + tileCoord[1] * tileSize[0] * resolution;
		double minY = origin[1] - (tileCoord[2] + 1) * tileSize[1] * resolution;
		double maxX = minX + tileSize[0] * resolution;
		double maxY = minY + tileSize[1] * resolution;
		
		return new double[] {minX,minY,maxX,maxY};
	}
	
	public int[] getTileSize() {
		return tileSize;
	}

	public double[] getExtent() {
		return extent;
	}

	public static TileGrid createForProjection(Projection projection, int maxZoom, int[] tileSize, String corner) {
		double[] extent = extentFromProjection(projection);
		return createForExtent(extent, maxZoom, tileSize, corner);
	}

	public static TileGrid createForProjection(Projection projection) {
		return createForProjection(projection, -1, null, null);
	}
	
	public static double[] extentFromProjection(Projection projection) {

		double[] extent = projection.getExtent();
		if (extent == null) {
			double half = (180 * METERS_PER_UNIT.degrees) / METERS_PER_UNIT.m;
			extent = Extent.createOrUpdate(-half, -half, half, half, null);
		}
		return extent;
	}
	
	public static TileGrid createForExtent(double[] extent, int maxZoom, int[] tileSize, String corner) {
		corner = corner != null ? corner : "top-left";
		maxZoom = maxZoom > 0 ? maxZoom : MapUtil.DEFAULT_MAX_ZOOM;
		tileSize = tileSize != null ? tileSize : new int[] { MapUtil.DEFAULT_TILE_SIZE, MapUtil.DEFAULT_TILE_SIZE };

		double[] resolutions = resolutionsFromExtent(extent, maxZoom, tileSize, -1);

		return new TileGrid(Extent.getCorner(extent, corner), extent, resolutions, tileSize);
	}
	
	public static double[] resolutionsFromExtent(double[] extent, int maxZoom, int[] tileSize, double maxResolution) {

		double height = Extent.getHeight(extent);
		double width = Extent.getWidth(extent);

		maxResolution = maxResolution > 0 ? maxResolution : Math.max(width / tileSize[0], height / tileSize[1]);
		
		int length = maxZoom + 1;
		double[] resolutions = new double[length];
		for (int z = 0; z < length; ++z) {
			resolutions[z] = maxResolution / Math.pow(2, z);
		}
		return resolutions;
	}
	public static TileGrid getTileGridForProjection(Projection projection) {
		if(projection != null) {
			  TileGrid tileGrid = projection.getDefaultTileGrid();
			  if (tileGrid == null) {
			    tileGrid = TileGrid.createForProjection(projection);
			    projection.setDefaultTileGrid(tileGrid);
			  }
			  return tileGrid;
			}
		return null;
	}
	
	public static double getPointResolution(Projection projection, double resolution, double[] point, String units) throws Exception {
//		  projection = get(projection);
		double pointResolution = 0;
//		  const getter = projection.getPointResolutionFunc();
		Object getter = null;
		if (getter != null) {
//		    pointResolution = getter(resolution, point);
//		    if (units && units !== projection.getUnits()) {
//		      const metersPerUnit = projection.getMetersPerUnit();
//		      if (metersPerUnit) {
//		        pointResolution =
//		          (pointResolution * metersPerUnit) / METERS_PER_UNIT[units];
//		      }
//		    }
		} else {
			String projUnits = projection.getUnits();
			if ((projUnits.equals("degrees") && units != null) || "degrees".equals(units)) {
				pointResolution = resolution;
			} else {
				// Estimate point resolution by transforming the center pixel to EPSG:4326,
				// measuring its width and height on the normal sphere, and taking the
				// average of the width and height.
//		      const toEPSG4326 = getTransformFromProjections(
//		        projection,
//		        get('EPSG:4326')
//		      );
				Reprojection toEPSG4326 = Reprojection.createInstance(projection.getCode(), "EPSG:4326");
				if (projection.getCode().equals("EPSG:4326") && !"degrees".equals(projUnits)) {
					// no transform is available
					pointResolution = resolution * projection.getMetersPerUnit();
				} else {
					Projection ePSG4326 = Projection.getInstance("EPSG:4326");
					double[] vertices = new double[] { point[0] - resolution / 2, point[1], point[0] + resolution / 2,
							point[1], point[0], point[1] - resolution / 2, point[0], point[1] + resolution / 2, };
					double[] w1 = toEPSG4326.transform(vertices[0], vertices[1]);
					double[] w2 = toEPSG4326.transform(vertices[2], vertices[3]);
					double[] h1 = toEPSG4326.transform(vertices[4], vertices[5]);
					double[] h2 = toEPSG4326.transform(vertices[6], vertices[7]);
					
					
					
					
//					double[] w1 = projection.transform(vertices[0],vertices[1],ePSG4326);
//					double[] w2 = projection.transform(vertices[2],vertices[3],ePSG4326);
//					double[] h1 = projection.transform(vertices[4],vertices[5],ePSG4326);
//					double[] h2 = projection.transform(vertices[6],vertices[7],ePSG4326);
//		        vertices = toEPSG4326(vertices, vertices, 2);
					double width = Sphere.getDistance(w1, w2, null);
					double height = Sphere.getDistance(h1, h2, null);
					pointResolution = (width + height) / 2;
				}
//		      double metersPerUnit = units
//		    		  
//		        ? METERS_PER_UNIT[units]
//		        : projection.getMetersPerUnit();
				double metersPerUnit = projection.getMetersPerUnit();
//		      if (metersPerUnit != null) {
//		        pointResolution /= metersPerUnit;
//		      }
				pointResolution /= metersPerUnit;
			}
		}
		return pointResolution;
	}

	public static double calculateSourceResolution(Projection sourceProj, Projection targetProj, double[] targetCenter,
			double targetResolution) throws Exception {
		double[] sourceCenter = Reprojection.createInstance(targetProj.getCode(),sourceProj.getCode() ).transform(targetCenter);
//		double[] sourceCenter = targetProj.transform(targetCenter, sourceProj);
//			  const sourceCenter = transform(targetCenter, targetProj, sourceProj);

		// calculate the ideal resolution of the source data
		double sourceResolution = getPointResolution(targetProj, targetResolution, targetCenter, null);

		double targetMetersPerUnit = targetProj.getMetersPerUnit();
//			  if (targetMetersPerUnit !== undefined) {
//			    sourceResolution *= targetMetersPerUnit;
//			  }
		sourceResolution *= targetMetersPerUnit;
		double sourceMetersPerUnit = sourceProj.getMetersPerUnit();
//			  if (sourceMetersPerUnit !== undefined) {
//			    sourceResolution /= sourceMetersPerUnit;
//			  }
		sourceResolution /= sourceMetersPerUnit;



		double[] sourceExtent = sourceProj.getExtent();

		if (sourceExtent == null || Extent.containsCoordinate(sourceExtent, sourceCenter)) {
			double compensationFactor = getPointResolution(sourceProj, sourceResolution, sourceCenter, null)
					/ sourceResolution;
			if (Double.isFinite(compensationFactor) && compensationFactor > 0) {
				sourceResolution /= compensationFactor;
			}
		}

		return sourceResolution;
	}

	public static double calculateSourceExtentResolution(Projection sourceProj, Projection targetProj,
			double[] targetExtent, double targetResolution) throws Exception {
		double[] targetCenter = Extent.getCenter(targetExtent);
		double sourceResolution = calculateSourceResolution(sourceProj, targetProj, targetCenter, targetResolution);

//			  if (!Double.isFinite(sourceResolution) || sourceResolution <= 0) {
//			    forEachCorner(targetExtent, function (corner) {
//			      sourceResolution = calculateSourceResolution(
//			        sourceProj,
//			        targetProj,
//			        corner,
//			        targetResolution
//			      );
//			      return isFinite(sourceResolution) && sourceResolution > 0;
//			    });
//			  }

		return sourceResolution;
	}
	@Override
	public String toString() {
		return "TileGrid [origin=" + Arrays.toString(origin) + ", extent=" + Arrays.toString(extent) + ", resolutions="
				+ Arrays.toString(resolutions) + ", tileSize=" + Arrays.toString(tileSize) + "]";
	}

}
