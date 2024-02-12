package wv.kmg.mapprinter.layer;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;

import wv.kmg.mapprinter.GeoserverParam;
import wv.kmg.mapprinter.RequestParam;
import wv.kmg.mapprinter.ViewState;
import wv.kmg.mapprinter.proj.Projection;
import wv.kmg.mapprinter.proj.Reprojection;
import wv.kmg.mapprinter.tile.TileGrid;

public class WmsLayer extends ImageLayer {
	private final Logger LOG = Logger.getLogger("WmsLayer");
	String layerName;
	RequestParam param;
	public WmsLayer(String url, String layerName, String epsg,RequestParam param) throws Exception {
		super(url,epsg);
		// TODO Auto-generated constructor stub
		this.layerName = layerName;
		this.param = param;
		initRequestParam();
		LOG.info("wms layer > url :"+url+", layer name : "+layerName+", epsg: "+epsg);
	}

	public WmsLayer(String url, String layerName, String epsg, TileGrid tileGrid,ViewState viewState) throws Exception {
		super(url,epsg,tileGrid);
		// TODO Auto-generated constructor stub
		this.layerName = layerName;
		initRequestParam();
		LOG.info("wms layer > url :"+url+", layer name : "+layerName+", epsg: "+epsg);
	}
	public double[] getExtent(double[] extent,ViewState viewState) {
		Projection viewProj = viewState.getProjection();
		Projection thisProj = projection;
		if(Projection.equivalent(viewProj, thisProj)) {
			return extent;
		}else {
			Reprojection reproj = Reprojection.createInstance(viewProj.getCode(),thisProj.getCode());
			double[] tse1 = reproj.transform(extent[0],extent[1]);
			double[] tse2 = reproj.transform(extent[2],extent[3]);
			return new double[] {tse1[0],tse1[1],tse2[0],tse2[1]};
		}
	}
	public void initRequestParam() {
		TileGrid st = this.getTileGridForProjection(this.projection);
		param = new GeoserverParam();

		param.set("TILESORIGIN",String.join(",", Arrays.stream(st.getOrigin())
				.mapToObj(String::valueOf).toArray(String[]::new)));
		param.set("LAYERS",this.layerName);
		param.set("SRS",this.projection.getCode());

	}


	@Override
	public ReferencedEnvelope getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedImage getImage(int x, int y, int z, ViewState viewState) throws Exception {
		TileGrid st = this.getTileGridForProjection(this.projection);
		
		double[] te = getExtent(st.getTileCoordExtent(new int[] {z,x,y}), viewState);
		String[] ata = Arrays.stream(te)
		.mapToObj(String::valueOf).toArray(String[]::new);
		param.set("BBOX", String.join(",", ata));
		URL url = new URL(this.url+"?"+param.toParam());
		LOG.info("request url : "+url);
		BufferedImage tileImage = ImageIO.read(url);
		return tileImage;
	}

}
