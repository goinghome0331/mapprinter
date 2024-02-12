package wv.kmg.mapprinter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

import wv.kmg.mapprinter.config.Config;
import wv.kmg.mapprinter.layer.LayerFactory;
import wv.kmg.mapprinter.layer.TileLayer;
import wv.kmg.mapprinter.tile.TileGrid;




public class App {
	public static void main(String args[]) throws Exception{
		// 배경지도 및 view 기본 설정
		String base_name = System.getProperty("base","kakao");
		String proj = System.getProperty("proj","EPSG:5174");
		int zoom = Integer.parseInt(System.getProperty("zoom","14"));
		String layers = System.getProperty("layers");
		// center, width, height를 기반으로 extent를 계산한다.(width,heigth는 px 단위이다.)
		int width = Integer.parseInt(System.getProperty("width","1920"));
		int heght = Integer.parseInt(System.getProperty("height","879"));
		double center_x = Double.parseDouble(System.getProperty("center_x","199466.4328413511"));
		double center_y = Double.parseDouble(System.getProperty("center_y","441252.25252770627"));
		
		MapContent map = new MapContent();
		
		double[] center = {center_x, center_y};
		double[] size = {width,heght};
		View view = new View(proj,zoom,center,size);
		
		map.setViewport(view);
		// view의 extent 설정(extent가 설정되면 center보다 우선한다.)
		String vme = System.getProperty("extent");
		if(vme != null) {
			String[] ve = vme.split(",");
			if(ve.length == 4) {
				double[] extent = new double[4];
				for(int i = 0 ; i < ve.length; i++) {
					extent[i] = Double.parseDouble(ve[i].trim());
				}	
				view.setViewBounds(extent);
			}
			
		}
		// application.properties를 기반으로 설정정보를 기반으로 배경 레이어 생성
        Properties props = readconfig();
        Class<?> cc = Class.forName(props.getProperty(base_name+".config"));
		final Config config = (Config)cc.getDeclaredConstructor(Properties.class).newInstance(props);
		Layer base = new TileLayer(
				config.get(config.get(base_name+".base")),
				props.getProperty(base_name+".projection"),  
				config.getTileUrl(base_name),
				props.getProperty(base_name+".tilegrid").equals("true") ?
				new TileGrid(stringToDouble(props.getProperty(base_name+".tilegrid.origin")), stringToDouble(props.getProperty(base_name+".tilegrid.extent")),
						stringToDouble(props.getProperty(base_name+".tilegrid.resolutions")),
						stringToInt(props.getProperty(base_name+".tilegrid.tilesize"))) : null);
		map.addLayer(base);
		if(layers != null) {// vm arguments에 입력된 layer 정보를 기반으로 레이어를 생성한다.
			String[] la = layers.split(",");
			
			for(String layername : la) {
				Layer layer = LayerFactory.createLayer(layername,view);
				if(layer != null) {
					map.addLayer(layer);
				}
				
			}
		}


		// rendering 코드
		GTRenderer renderer = new StreamingRenderer();
	    renderer.setMapContent(map);
	    
	    Rectangle imageBounds = null;
	    
	    // view extent를 기반으로 이미지 사이즈를 정해야한다. 그렇지 않으면 배경맵은 잘리며, feature 데이터는 왜곡된다.
	    double[] ve = view.getViewExtent();
	    double[] msize = new double[] {Math.round((ve[2]-ve[0])/view.getResolution()),Math.round((ve[3]-ve[1])/view.getResolution())};
	    
	    ReferencedEnvelope mapBounds = null;
	    try {

	    	mapBounds = map.getViewport().getBounds();

	        imageBounds = new Rectangle(
	                0, 0, (int)msize[0], (int)msize[1]);//

	    } catch (Exception e) {
	        // failed to access map layers
	        throw new RuntimeException(e);
	    }
	    
	    BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_RGB);

	    Graphics2D gr = image.createGraphics();

	    try {


	        renderer.paint(gr, imageBounds, mapBounds);
	        File fileToSave = new File("./result.png");

	        ImageIO.write(image, "png", fileToSave);
	        
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
		
	}
	
	public static Properties readconfig() throws Exception {
		String resource = "res/application.properties";
        Properties properties = new Properties();
        InputStream is = new FileInputStream(new File(resource));
        properties.load(is);
        return properties;
	}
	public static double[] stringToDouble(String stringarray) {
		return Arrays.stream(stringarray.split(",")).mapToDouble(Double::parseDouble).toArray();
	}
	public static int[] stringToInt(String stringarray) {
		return Arrays.stream(stringarray.split(",")).mapToInt(Integer::parseInt).toArray();
	}
}
