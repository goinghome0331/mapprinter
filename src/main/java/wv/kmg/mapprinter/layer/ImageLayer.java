package wv.kmg.mapprinter.layer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;

import wv.kmg.mapprinter.Extent;
import wv.kmg.mapprinter.Transform;
import wv.kmg.mapprinter.ViewState;
import wv.kmg.mapprinter.proj.Projection;
import wv.kmg.mapprinter.tile.TileGrid;
import wv.kmg.mapprinter.tile.TileRange;

public abstract class ImageLayer extends DirectLayer {

	Map<String,BufferedImage> cache = new HashMap<String, BufferedImage>();
	String url;
	Projection projection;
	TileGrid tileGrid;
	ViewState viewState;

	public void setCache(String url, BufferedImage image) {
		cache.put(url, image);
	}

	public BufferedImage hit(String url) {
		return cache.get(url);
	}

	public ImageLayer(String url, String epsg) throws Exception {
		this.projection = Projection.getInstance(epsg);
		this.url = url;
	}

	public ImageLayer(String url, String epsg, TileGrid tileGrid) throws Exception {
		this(url, epsg);
		this.tileGrid = tileGrid;
	}

	// projection에 대한 tilegrid 얻기
	public TileGrid getTileGridForProjection(Projection projection) {
		if (this.tileGrid != null && (this.projection != null && Projection.equivalent(this.projection, projection))) {
			return this.tileGrid;
		} else {
			return TileGrid.getTileGridForProjection(projection);
		}
	}

	// pixel 단위의 BufferedImage 위에 tile 위치 계산 및 그리기
	@Override
	public void draw(Graphics2D g, MapContent m, MapViewport mv) {
		// TODO Auto-generated method stub
		double pixelRatio = 1;
		double tilePixelRatio = 1;
		ViewState viewState = (ViewState) mv;
		Projection viewProj = viewState.getProjection();
		Projection thisProj = projection;// this.getProjection();

		double viewResolution = viewState.getResolution();

		double[] extent = viewState.getViewExtent();
		TileGrid tilegrid = this.getTileGridForProjection(viewProj);
		int z = tilegrid.getZForResolution(viewResolution, 0);
		double tileResolution = tilegrid.getResolution(z);
		TileRange tileRange = tilegrid.getTileRangeForExtentAndZ(extent, z, null);
		int[] tileSize = tilegrid.getTileSize();

		double[] ve = viewState.getViewExtent();

		double[] center = viewState.getCenter();
		double dx = (tileResolution * (extent[2] - extent[0]) / viewResolution) / 2;
		double dy = (tileResolution * (extent[3] - extent[1]) / viewResolution) / 2;
		double[] canvasExtent = new double[] { center[0] - dx, center[1] - dy, center[0] + dx, center[1] + dy, };

		double width = Math.round(Math.abs(ve[0] - ve[2]) / viewState.getResolution());
		double height = Math.round(Math.abs(ve[1] - ve[3]) / viewState.getResolution());

		double[] originTileCoord = tilegrid.getTileCoordForCoordAndZ(Extent.getTopLeft(canvasExtent), z, null);
		double[] originTileExtent = tilegrid.getTileCoordExtent(
				new int[] { (int) originTileCoord[0], (int) originTileCoord[1], (int) originTileCoord[2] });
		double[] tempTransform = new double[] { 1, 0, 0, 1, 0, 0 };
		double canvasScale = ((tileResolution / viewResolution) * pixelRatio) / tilePixelRatio;
		;
		double currentScale = 1;
		int[] currentTilePixelSize = tilegrid.getTileSize();

		dx = currentTilePixelSize[0] * currentScale * canvasScale;
		dy = currentTilePixelSize[1] * currentScale * canvasScale;

		Transform.compose(tempTransform, width / 2, height / 2, canvasScale, canvasScale, 0, -width / 2, -height / 2);

		double[] origin = Transform.apply(tempTransform,
				new double[] { (tilePixelRatio * (originTileExtent[0] - canvasExtent[0])) / tileResolution, // resolution,
						(tilePixelRatio * (canvasExtent[3] - originTileExtent[3])) / tileResolution,// resolution,
				});
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		for (int tx = tileRange.getMinX(); tx <= tileRange.getMaxX(); tx++) {
			for (int ty = tileRange.getMinY(); ty <= tileRange.getMaxY(); ty++) {
				try {

					int xIndex = (int) originTileCoord[1] - tx;// tileCoord[1];
					int nextX = (int) Math.round(origin[0] - (xIndex - 1) * dx);
					int yIndex = (int) originTileCoord[2] - ty;// tileCoord[2];
					int nextY = (int) Math.round(origin[1] - (yIndex - 1) * dy);
					int x = (int) Math.round(origin[0] - xIndex * dx);
					int y = (int) Math.round(origin[1] - yIndex * dy);
					int w = nextX - x;
					int h = nextY - y;

					BufferedImage tileImage = getImage(tx, ty, z, viewState);

					g.drawImage(tileImage, x, y, w, h, null);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	// 특정 타일 위치에 관한 이미지 얻기
	public abstract BufferedImage getImage(int x, int y, int z, ViewState viewState) throws Exception;
}
