package wv.kmg.mapprinter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class RequestParam {
	protected Map<String,String> m;
	public RequestParam() {
		m = new HashMap<String,String>();
	}
	public void set(String name,String value) {
		m.put(name, value);
	}
	
	public void get(String name) {
		m.get(name);
	}
	public String toParam() {
		
		List<String> l = new LinkedList<String>();
		
		m.forEach((key,value)->{
			l.add(key+"="+value) ;
		});
		return String.join("&", l);
	}
}
