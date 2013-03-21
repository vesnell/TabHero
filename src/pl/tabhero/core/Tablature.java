package pl.tabhero.core;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Tablature {
    private String songPerformer;
    private String songTablature;
    private String songTitle;
    private String songUrl;
    private static final int POINTER_ON_BEGIN_OF_TAB = 3;

    public Tablature(String title, String url) {
        this.setSongTitle(title);
        this.setSongUrl(url);
    }

    public void setSongTablature(Document docWithTab) {
        Element elements = docWithTab.select("pre").first();
        String tab = elements.text();
        String[] table = tab.split("\n");
        tab = "";
        for (int i = POINTER_ON_BEGIN_OF_TAB; i < table.length; i++) {
            tab += table[i] + "\n";
        }
        this.songTablature = tab;
    }
    
    public String getSongPerformer() {
        return songPerformer;
    }
    
    public void setSongPerformer(String songPerformer) {
        this.songPerformer = songPerformer;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getSongTablature() {
        return songTablature;
    }

    public void setSongTablature(String songTablature) {
        this.songTablature = songTablature;
    }
}
