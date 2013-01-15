package pl.tabhero.net;

import java.io.IOException;
import java.net.MalformedURLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Connect {
	
	public Document doc;
	public boolean errorConnection;

	public Connect() {
		this.errorConnection = false;
	}
	
	public Document tryEnable(String url) {
		this.doc = null;
	    try {
			this.doc = Jsoup.connect(url).get();
			this.errorConnection = false;
		} catch (MalformedURLException ep) {
			this.errorConnection = true;
		} catch (IOException e) {
			this.errorConnection = true;
		}
	    return this.doc;
	}
}
