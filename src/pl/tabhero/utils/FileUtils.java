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
    private String filePath;
    private String fileName;
    private File file;
    private File outDir;
    private String dir;
    private final String config;
    private final String maximize;
    private final String minimize;
    private final String androidDir;
    private final String data;
    private static final int POINTER_ON_PERF = 4;
    private static final int POINTER_ON_TITLE_ID = 5;
    private static final float DEFAULT_SIZE_TEXT = 12f;

    public FileUtils(Context context) {
        File root = Environment.getExternalStorageDirectory();
        this.context = context;
        this.activity = (Activity) context;
        androidDir = this.context.getString(R.string.androidDir);
        data = this.context.getString(R.string.dataDir);
        this.setDir(root.getAbsolutePath() + File.separator + androidDir + File.separator
                + this.context.getPackageName());
        config = this.context.getString(R.string.configNameFile);
        maximize = this.context.getString(R.string.configMAX);
        minimize = this.context.getString(R.string.configMIN);
    }

    public void makePaths(String songUrl) {
        String[] fileTab1 = songUrl.split("/");
        String filePerf = fileTab1[POINTER_ON_PERF];
        String[] fileTab2 = fileTab1[POINTER_ON_TITLE_ID].split(",");
        String fileId = fileTab2[0];
        String fileTitle = fileTab2[1];
        this.setFileName(filePerf + "-" + fileTitle + "." + fileId + ".txt");
        this.setFilePath(getDir() + File.separator + getFileName());
    }

    public void makeFiles() {
        this.setFile(new File(this.getFilePath()));
        this.setOutDir(new File(this.getDir()));
    }

    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
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
        } catch (IOException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    e.getMessage() + this.context.getString(R.string.sdcardReadError),
                    Toast.LENGTH_LONG).show();
        }
        return text.toString();
    }

    public void setIfMax(String configText) {
        String max = configText.split(",")[0];
        if (max.equals(maximize)) {
            this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (max.equals(minimize)) {
            this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            this.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    public void writeForConfig(File file, String maxForConfig, String textSize, String checkBoxResult) throws IOException {
        Writer writer;
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(maxForConfig + "," + textSize + "," + checkBoxResult);
        writer.close();
    }

    public void checkIfMax() {
        MyTelephonyManager device = new MyTelephonyManager(this.context);
        if (!device.isTablet()) {
            File file = new File(this.getDir() + File.separator + config);
            if (file.isFile()) {
                String configText = readConfig(file);
                setIfMax(configText);
            }
        }
    }

    public float setSizeTextAndCheckSDCardReadable() {
        FileUtils fileUtils = new FileUtils(this.context);
        File file = new File(this.getDir() + File.separator + config);
        Float size;
        if (file.isFile()) {
            size = fileUtils.getTabSize();
        } else {
            size = DEFAULT_SIZE_TEXT;
        }
        return size;
    }

    @SuppressLint("UseValueOf")
    public Float getTabSize() {
        File file = new File(this.getDir() + File.separator + config);
        Float size = null;
        if (file.isFile()) {
            String configText = readConfig(file);
            size = new Float(configText.split(",")[1]);
        }
        return size;
    }

    public void setSizeToConfig(float size) throws IOException {
        String maxForConfig;
        String checkBoxResult;
        File file = new File(this.getDir() + File.separator + config);
        if (file.isFile()) {
            String configText = readConfig(file);
            maxForConfig = configText.split(",")[0];
            checkBoxResult = configText.split(",")[2];
            String textSize = Float.toString(size);
            writeForConfig(file, maxForConfig, textSize, checkBoxResult);
        }
    }

    public void saveTabSize(TextView tab) {
        try {
            FileUtils fileUtils = new FileUtils(this.context);
            fileUtils.setSizeToConfig(tab.getTextSize());
        } catch (IOException e) {
            Toast.makeText(this.context.getApplicationContext(),
                    R.string.sdcardWriteError, Toast.LENGTH_LONG).show();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getOutDir() {
        return outDir;
    }

    public void setOutDir(File outDir) {
        this.outDir = outDir;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
