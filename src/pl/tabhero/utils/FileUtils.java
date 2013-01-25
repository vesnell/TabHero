package pl.tabhero.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import pl.tabhero.R;
import pl.tabhero.db.DBUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class FileUtils { 
	
	private Context context;
	private Activity activity;
	public String filePath;
	public String fileName;
	public File file;
	public File outDir;
	public String dir;
	private static final String CONFIG = "config.txt";
	
	public FileUtils(Context context) {
		File root = Environment.getExternalStorageDirectory();
		this.context = context;
		this.activity = (Activity) context;
		this.dir = root.getAbsolutePath() + File.separator + "Android" + File.separator + this.context.getPackageName();
	}
	
	public void makePaths(String songUrl) {
		String[] fileTab1 = songUrl.split("/");
		String filePerf = fileTab1[4];
		String[] fileTab2 = fileTab1[5].split(",");
		String fileId = fileTab2[0];
		String fileTitle = fileTab2[1];
	    this.fileName = filePerf + "-" + fileTitle + "." + fileId + ".txt";
	    this.filePath = dir + File.separator + fileName;
	}
	
	public void makeFiles() {
		this.file = new File(this.filePath);
	    this.outDir = new File(this.dir);
	}
	
	public static String readFile(String path) throws IOException {
		  FileInputStream stream = new FileInputStream(new File(path));
		  try {
		    FileChannel fc = stream.getChannel();
		    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		    return Charset.defaultCharset().decode(bb).toString();
		  }
		  finally {
		    stream.close();
		  }
		}
	
	public void updateTablatureFU(String tablature, String songUrl, File file, String fileName) throws IOException {
		String tabFromFile = readFile(fileName);
		DBUtils dbUtils = new DBUtils(this.context);
		dbUtils.updateTablatureDBU(songUrl, tabFromFile);
		tablature = tabFromFile;
		file.delete();
	}
	
	@SuppressWarnings("resource")
	public String readConfig(File file) {
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		}
		catch (IOException e) {
			Toast.makeText(this.context.getApplicationContext(), e.getMessage() + this.context.getString(R.string.sdcardReadError), Toast.LENGTH_LONG).show();
		}
		return text.toString();
	}
	
	public void setIfMax(String configText) {
		String max = configText.split(",")[0];
		if(max.equals("MAX")) {
			this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else if(max.equals("MIN")) {
			this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    	this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}
	
	public void writeForConfig(File file, String maxForConfig, String textSize) throws IOException {
		Writer writer;
		writer = new BufferedWriter(new FileWriter(file));
		writer.write(maxForConfig + "," + textSize);
		writer.close();
	}
	
	public void checkIfMax() {
		MyTelephonyManager device = new MyTelephonyManager(this.context);
		if(!device.isTablet()) {
			File file = new File(this.dir + File.separator + CONFIG);
			if(file.isFile()) {
				String configText = readConfig(file);
				setIfMax(configText);
			}
		}
	}
	
	public float setSizeTextAndCheckSDCardReadable() {
		FileUtils fileUtils = new FileUtils(this.context);
		File file = new File(this.dir + File.separator + CONFIG);
		Float size;
		if(file.isFile()) {
			size = fileUtils.getTabSize();
		} else {
			size = 12f;
		}
		return size;
	}
	
	@SuppressLint("UseValueOf")
	public Float getTabSize() {
		File file = new File(this.dir + File.separator + CONFIG);
		Float size = null;
		if(file.isFile()) {
			String configText = readConfig(file);
			size = new Float(configText.split(",")[1]);
		}
		return size;
	}
	
	public void setSizeToConfig(float size) throws IOException {
		String maxForConfig;
		File file = new File(this.dir + File.separator + CONFIG);
		if(file.isFile()) {
			String configText = readConfig(file);
			maxForConfig = configText.split(",")[0];
			String textSize = Float.toString(size);
			writeForConfig(file, maxForConfig, textSize);
		}
	}
	
	public void saveTabSize(TextView tab) {
		try {
			FileUtils fileUtils = new FileUtils(this.context);
			fileUtils.setSizeToConfig(tab.getTextSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
