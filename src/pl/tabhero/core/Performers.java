package pl.tabhero.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pl.tabhero.utils.PolishComparator;

public class Performers {
	public String typedName;
	public String url;
	public Map<String, String> chosenPerformersAndUrls;
	public List<String> listOfNames;
	public List<String> listOfUrls;
	
	public Performers(String typedPerformerName) {
		this.typedName = typedPerformerName;
	}
	
	public void setMapOfChosenPerformers(Document doc) {
		Comparator<String> comparator = new PolishComparator();
    	Map<String, String> mapOfChosenPerformers = new TreeMap<String, String>(comparator);
    	String codeFind = doc.select("tr.v0,tr.v1").toString();
    	Document docFind = Jsoup.parse(codeFind);
    	Elements performers = docFind.select("a[href]");
    	
    	for(Element el : performers) {
    		String localUrl = el.attr("href");
    		String localPerformer = Jsoup.parse(el.toString()).select("a").first().ownText().replace("\\", "");
    		if(localPerformer.toLowerCase().contains(this.typedName) == true) {
    			mapOfChosenPerformers.put(localPerformer, localUrl);
    		}
    	}
		this.chosenPerformersAndUrls = mapOfChosenPerformers;
	}
	
	public void setListOfNames() {
		this.listOfNames = new ArrayList<String>(this.chosenPerformersAndUrls.keySet());
	}
	
	public void setListOfUrls() {
		this.listOfUrls = new ArrayList<String>(this.chosenPerformersAndUrls.values());
	}

}
