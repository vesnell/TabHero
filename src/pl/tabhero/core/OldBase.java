package pl.tabhero.core;

public class OldBase {
    
    private String performer;
    private String title;
    private String tablature;
    private String url;
    
    public OldBase(String performer, String title, String tablature, String url) {
        this.setPerformer(performer);
        this.setTitle(title);
        this.setTablature(tablature);
        this.setUrl(url);
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTablature() {
        return tablature;
    }

    public void setTablature(String tablature) {
        this.tablature = tablature;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
