package pl.tabhero.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Tablature {
	public String songTablature;
	public String songTitle;
	public String songUrl;
	
	public Tablature(String title, String url) {	
		this.songTitle = title;
		this.songUrl = url;
	}

	public void setSongTablature(Document docWithTab) {
		Element elements = docWithTab.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++) {
    		tab += table[i] + "\n";
    	}
		this.songTablature = tab;
	}
}
