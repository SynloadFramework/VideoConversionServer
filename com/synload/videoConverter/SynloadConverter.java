package com.synload.videoConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import com.synload.videoConverter.converter.Converter;
import com.synload.videoConverter.converter.Video;
import com.synload.videoConverter.http.HTTPServer;
import com.synload.videoConverter.uploader.Uploader;

public class SynloadConverter{
	public SynloadConverter(){}
	public static HTTPServer server = null;
	public static Converter worker = null;
	public static Uploader uploader = null;
	private static String OS = System.getProperty("os.name").toLowerCase();
	public static Properties prop = new Properties();
	public static Hashtable<String,Video> uploadQueue = new Hashtable<String,Video>();
	public static Hashtable<String,Video> history = new Hashtable<String,Video>();
	public static HashMap<String,Object> current = new HashMap<String,Object>();
	public static void onStart() throws FileNotFoundException, IOException{
		if((new File("config.ini")).exists()){
			prop.load(new FileInputStream("config.ini"));
		}else{
			if(OS.indexOf("win") >= 0){
				prop.setProperty("os", "Windows");
				prop.setProperty("ffmpeg", "ffmpeg.exe");
				prop.setProperty("mkvextract", "mkvextract.exe");
				prop.setProperty("mkvmerge", "mkvmerge.exe");
			}else{
				prop.setProperty("os", "Linux");
				prop.setProperty("ffmpeg", "ffmpeg");
				prop.setProperty("mkvextract", "mkvextract");
				prop.setProperty("mkvmerge", "mkvmerge");
			}
			prop.setProperty("customPath", "./custom/");
			prop.setProperty("videoPath", "./tmp/");
			prop.setProperty("userName", "derp");
			prop.setProperty("userPass", "password123");
			prop.store(new FileOutputStream("config.ini"), null);
		}
	}
	public static void main(String[] args) {
		try {
			onStart();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			(worker = new Converter()).start();
			(new Thread(uploader = new Uploader())).start();
			int port = 2023;
			if(args.length>=1){
				port = Integer.valueOf(args[0]);
			}
			System.out.println("Started server at http://0.0.0.0:"+port);
			server = new HTTPServer(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}