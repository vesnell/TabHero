package pl.tabhero;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
 
public class SearchActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
    }
    
    public void searchView(View v) throws IOException {
    	//String url = "http://www.chords.pl/chwyty/metallica/17824,fight_fire_with_fire";
    	String url = "http://www.chords.pl/chwyty/";
		String performer = new String();
		String title = new String();
		performer = ((EditText) findViewById(R.id.editPerformer)).getText().toString().toLowerCase();
		title = ((EditText) findViewById(R.id.editTitle)).getText().toString().toLowerCase();
		
		String urlPerformerSongs = url + performer;
		/*BufferedReader bufferedReader = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(urlPerformerSongs);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("fband_name", performer));
        postParameters.add(new BasicNameValuePair("ftitle", title));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
        request.setEntity(entity);
        HttpResponse response= httpClient.execute(request);
        bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer stringBuffer = new StringBuffer("");
        String line = "";
        String LineSeparator = System.getProperty("line.separator");
        while ((line = bufferedReader.readLine()) != null) {
        	stringBuffer.append(line + LineSeparator); 
        }
        bufferedReader.close();
        String tablature = stringBuffer.toString();
        if (bufferedReader != null)
        	bufferedReader.close();*/
        
		//String tablature = getTablature(url);
		String tablature = split(urlPerformerSongs, title);
		Intent i = new Intent(SearchActivity.this, TabViewActivity.class);
		Bundle bun = new Bundle();
		bun.putString("performer", performer);
		bun.putString("title", title);
		bun.putString("tab", tablature);
		i.putExtras(bun);
		startActivity(i);
		//startActivityForResult(i, 500);
		//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    private String split(String urlPerformerSongs, String title) throws IOException {
    	String url = "http://www.chords.pl";
    	Document doc = Jsoup.connect(urlPerformerSongs).get();
    	String codeSongs = doc.select("table.piosenki").toString();
    	Document songs = Jsoup.parse(codeSongs);
    	Elements chosenLineSong = songs.select("a[href]");
    	String[][] array = new String[chosenLineSong.size()][2];
    	int i = 0;
    	Log.d("BBBBBBB", title);
    	for(Element element : chosenLineSong) {
    		//tab += element.attr("href") + " <---> " + element.text() +"\n";
    		array[i][0] = element.attr("href");
    		array[i][1] = element.toString().toLowerCase();
    		array[i][1] = array[i][1].toLowerCase();
    		String e = Jsoup.parse(array[i][1]).select("a").first().ownText();
    		//Log.d("AAAAAAAAAAAA", element.text());
    		Log.d("EEEEEEEEEE", e);
    		int comparison = e.compareTo(title);
			//Log.d("CCCCCCCCCC", array[i][1]);
    		if(comparison == 0) {
    			break;
    		}
    		else
    			i++;
    	}
    	String tab = getTablature(url + array[i][0]);
    	return tab;
    }
    
    private String getTablature(String url) throws IOException {
    	Document doc = Jsoup.connect(url).get();
    	Element elements = doc.select("pre").first();
    	String tab = elements.text();
    	String[] table = tab.split("\n");
    	tab = "";
    	for (int i = 3; i < table.length; i++)
    		tab += table[i] + "\n";
    	return tab;
    }
    
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    //}
}