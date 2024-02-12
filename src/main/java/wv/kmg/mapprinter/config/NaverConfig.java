package wv.kmg.mapprinter.config;

import java.net.URL;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NaverConfig extends Config {

	public NaverConfig(Properties props) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		URL url = new URL(props.getProperty("naver.info")+props.getProperty("naver.type")+".json");
		String config = getRequest(url);
		ObjectMapper objectMapper = new ObjectMapper(); 
		NaverJson naverJson = objectMapper.readValue(config, NaverJson.class);
		int end = naverJson.getTiles()[0].indexOf("{z}/{x}/{y}.jpg");
		String img_url = naverJson.getTiles()[0].substring(0, end);
		h.put(props.getProperty("naver.type"), img_url);
		h.put("format", naverJson.getFormat());
	}

}
