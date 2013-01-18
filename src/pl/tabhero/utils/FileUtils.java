package pl.tabhero.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import pl.tabhero.db.DBUtils;

import android.content.Context;
import android.os.Environment;

public class FileUtils { 
	
	private Context context;
	public String filePath;
	public String fileName;
	public File file;
	public File outDir;
	
	public FileUtils(Context context) {
		this.context = context;
	}
	
	public void makeFiles(String songUrl) {
		File root = Environment.getExternalStorageDirectory();
		String[] fileTab1 = songUrl.split("/");
		String filePerf = fileTab1[4];
		String[] fileTab2 = fileTab1[5].split(",");
		String fileId = fileTab2[0];
		String fileTitle = fileTab2[1];
		String dir = root.getAbsolutePath() + File.separator + "Android" + File.separator + this.context.getPackageName();
	    this.fileName = filePerf + "-" + fileTitle + "." + fileId + ".txt";
	    this.filePath = dir + File.separator + fileName;
	    this.file = new File(this.filePath);
	    this.outDir = new File(dir);
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
}
