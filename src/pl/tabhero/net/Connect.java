package pl.tabhero.net;

import java.io.IOException;
import java.net.MalformedURLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Connect {

    private Document doc;
    private boolean errorConnection;

    public Connect() {
        this.setErrorConnection(false);
    }

    public Document tryEnable(String url) {
        this.doc = null;
        try {
            this.doc = Jsoup.connect(url).get();
            this.setErrorConnection(false);
        } catch (MalformedURLException ep) {
            this.setErrorConnection(true);
        } catch (IOException e) {
            this.setErrorConnection(true);
        }
        return this.doc;
    }

    public boolean isErrorConnection() {
        return errorConnection;
    }

    public void setErrorConnection(boolean errorConnection) {
        this.errorConnection = errorConnection;
    }
}
