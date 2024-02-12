package wv.kmg.mapprinter.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import wv.kmg.mapprinter.tile.TileUrl;

public abstract class Config {
	protected Map<String,String> h = new HashMap<String,String>();
	protected static Map<String,TileUrl> tu = new HashMap<String,TileUrl>();
	public Config() {
		h.put("kakao.base", "ROADMAP");
		h.put("naver.base", "basic");
		h.put("vworld.base", "base");
		tu.put("kakao", new TileUrl() {

			@Override
			public String tileurlfunction(String url, int coord[]) {
				// TODO Auto-generated method stub
				Logger log = Logger.getLogger("TileUrl");
                log.info("xyz tile : [x="+coord[1]+",y="+coord[2]+",z="+coord[0]+"]");
				return url + (14 - coord[0]) + "/" + (-coord[2] - 1) + "/" + coord[1] + ".png";
			}
		});
		tu.put("naver", new TileUrl() {

			@Override
			public String tileurlfunction(String url, int coord[]) {
				// TODO Auto-generated method stub
				Logger log = Logger.getLogger("TileUrl");
				log.info("xyz tile : [x="+coord[1]+",y="+coord[2]+",z="+coord[0]+"]");
				return url+coord[0]+"/"+(coord[1])+"/"+coord[2]+".jpg";
			}
		});
		
		tu.put("vworld", new TileUrl() {
			
			@Override
			public String tileurlfunction(String url, int[] coord) {
				// TODO Auto-generated method stub
				Logger log = Logger.getLogger("TileUrl");
				log.info("xyz tile : [x="+coord[1]+",y="+coord[2]+",z="+coord[0]+"]");
				return url+coord[0]+"/"+(coord[1])+"/"+coord[2]+".png";
			}
		});
	}
	public String get(String value) {
		return h.get(value);
	}
	public TileUrl getTileUrl(String value) {
		return tu.get(value);
	}
	public static String getRequest(URL url) throws Exception {
		 
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = connection.getResponseCode();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer stringBuffer = new StringBuffer();
        String inputLine;

        while ((inputLine = bufferedReader.readLine()) != null)  {
            stringBuffer.append(inputLine);
        }
        bufferedReader.close();

        String response = stringBuffer.toString();
        return response;
	}
}
