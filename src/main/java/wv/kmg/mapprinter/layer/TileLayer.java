package wv.kmg.mapprinter.layer;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;

import wv.kmg.mapprinter.Cal;
import wv.kmg.mapprinter.Extent;
import wv.kmg.mapprinter.MapUtil;
import wv.kmg.mapprinter.Triangulation;
import wv.kmg.mapprinter.ViewState;
import wv.kmg.mapprinter.proj.Projection;
import wv.kmg.mapprinter.tile.ImageTile;
import wv.kmg.mapprinter.tile.TileGrid;
import wv.kmg.mapprinter.tile.TileRange;
import wv.kmg.mapprinter.tile.TileUrl;


public class TileLayer extends ImageLayer {
	private final Logger LOG = Logger.getLogger("TileLayer");
	TileUrl tileUrl;


	public TileLayer(String url,String epsg, TileUrl tileUrl,TileGrid tileGrid) throws Exception {
		super(url,epsg,tileGrid);
		this.tileUrl = tileUrl;
		LOG.info("base layer > url :"+url+", epsg : "+epsg);
	}
	@Override
	public ReferencedEnvelope getBounds() {
		return null;
//		double[] extent = this.viewState.getViewExtent();
//
//		// TODO Auto-generated method stub
//		Projection viewProj = this.viewState.getProjection();
//		try {
//			
////			extent = viewProj.transform(this.viewState.getViewExtent(), projection);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} ;
//		
//		return new ReferencedEnvelope(extent[0],extent[2],extent[1],extent[3], projection.getCoordinateReferenceSystem());
	}


	public BufferedImage getImage(int x, int y, int z,ViewState viewState) throws Exception{
		Projection viewProj = viewState.getProjection();
		Projection thisProj = projection;//this.getProjection();
		TileGrid vt = this.getTileGridForProjection(viewProj); //viewState.getTileGrid();
		TileGrid st = this.getTileGridForProjection(thisProj);//this.tileGrid;

		double resolution = viewState.getResolution();
		
		double[] maxTargetExtent = vt.getExtent();
		
		double[] targetExtent = vt.getTileCoordExtent(
			      new int[] {z,x,y}
			    );
	    double[] limitedTargetExtent = maxTargetExtent != null
	      ? Extent.getIntersection(targetExtent, maxTargetExtent,null)
	      : targetExtent;
	      
		double sourceResolution = TileGrid.calculateSourceExtentResolution(
				thisProj,
				viewProj,
			      limitedTargetExtent,
			      resolution
			    );

		int sourceZ_= st.getZForResolution(sourceResolution,0);

		int[] ts = st.getTileSize();
		BufferedImage clipImage = new BufferedImage(ts[0],ts[1], BufferedImage.SCALE_SMOOTH);
		if(Projection.equivalent(viewProj, thisProj)) {
//			String req_url =  url+z+"/"+y+"/"+x+".png";
			String req_url = null;
			if (this.tileUrl==null) {
				req_url = url+(sourceZ_)+"/"+(x)+"/"+y+".png";
    		}else {
    			req_url = this.tileUrl.tileurlfunction(this.url,new int[] {sourceZ_,x,y});
    		}
			URL url = new URL(req_url);
			try {
				Graphics2D g2d = clipImage.createGraphics();
				BufferedImage img = ImageIO.read(url);
				g2d.drawImage(img, 0,0,null);
				g2d.dispose();	
			}catch(Exception e) {
				LOG.info("Image get error : "+ e.getMessage());
			}
			
		}else {
			double errorThresholdInPixels = MapUtil.ERROR_THRESHOLD;
			double[] maxSourceExtent = st.getExtent();

			double[] te = vt.getTileCoordExtent(new int[] {z,x,y});

			double sz= st.getResolution(sourceZ_);
			
			Triangulation t = new Triangulation(thisProj.getCode(),viewProj.getCode(), te, maxSourceExtent, sourceResolution*errorThresholdInPixels, resolution);
			double[] sourceExtent = t.calculateSourceExtent();
			
			if(t.getTriangles().size() == 0) {
				return null;
			}
			if (st.getExtent() != null) {
				if (thisProj.canWrapX()) {
					sourceExtent[1] = Cal.clamp(sourceExtent[1], maxSourceExtent[1], maxSourceExtent[3]);
					sourceExtent[3] = Cal.clamp(sourceExtent[3], maxSourceExtent[1], maxSourceExtent[3]);
				} else {
					sourceExtent = Extent.getIntersection(sourceExtent, maxSourceExtent, null);
				}
			}
			TileRange tileRange = st.getTileRangeForExtentAndZ(sourceExtent,sourceZ_,null);

	        
			int xmin = tileRange.getMinX();
			int xmax = tileRange.getMaxX();
			int ymin = tileRange.getMinY();
			int ymax = tileRange.getMaxY();
			double[] sourceDataExtent = Extent.createEmpty();
			List<ImageTile> sources = new LinkedList<ImageTile>();

			
	        for(int i = xmin ;i <= xmax; i++) {

	        	for(int j = ymin ; j <= ymax;j++) {

	        		String sub_img_url;
	        		if (this.tileUrl==null) {
	        			sub_img_url = url+(sourceZ_)+"/"+(i)+"/"+j+".png";
	        		}else {
	        			sub_img_url = this.tileUrl.tileurlfunction(this.url,new int[] {sourceZ_,i,j});	
	        		}
	        		
	        		BufferedImage img = hit(sub_img_url);
	        		
	        		if (img == null) {
	        			URL url = new URL(sub_img_url);
						img = ImageIO.read(url);
						setCache(sub_img_url, img);

	        		}

	        		double[] se = st.getTileCoordExtent(new int[] {sourceZ_,i,j});
					sources.add(new ImageTile(img, se));
					
					
	        	}

	        }
			sources.forEach((src) -> {
				Extent.extend(sourceDataExtent, src.getExtent());
			});
			
			double canvasWidthInUnits = Extent.getWidth(sourceDataExtent);
			double canvasHeightInUnits = Extent.getHeight(sourceDataExtent);
			BufferedImage subImage = new BufferedImage((int)Math.round((MapUtil.pixelRatio * canvasWidthInUnits) / sz),
					(int)Math.round((MapUtil.pixelRatio * canvasHeightInUnits) / sz), BufferedImage.TYPE_INT_RGB);
			Graphics2D sg =subImage.createGraphics();
			sg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			double stitchScale = MapUtil.pixelRatio / sz;
			sources.forEach((src) -> {
				double xPos = src.getExtent()[0] - sourceDataExtent[0];
			    double yPos = -(src.getExtent()[3] - sourceDataExtent[3]);
			    double srcWidth = Extent.getWidth(src.getExtent());
			    double srcHeight = Extent.getHeight(src.getExtent());


			    if (src.getImage().getWidth()> 0 && src.getImage().getHeight() > 0) {
			    	int dx = (int)Math.round(xPos * stitchScale);
			    	int dy = (int)Math.round(yPos * stitchScale);
			      sg.drawImage(
			        src.getImage(),
			        dx,
			        dy,
			        dx+(int)Math.round(srcWidth * stitchScale),
			        dy+(int)Math.round(srcHeight * stitchScale),
			        0,
			        0,
			        src.getImage().getWidth(),
			        src.getImage().getHeight(),
			        null
			      );
			    }
			});
			
			
			double[] targetTopLeft = Extent.getTopLeft(te);

			t.getTriangles().forEach((triangle)->{

				
				double[][] source = triangle.getSource();
				double[][] target = triangle.getTarget();
			    double x0 = source[0][0];
			    double  y0 = source[0][1];
			    double x1 = source[1][0];
			    double y1 = source[1][1];
			    double x2 = source[2][0];
			    double y2 = source[2][1];
			    // Make sure that everything is on pixel boundaries
			    double u0 = MapUtil.pixelRound((target[0][0] - targetTopLeft[0]) / resolution,MapUtil.pixelRatio);
			    double v0 = MapUtil.pixelRound(-(target[0][1] - targetTopLeft[1]) / resolution,MapUtil.pixelRatio);
			    double u1 = MapUtil.pixelRound((target[1][0] - targetTopLeft[0]) / resolution,MapUtil.pixelRatio);
			    double v1 =  MapUtil.pixelRound(-(target[1][1] - targetTopLeft[1]) / resolution,MapUtil.pixelRatio);
			    double u2 = MapUtil.pixelRound((target[2][0] - targetTopLeft[0]) / resolution,MapUtil.pixelRatio);
			    double v2 = MapUtil.pixelRound(-(target[2][1] - targetTopLeft[1]) / resolution,MapUtil.pixelRatio);


			    double sourceNumericalShiftX = x0;
			    double sourceNumericalShiftY = y0;
			    x0 = 0;
			    y0 = 0;
			    x1 -= sourceNumericalShiftX;
			    y1 -= sourceNumericalShiftY;
			    x2 -= sourceNumericalShiftX;
			    y2 -= sourceNumericalShiftY;

			    double[][] augmentedMatrix = {
			      {x1, y1, 0, 0, u1 - u0},
			      {x2, y2, 0, 0, u2 - u0},
			      {0, 0, x1, y1, v1 - v0},
			      {0, 0, x2, y2, v2 - v0},
			    };
			    double[] affineCoefs = Cal.solveLinearSystem(augmentedMatrix);
			    if (affineCoefs == null) {
			      return;
			    }
			    GeneralPath clip = new GeneralPath();
			    clip.moveTo(u1, v1);
			    clip.lineTo(u0, v0);
			    clip.lineTo(u2, v2);
			    clip.closePath();
			    AffineTransform af = new AffineTransform(affineCoefs[0],affineCoefs[1],affineCoefs[2],affineCoefs[3],u0,v0);

			    Rectangle bounds = clip.getBounds();
			    
			    Graphics2D g2d = clipImage.createGraphics();
			    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			    g2d.setClip(clip);
			    g2d.transform(af);
			    g2d.translate(
					      sourceDataExtent[0] - sourceNumericalShiftX,
					      sourceDataExtent[3] - sourceNumericalShiftY 		
			    		);
			    g2d.scale(
			      sz / MapUtil.pixelRatio,
			      -sz / MapUtil.pixelRatio
			    );
			    g2d.drawImage(subImage, 0, 0, null);
			    g2d.dispose();
			    
			});		
		}
		
		return clipImage;
	}

}
