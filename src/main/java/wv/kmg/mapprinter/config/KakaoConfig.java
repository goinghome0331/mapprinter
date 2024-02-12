package wv.kmg.mapprinter.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KakaoConfig extends Config {
	public KakaoConfig(Properties props) throws Exception {
		super();
		String config = getRequest(new URL(props.getProperty("kakao.info")));
		Pattern p = Pattern.compile("URI_FUNC[\\W\\w]*(?=\\,t\\.VERSION)");
		Matcher m = p.matcher(config);
		StringBuilder sb = new StringBuilder();
		while(m.find()) {
			sb.append(m.group());
		}
		
		p = Pattern.compile("[A-Z_]*:[a-z(){},\"./_0-9A-Z]+");
		m = p.matcher(sb.toString());
		sb.setLength(0);
		while(m.find()) {
			String[] kv= m.group().split(":");
//			System.out.println(kv[0]);
//			System.out.println(kv[1]);
			p = Pattern.compile("\"[a-zA-Z0-9./_]+\"");
			Matcher tm = p.matcher(kv[1]);
			while(tm.find()) {
				String url = tm.group();
				h.put(kv[0], "http://"+url.substring(1, url.length()-1));
			}
		}
	}
	
}
