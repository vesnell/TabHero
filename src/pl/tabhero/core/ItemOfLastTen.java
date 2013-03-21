package pl.tabhero.core;

public class ItemOfLastTen {
    
    private String performer;
    private String title;
    private String tablature;
    private String url;
    private String date;
    private String type;
    
    public ItemOfLastTen(String performer, String title, String tablature, String url, String date, String type) {
        this.setPerformer(performer);
        this.setTitle(title);
        this.setTablature(tablature);
        this.setUrl(url);
        this.setDate(date);
        this.setType(type);
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
