package pl.tabhero.core;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Songs {
	public String typedTitle;
	public String title;
	public String performerUrl;
	public Map<String, String> mapOfChosenUrlsAndTitles;
	public List<String> listOfTitles;
	public List<String> listOfSongUrls;
	
	private String chordsUrl = "http://www.chords.pl";

	public Songs(String typedTitle, String performerUrl) {
		this.typedTitle = typedTitle;
		this.performerUrl = performerUrl;
	}
	
	public void setMapOfChosenTitles(Document doc) {
		String codeSongs = doc.select("table.piosenki").toString();
    	Document songs = Jsoup.parse(codeSongs);
    	Elements chosenLineSong = songs.select("a[href]");
    	
    	Map<String, String> unsortedChosenTitles = new TreeMap<String, String>();
    	for(Element el : chosenLineSong) {
    		String localUrl = el.attr("href");
    		localUrl = chordsUrl + localUrl;
    		String localTitle = Jsoup.parse(el.toString()).select("a").first().ownText().replace("\\", "");
    		if(localTitle.toLowerCase().contains(this.typedTitle) == true) {
    			unsortedChosenTitles.put(localUrl, localTitle);
    		}
    	}
    	Map<String, String> sortedMapOfChosenTitles = sortMapByValue(unsortedChosenTitles);
    	this.mapOfChosenUrlsAndTitles = sortedMapOfChosenTitles;
	}
	
	public void setListOfTitles() {
		this.listOfTitles = new ArrayList<String>(this.mapOfChosenUrlsAndTitles.values());
	}
	
	public void setListOfUrls() {
		this.listOfSongUrls = new ArrayList<String>(this.mapOfChosenUrlsAndTitles.keySet());
	}
	
	private Map<String, String> sortMapByValue(Map<String, String> unsortedMap) {
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(unsortedMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, String>>() {

			@Override
			public int compare(Entry<String, String> lhs, Entry<String, String> rhs) {
				Collator c = Collator.getInstance(new Locale("pl", "PL"));
				return  c.compare(((Entry<String, String>) (lhs)).getValue(), ((Entry<String, String>) (rhs)).getValue());
			}
		
		}); 
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
