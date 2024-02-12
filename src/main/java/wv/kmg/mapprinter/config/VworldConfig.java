package wv.kmg.mapprinter.config;

import java.net.URL;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VworldConfig extends Config {

	public VworldConfig(Properties props) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		
		h.put(props.getProperty("vworld.type"), props.getProperty("vworld.url"));
	}
}
